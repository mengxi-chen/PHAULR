package messaging.message;

import java.io.Serializable;

import communication.Address;

/**
 * Globally unique identifier of {@link IPMessage}
 * consisting of client's address and client_seqno.
 *
 * <b>Note:</b> They are assigned to not only {@link UpdateMessage}s but also to
 * {@link QueryMessage}s.
 *
 * The {@link MessageGid} class has overridden both {@link #equals(Object)} and {@link #hashCode()}.
 * So you can use them as keys of hashing.
 *
 * @date 2014-10-11
 */
public class MessageGid implements Serializable
{
	private static final long serialVersionUID = 2981991400190806291L;

	private Address client_addr;
	private int client_seqno;

	public MessageGid(Address addr, int seqno)
	{
		this.client_addr = addr;
		this.client_seqno = seqno;
	}

	@Override
	public boolean equals(Object another)
	{
		if (this == another)
			return true;

		if (! (another instanceof MessageGid))
			return false;

		MessageGid another_umid = (MessageGid) another;
		return this.client_addr.equals(another_umid) && this.client_seqno == another_umid.client_seqno;
	}

	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 13 + this.client_addr.hashCode();
		hash = hash * 17 + this.client_seqno;

		return hash;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(this.client_addr.toString()).append(":").append(this.client_seqno);

		return sb.toString();
	}
}
