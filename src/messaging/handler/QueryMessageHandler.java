package messaging.handler;

import messaging.message.IPMessage;
import messaging.message.QueryMessage;
import storage.Replica;
import storage.datastructure.MultipartTimestamp;
import util.log4j.TimeLogger;
import application.message.QueryAckMessage;

import communication.CommunicationService;

public class QueryMessageHandler implements IMessageHandler 
{
	/**
	 * @return <code>true</code> if this {@link QueryMessage} has been executed;
	 * 	<code>false</code>, otherwise.
	 */
	@Override
	public boolean handleMessage(IPMessage msg)
	{
		TimeLogger.recordEventualTime(msg);

		QueryMessage query_msg = (QueryMessage) msg;

		System.out.println("----- Receive (& Handle) QueryMessage" + query_msg.toString() + " -----");

		MultipartTimestamp prev = query_msg.getPrev();
		MultipartTimestamp val_ts = Replica.INSTANCE.getValTs();

		/**
		 * Process the {@link QueryMessage} if possible
		 */
		if(prev.compareTo(val_ts) <= 0)
		{
			/*
			 * apply q.op to val and return result and val_ts
			 */
			//				val = apply(val, query_msg.get_op());

			QueryAckMessage query_ack_msg = new QueryAckMessage(query_msg.getMsgGid(), val_ts);
			CommunicationService.INSTACNE.sendMsg(msg.getSenderAddr(), query_ack_msg);
			
			// for experiment: (1) delete deps from other messages
			
			// for experiment: (2) record the "causal time"
			TimeLogger.recordCausalTime(query_ack_msg);
			
			return true;
		}
		else
		{
			/**
			 * Delay the {@link QueryMessage}.
			 * It will be processed on receiving enough information from other replicas via {@link GossipMessage}.
			 * @see {@link GossipMessageHandler}
			 */
			Replica.INSTANCE.delayQueryMessage(query_msg);
			return false;
		}
	}

}
