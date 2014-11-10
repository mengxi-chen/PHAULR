package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import messaging.message.IPMessage;
import messaging.message.MessageGid;
import messaging.message.QueryMessage;
import messaging.message.UpdateMessage;

import org.apache.log4j.Logger;

import storage.datastructure.MultipartTimestamp;
import application.message.QueryAckMessage;
import application.message.UpdateAckMessage;

import communication.Address;
import communication.CommunicationService;
import communication.Configuration;

/**
 * Front end (i.e., a proxy) of the {@link Client}.
 * It maintains the {@link UpdateMessage}s that it has seen so far.
 * It sends and receives messages on behalf of its client.
 *
 * @date 2014-10-13
 */
public class FrontEnd
{
	Logger logger = Logger.getLogger(FrontEnd.class.getName());
	
	// my own address
	private Address addr;

	// default replica to which I will issue my requests
	private Address default_contact_replica_addr;

	// for {@link UpdateMessageId}
	private static int seqno = 0;

	/**
	 * maintains all the messages that have been issued but have not yet received acks
	 *
	 * <b>note:</b> A {@link LinkedHashMap} keeps the keys in the order they were inserted.
	 */
	private Map<MessageGid, IPMessage> msg_waiting_queue = new LinkedHashMap<>();

	/**
	 * maintains all the <b>already executed</b> messages
	 * (including both {@link UpdateMessage} and {@link QueryMessage})
	 * that it has seen so far.
	 *
	 * Use ConcurrentHashMap to resolve the java.util.ConcurrentModificationException
	 * See http://stackoverflow.com/q/26621907/1833118
	 * 
	 * FIXME: they are not necessarily executed!
	 */
	private ConcurrentHashMap<MessageGid, MultipartTimestamp> mid_ts_map = new ConcurrentHashMap<>();

