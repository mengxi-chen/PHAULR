package application;

import messaging.message.QueryMessage;
import messaging.message.UpdateMessage;
import storage.datastructure.MultipartTimestamp;
import application.message.QueryAckMessage;
import application.message.UpdateAckMessage;

/**
 * Client of this lazy-replication database.
 * The client has a front end ({@link FrontEnd}) to do everything for it.
 * @date 2014-10-11
 */
public enum Client
{
	INSTANCE;

	private FrontEnd fe = new FrontEnd();

	public void start()
	{
		this.fe.start();
	}

	public void issueUpdateRequest(MultipartTimestamp prev, String op)
	{
		this.fe.issueUpdateRequest(prev, op);
	}

	public void issueUpdateRequest(UpdateMessage update_msg)
	{
		this.issueUpdateRequest(update_msg.getPrev(), update_msg.get_op());
	}

	public void issueQueryRequest(MultipartTimestamp prev, String op)
	{
		this.fe.issueQueryRequest(prev, op);
	}

	public void issueQueryRequest(QueryMessage query_msg)
	{
		this.issueQueryRequest(query_msg.getPrev(), query_msg.getOp());
	}

	public void receiveUpdateAckMessage(UpdateAckMessage update_ack_msg)
	{
		this.fe.processUpdateAckMessage(update_ack_msg);
	}

	public void receiveQueryAckMessage(QueryAckMessage query_ack_msg)
	{
		this.fe.processQueryAckMessage(query_ack_msg);
	}

	public FrontEnd getFrontEnd()
	{
		return this.fe;
	}
}
