package messaging.message;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import communication.Address;

/**
 * Messages carrying its sender's address
 * @date 2014-10-11
 */
public abstract class IPMessage implements Serializable
{
	private static final long serialVersionUID = 6404655877945517424L;

	private final Address sender_addr;
	
	// globally unique id for this message
	protected MessageGid msg_id = null;

	/**
	 * for experiment
	 * added @date 2014-10-29
	 */
//	private long issue_time = 0L;
//	private long eventual_time = 0L;
//	private long causal_time = 0L;
//	private long ack_time = 0L;

	protected Set<MessageGid> deps = new HashSet<>();

	public IPMessage(Address addr)
	{
		this.sender_addr = addr;
	}

	public final Address getSenderAddr()
	{
		return this.sender_addr;
	}

	public MessageGid getMsgGid()
	{
		return this.msg_id;
	}
	
//	public long getIssueTime() 
//	{
//		return issue_time;
//	}
//
//	public void setIssueTime(long issue_time) 
//	{
//		this.issue_time = issue_time;
//	}
//
//	public long getEventualTime() 
//	{
//		return eventual_time;
//	}
//
//	public void setEventualTime(long eventual_time) 
//	{
//		this.eventual_time = eventual_time;
//	}
//
//	public long getCausalTime() 
//	{
//		return causal_time;
//	}
//
//	public void setCausalTime(long causal_time) 
//	{
//		this.causal_time = causal_time;
//	}
//
//	public long getAckTime()
//	{
//		return this.ack_time;
//	}
//	
//	public void setAckTime(long time)
//	{
//		this.ack_time = time;
//	}
	
	public Set<MessageGid> getDeps()
	{
		return this.deps;
	}
	
	/*
	 * Delete a dependency it relies on if it is present
	 * 
	 * @return <code>true</code> if it contains the specified dependency
	 */
	public boolean deleteDep(MessageGid msg_id)
	{
		return this.deps.remove(msg_id);
	}
	
	public int getDepsNum()
	{
		return this.deps.size();
	}
	
	@Override
	public String toString()
	{
		return this.sender_addr.toString();
	}
}