	/**
	 * to iterate over {@link #msg_waiting_queue} and send the requests that have timed out
	 */
	private static final Executor exec = Executors.newCachedThreadPool();
	private static final int TIMEOUT = 3000;	// milliseconds
	private Runnable resend_msg_daemon = new Runnable()
	{
		@Override
		public void run()
		{
			/**
			 * Avoiding iterating over #msg_waiting_queue directly through its iterator.
			 * Otherwise, you will cause the {@link ConcurrentModificationException}.
			 *
			 * See stackoverflow.com/questions/17016747/linkedhashmap-concurrentmodificationexception-error
			 */
			int size = msg_waiting_queue.size();
			IPMessage[] msgs = (IPMessage[]) msg_waiting_queue.entrySet().toArray(new IPMessage[size]);
			for (IPMessage msg : msgs)
			{
				// re-send message to another randomly chosen replica
				Address rand_replica_addr = Configuration.INSTANCE.getRandomReplicaOtherThan(default_contact_replica_addr);
				CommunicationService.INSTACNE.sendMsg(rand_replica_addr, msg);
			}

			try
			{
				Thread.sleep(TIMEOUT);
			} catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
	};

	public FrontEnd()
	{
//		Configuration.INSTANCE.configSystem();
		this.configFrontEnd();
		FrontEnd.exec.execute(this.resend_msg_daemon);
	}

	public void start()
	{
		CommunicationService.INSTACNE.start2Listen(this.addr);
		System.out.println("Start a client (and its front end) and listen to : " + this.addr.toString());
	}

	/**
	 * Issue an update request:
	 * The front end sends an {@link UpdateMessage} to its default contact replica
	 * and will send duplicates to other replicas if it times out
	 *
	 * <b>Note:</b> The update requests are not delayed.
	 */
	public void issueUpdateRequest(Set<MessageGid> deps, MultipartTimestamp prev, String op)
	{
		MessageGid umid = new MessageGid(this.addr, seqno++);
		IPMessage update_msg = new UpdateMessage(this.addr, deps, prev, op, umid);
		update_msg.setIssueTime(System.currentTimeMillis());
		
		logger.info(update_msg.getMsgGid() + "\t Issue Time \t" + update_msg.getIssueTime());
		
		CommunicationService.INSTACNE.sendMsg(this.default_contact_replica_addr, update_msg);

		this.msg_waiting_queue.put(umid, update_msg);
	}

	/**
	 * Issue a query request:
	 * The front end sends an {@link QueryMessage} to its default contact replica
	 * and will send duplicates to other replicas if it times out
	 *
	 * <b>Note:</b> The query requests may be delayed.
	 */
	public void issueQueryRequest(Set<MessageGid> deps, MultipartTimestamp prev, String op)
	{
		MessageGid qmid = new MessageGid(this.addr, seqno++);
		QueryMessage query_msg = new QueryMessage(this.addr, deps, prev, op, qmid);
		query_msg.setIssueTime(System.currentTimeMillis());
		
		logger.info(query_msg.getMsgGid() + "\t Issue Time \t" + query_msg.getIssueTime());

		CommunicationService.INSTACNE.sendMsg(this.default_contact_replica_addr, query_msg);

		this.msg_waiting_queue.put(qmid, query_msg);
	}

	/**
	 * Process the received {@link UpdateAckMessage}:
	 *
	 * @param update_ack_msg
	 */
	public void processUpdateAckMessage(UpdateAckMessage update_ack_msg)
	{
		update_ack_msg.setAckTime(System.currentTimeMillis());
		
		MessageGid umid = update_ack_msg.getUmid();

		logger.info(umid + "\t Ack Time \t" + update_ack_msg.getAckTime());

		this.msg_waiting_queue.remove(umid);

		this.mid_ts_map.put(umid, update_ack_msg.getUpdateTs());
	}

	public void processQueryAckMessage(QueryAckMessage query_ack_msg)
	{
		query_ack_msg.setAckTime(System.currentTimeMillis());
		
		MessageGid qmid = query_ack_msg.getQmid();

		logger.info(qmid + "\t Ack Time \t" + query_ack_msg.getAckTime());

		this.msg_waiting_queue.remove(qmid);

		this.mid_ts_map.put(qmid, query_ack_msg.getQueryResultTs());
	}

	/**
	 * Randomly generate "prev" (deps) parameter for requests
	 * @return an array of {@link MessageGid}
	 */
	public Set<MessageGid> generateRandomDeps()
	{
		Random rand = new Random();

		MessageGid[] msg_gid_array = (MessageGid[]) this.mid_ts_map.keySet().toArray(new MessageGid[mid_ts_map.size()]);
		int size = msg_gid_array.length;

		HashSet<MessageGid> msg_gid_set = new HashSet<>();
		if (size > 0)
		{
			int start = rand.nextInt(size);
			int end = rand.nextInt(size);
			if (start < end)
				for (int index = start; index < end; index++) 
					msg_gid_set.add(msg_gid_array[index]);
			else
				for (int index = end; index < start; index++)
					msg_gid_set.add(msg_gid_array[index]);
		}
		
		return msg_gid_set;
	}

	/**
	 * generate the "prev" label 
	 * @param msg_gid_array
	 * @return
	 */
	public MultipartTimestamp generateMpts(Set<MessageGid> msg_gid_array)
	{
		MultipartTimestamp mpts = new MultipartTimestamp();
		
		for (MessageGid msg_gid : msg_gid_array) 
			mpts.merge(this.mid_ts_map.get(msg_gid));
		
		return mpts;
	}
	
	/**
	 * Configure the front end:
	 * parse the "config_client" file to
	 * (1) set its ip and port
	 * (2) set its default contact replica
	 */
	private void configFrontEnd()
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(new File(Configuration.CONFIG_CLIENT_FILE)));

			// (1) its own address: ip \t port \n
			String addr = br.readLine();
			String[] addr_fields = addr.split(Configuration.PARSE_SPLIT);

			String ip = String.valueOf(addr_fields[0]);
			int port = Integer.parseInt(addr_fields[1]);
			this.addr = new Address(ip, port);

			// (2) the address of its default contact replica: ip \t port \n
			addr = br.readLine();
			addr_fields = addr.split(Configuration.PARSE_SPLIT);

			ip = String.valueOf(addr_fields[0]);
			port = Integer.parseInt(addr_fields[1]);
			this.default_contact_replica_addr = new Address(ip, port);
		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
}
