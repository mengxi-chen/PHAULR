package messaging.message;

import java.util.Set;

import storage.datastructure.MultipartTimestamp;
import application.Client;
import communication.Address;

public class UpdateMessage extends IPMessage
{
	private static final long serialVersionUID = 5861213616856217661L;

	private MultipartTimestamp prev;
	private String op;
	private MultipartTimestamp update_ts; // update's timestamp = return message

	/**
	 * Constructor of an {@link UpdateMessage}
	 * @param sender_addr {@link Address} of the message sender
	 * @param deps
	 * @param prev
	 * @param op	operation
	 * @param umid	identifier of this {@link UpdateMessage} assigned by the {@link Client} who issued it
	 */
	public UpdateMessage (Address sender_addr, Set<MessageGid> deps, MultipartTimestamp prev, String op, MessageGid umid)
	{
		super(sender_addr);

		this.prev = prev;
		this.op = op;
		this.update_ts = new MultipartTimestamp();

		super.msg_id = umid;
		super.deps = deps;
	}

	public UpdateMessage(Set<MessageGid> deps, MultipartTimestamp prev, String op)
	{
		super(null);

		this.prev = prev;
		this.op = op;
		
		super.deps = deps;
	}

	public MultipartTimestamp getPrev(){
		return prev;
	}

	public void setPrev(MultipartTimestamp mpts)
	{
		this.prev = mpts;
	}
	
	public String get_op(){
		return op;
	}
	
	public void setOp(String op)
	{
		this.op = op;
	}
	
	public void setUpdateTs(MultipartTimestamp mTimestamp){
		update_ts = mTimestamp;
	}

	public MultipartTimestamp getUpdateTs(){
		return update_ts;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(super.toString()).append(";")
			.append("Prev: ").append(this.prev.toString()).append(";")
			.append("UMessageId: ").append(super.msg_id.toString()).append(";");

		return sb.toString();
	}
}
