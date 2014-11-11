package messaging.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import messaging.message.GossipMessage;
import messaging.message.IPMessage;
import messaging.message.MessageGid;
import messaging.message.QueryMessage;
import messaging.message.UpdateMessage;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.graph.DefaultEdge;

import storage.Replica;
import storage.datastructure.LogRecord;
import storage.datastructure.Logs;
import storage.datastructure.MultipartTimestamp;

public class GossipMessageHandler implements IMessageHandler
{
	@Override
	public boolean handleMessage(IPMessage msg)
	{
		GossipMessage gossip_msg = (GossipMessage) msg;

		System.out.println("------------------Receive GossipMessage-" + gossip_msg.toString() + " -------------");

		/*
		 *	Replica i discards gossip message m from replica j if m.ts <= j's timestamp in ts_table
		 */
		int gossip_sender_replica = gossip_msg.getRid();

		MultipartTimestamp gossip_msg_ts = gossip_msg.getReplicaTs();
		MultipartTimestamp gossip_sender_latest_rep_ts = new MultipartTimestamp(
				Replica.INSTANCE.getTsTable().getReplicaTs(gossip_msg.getRid()));

		if(gossip_msg_ts.compareTo(gossip_sender_latest_rep_ts) <= 0)
			return true;

		/*
		 * (1) Add the new information in the gossip message to the replica's log
		 */
		Logs replica_logs = Replica.INSTANCE.getLogs();
		Logs gossip_logs = gossip_msg.getLogs();
		MultipartTimestamp rep_ts = Replica.INSTANCE.getRepTs();
		replica_logs.merge(gossip_logs, rep_ts);

		/*
		 * (2) Merge the replica's timestamp with the timestamp in the message
		 */
		rep_ts.merge(gossip_msg_ts);

		/*
		 * (3) Insert all the update records that are ready to be added to the value into the set comp
		*/
		HashMap<MessageGid, LogRecord> comp = this.constructComputationSet();

		/*
		 * (4) Compute the new value of the object
		 */
		this.compute(comp);

		/**
		 * (5) Update ts_table
		 */
		Replica.INSTANCE.getTsTable().setReplicaTs(gossip_sender_replica, gossip_msg_ts);

		/**
		 * (6) Discards update records from the log if they have been received by all replicas
		 */
		LogRecord log_record = null;

		for (Iterator<Map.Entry<MessageGid, LogRecord>> iter = replica_logs.get_log_map().entrySet().iterator();
				iter.hasNext(); )
		{
			log_record = iter.next().getValue();
			if (Replica.INSTANCE.getTsTable().isKnown(log_record))
				iter.remove();
		}

		/**
		 * (7) Added: processing the blocked {@link QueryMessage}s
		 * @see {@link QueryMessageHandler}
		 */
		QueryMessage query_msg = null;
		for (Iterator<Map.Entry<MessageGid, QueryMessage>> iter = Replica.INSTANCE.getQueryMessageWaitingQueue().entrySet().iterator();
				iter.hasNext(); )
		{
			query_msg = iter.next().getValue();
			if (new QueryMessageHandler().handleMessage(query_msg))
			{
				// TODO: delete dependency on it
				Replica.INSTANCE.removeDepsOn(query_msg.getMsgGid());
				
				// this {@link QueryMessage} has been executed.
				iter.remove();
			}
		}
		
		return true;
	}

	/**
	 * comp = {r \in log | type(r) = update /\ r.prev <= rep_ts}
	 * @return "comp" set consisting of all the update records that are ready to be added to the value
	 */
	public HashMap<MessageGid, LogRecord> constructComputationSet()
	{
		HashMap<MessageGid, LogRecord> comp = new HashMap<>();

        LogRecord log_record = null;
        UpdateMessage update_msg = null;
        MultipartTimestamp prev = null;

        for(Map.Entry<MessageGid, LogRecord> umid_logrecord_entry : (Replica.INSTANCE.getLogs().get_log_map().entrySet()))
        {
        	log_record = umid_logrecord_entry.getValue();
        	update_msg = log_record.getUpdateMessage();
        	prev = update_msg.getPrev();

        	// prev <= rep_ts
        	if(prev.compareTo(Replica.INSTANCE.getRepTs()) <= 0)
        		comp.put(umid_logrecord_entry.getKey(), log_record);
        }

        return comp;
	}

	/**
	 * Computes the new value by applying the records in @para comp in topological order
	 * @param comp
	 */
	public void compute(HashMap<MessageGid, LogRecord> comp)
	{
		DirectedAcyclicGraph<LogRecord, DefaultEdge> comp_dag = new DirectedAcyclicGraph<>(DefaultEdge.class);

		LogRecord outer_log_record = null;
		LogRecord inner_log_record = null;

		/**
		 * Check each pair of log records in comp and construct a dag for them
		 */
		for (Map.Entry<MessageGid, LogRecord> outer_mid_logrecord_entry : comp.entrySet())
		{
			outer_log_record = outer_mid_logrecord_entry.getValue();

			for (Map.Entry<MessageGid, LogRecord> inner_mid_logrecord_entry: comp.entrySet())
			{
				inner_log_record = inner_mid_logrecord_entry.getValue();

				if (outer_log_record != inner_log_record
						&& outer_log_record.getTs().compareTo(inner_log_record.getPrev()) <= 0)
					try
					{
						comp_dag.addDagEdge(outer_log_record, inner_log_record);
					} catch (CycleFoundException cfe)
					{
						cfe.printStackTrace();
					}
			}
		}

		/**
		 * Apply all the {@link UpdateMessage} in topological order such that
		 * record r is earlier than record s if r.ts <= s.prev.
		 */
		Iterator<LogRecord> comp_iter = comp_dag.iterator();
		LogRecord log_record = null;
		MessageGid umid = null;

		while (comp_iter.hasNext())
		{
			log_record = (LogRecord) comp_iter.next();
			umid = log_record.getUmid();

			if (! Replica.INSTANCE.getInval().contains(umid))
			{
				System.out.println("----- Applying log record: " + log_record.toString() + " -----");
				// val = apply(val, r.op);
				Replica.INSTANCE.getInval().addUmid(umid);
				
				// delete from waiting queue
				Replica.INSTANCE.removeUpdateMessage(log_record.getUpdateMessage());
				
				// TODO: for experiment: delete deps
				Replica.INSTANCE.removeDepsOn(umid);
			}

			Replica.INSTANCE.getValTs().merge(log_record.getTs());
		}
	}
}
