package storage;

//import com.objectspace.jgl.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import messaging.message.MessageGid;
import messaging.message.QueryMessage;
import messaging.message.UpdateMessage;
import storage.datastructure.InVal;
import storage.datastructure.Logs;
import storage.datastructure.MultipartTimestamp;
import storage.datastructure.TsTable;
import util.log4j.TimeLogger;

import communication.Address;
import communication.Broadcast;
import communication.CommunicationService;
import communication.Configuration;

public enum Replica
{
	INSTANCE;

	private Address addr;

	// replica's identifier
	private int rid;

	// replica's {@link MultipartTimestamp}
	private MultipartTimestamp rep_ts = null;

	private TsTable ts_table = null;

	// replica's timestamp associated with val
	private MultipartTimestamp val_ts = null;
	private InVal inval = new InVal();

	// queue consisting of {@link QueryMessage}s that wait to be processed
	private HashMap<MessageGid, QueryMessage> query_msg_waiting_queue = new HashMap<>();
	// queue consisting of {@link UpdateMessage} that wait to be processed
	private HashMap<MessageGid, UpdateMessage> update_msg_waiting_queue = new HashMap<>();

	private Logs log = new Logs();

	/**
	 * Configure this replica and initialize its state.
	 */
	private Replica()
	{
		this.configReplica();

		/**
		 * <b>Note:</b> these two {@link MultipartTimestamp}s should be
		 * initialized <i>after</i> <code>Configuration.INSTANCE.configSystem();</code>
		 * has been called.
		 * The latter will set the Configuration#REPLICA_NUM to the right number.
		 */
		this.rep_ts = new MultipartTimestamp();
		this.val_ts = new MultipartTimestamp();

		this.ts_table = new TsTable();
	}

	/**
	 * Configure this replica with its ip, port, and identifier.
	 */
	private void configReplica()
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(new File(Configuration.CONFIG_REPLICA_FILE)));

			// parse the "config_replica" file
			String config = br.readLine();

			String[] config_fields = config.split(Configuration.PARSE_SPLIT);
			String ip = String.valueOf(config_fields[0]);
			int port = Integer.parseInt(config_fields[1]);
			this.addr = new Address(ip, port);

			this.rid = Integer.parseInt(config_fields[2]);
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

	/**
	 * Start the replica as a server and wait for messages from other replicas and clients
	 */
	public void start()
	{
		CommunicationService.INSTACNE.start2Listen(this.addr);
		System.out.println("Start a replica and listen to : " + this.addr.toString());
	}

	public void startBroadcastService()
	{
		new Broadcast(this.addr).startBroadcast(Configuration.INITIAL_DELAY, Configuration.DELAY);
	}

	public Address getAddress()
	{
		return this.addr;
	}

	public int getRid()
	{
		return this.rid;
	}

	public MultipartTimestamp getRepTs()
	{
		return rep_ts;
	}

	public MultipartTimestamp getValTs(){
		return this.val_ts;
	}

	public TsTable getTsTable()
	{
		return this.ts_table;
	}

	public Logs getLogs(){
		return this.log;
	}

	public void resetLogs()
	{
		this.log = new Logs();
	}

	public InVal getInval(){
		return inval;
	}

	public HashMap<MessageGid, QueryMessage> getQueryMessageWaitingQueue()
	{
		return this.query_msg_waiting_queue;
	}

	/**
	 * Put the {@link QueryMessage} into waiting queue
	 * @param query_msg {@link QueryMessage} waits to be processed
	 */
	public void delayQueryMessage(QueryMessage query_msg)
	{
		this.query_msg_waiting_queue.put(query_msg.getMsgGid(), query_msg);
	}

	public HashMap<MessageGid, UpdateMessage> getUpdateMessageWaitingQueue()
	{
		return this.update_msg_waiting_queue;
	}
	
	public void delayUpdateMessage(UpdateMessage update_msg)
	{
		this.update_msg_waiting_queue.put(update_msg.getMsgGid(), update_msg);
	}
	
	public void removeUpdateMessage(UpdateMessage update_msg)
	{
		this.update_msg_waiting_queue.remove(update_msg.getMsgGid());
	}
	
	/**
	 * The message with this specified {@link MessageGid} has been executed.
	 * Remove deps on it.
	 * 
	 * @param msg_id
	 */
	public void removeDepsOn(MessageGid msg_id)
	{
		// from update message waiting queue
		for (UpdateMessage update_msg : this.update_msg_waiting_queue.values())
		{
			if (update_msg.deleteDep(msg_id))
				TimeLogger.recordDepTime(update_msg);
		}
		
		// from query message waiting queue
		for (QueryMessage query_msg : this.query_msg_waiting_queue.values())
		{
			if (query_msg.deleteDep(msg_id))
				TimeLogger.recordDepTime(query_msg);
		}
	}
	
	public static void main(String[] args)
	{
		Configuration.INSTANCE.configSystem();
		Replica.INSTANCE.start();
	}
}
