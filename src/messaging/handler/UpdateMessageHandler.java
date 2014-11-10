package messaging.handler;

import org.apache.log4j.Logger;

import messaging.message.IPMessage;
import messaging.message.UpdateMessage;
import storage.Replica;
import storage.datastructure.LogRecord;
import storage.datastructure.MultipartTimestamp;
import application.message.UpdateAckMessage;
import communication.CommunicationService;

public class UpdateMessageHandler implements IMessageHandler
{
	private static Logger logger = Logger.getLogger(UpdateMessageHandler.class.getName());

	public void handleMessage(IPMessage msg)
	{
		msg.setEventualTime(System.currentTimeMillis());
		logger.info(msg.getMsgGid() + "\t Eventual Time \t" + msg.getEventualTime());
		
		UpdateMessage update_msg = (UpdateMessage) msg;
		
		System.out.println("----- Receive an UpdateMessage: " + update_msg.toString() + " -----");
//		logger.info("----- Receive an UpdateMessage: " + update_msg.toString() + " -----");

		int rid = Replica.INSTANCE.getRid();

		/**
		 * (1) Advance {@link #rep_ts} by incrementing its i-th part by one
		 * while leaving all other parts unchanged.
		 */
		this.advanceRepTs(rid);

		/**
		 * (2) Computes the timestamp for the update, update_ts, by replacing the i-th part of
		 * the input argument u.prev with the i-th part of rep_ts.
		 */
		MultipartTimestamp prev = update_msg.getPrev();
		MultipartTimestamp update_ts = this.computeUid(prev, rid);
		update_msg.setUpdateTs(update_ts);

		/**
		 * (3) Constructs the update record associated with this execution of the update
		 * and adds it to the local log.
		 */
		LogRecord record = new LogRecord(update_msg, rid);
		Replica.INSTANCE.getLogs().addLogRecord(record);

		/**
		 * (4) Executes u.op if all the updates that u depends on have already been
		 * incorporated into val.
		 */
		MultipartTimestamp val_ts = Replica.INSTANCE.getValTs();
		if (prev.compareTo(val_ts) < 0)
		{
			// (1) TODO perform the op
//			val = apply(val, u.op);
			// (2) merge the timestamp of the executed {@link UpdateMessage} to the value's timestamp
			val_ts.merge(update_ts);
			// (3) to avoid duplicate, keep record that this {@link UpdateMessage} has been executed
			Replica.INSTANCE.getInval().addUmid(update_msg);
		}

		/**
		 * (5) Returns the update's timestamp in a reply message;
		 * Note that the processing of {@link UpdateMessage} does not block.
		 */
		UpdateAckMessage udpate_ack_msg = new UpdateAckMessage(update_msg);
		CommunicationService.INSTACNE.sendMsg(msg.getSenderAddr(), udpate_ack_msg);
	}

	private void advanceRepTs(int rid)
	{
		Replica.INSTANCE.getRepTs().advance(rid);
	}

	public MultipartTimestamp computeUid(MultipartTimestamp prev, int rid)
	{
		MultipartTimestamp update_ts = new MultipartTimestamp(prev);
		update_ts.replaceAtIndex(Replica.INSTANCE.getRepTs(), rid);

		return update_ts;
	}
}
