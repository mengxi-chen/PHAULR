package storage.datastructure;

import java.io.Serializable;

import messaging.message.MessageGid;
import messaging.message.UpdateMessage;

/**
 * Record for {@link UpdateMessage}
 * @date 2014-10-12
 */
public class LogRecord implements Serializable
{
	private static final long serialVersionUID = -8314909756035487688L;

	private UpdateMessage update_msg;
	private int creator_rid;

	/**
	 * Constructor of {@link LogRecord}
	 * @param msg {@link UpdateMessage}
	 * @param rid creator of the uid of this {@link UpdateMessage}
	 */
	public LogRecord(UpdateMessage msg, int rid)
	{
		this.update_msg = msg;
		this.creator_rid = rid;
	}

	public UpdateMessage getUpdateMessage(){
		return update_msg;
	}

	public int getCreatorId(){
		return this.creator_rid;
	}
	public MultipartTimestamp getPrev(){
		return this.update_msg.getPrev();
	}

	public String getOp(){
		return this.update_msg.get_op();
	}

	public MultipartTimestamp getTs(){
		return this.update_msg.getUpdateTs();
	}

	/**
	 * @return cid ({@link MessageGid}) of the {@link UpdateMessage}
	 * 	represented by this {@link LogRecord}
	 */
	public MessageGid getUmid()
	{
		return this.update_msg.getMsgGid();
	}

	@Override
	public String toString()
	{
		return this.update_msg.toString();
	}
}

