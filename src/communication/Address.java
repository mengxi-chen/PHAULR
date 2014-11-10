package communication;

import java.io.Serializable;

public class Address implements Serializable
{
	private static final long serialVersionUID = 3777445645461924057L;

	private final String ip;
	private final int port;

	public Address(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}

	public String getIp()
	{
		return ip;
	}

	public int getPort()
	{
		return port;
	}

	@Override
	public boolean equals(Object another)
	{
		if (this == another)
			return true;

		if (! (another instanceof Address))
			return false;

		Address another_addr = (Address) another;
		return this.ip.equals(another_addr.ip) && this.port == another_addr.port;
	}

	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 17 + this.ip.hashCode();
		hash = hash * 31 + this.port;

		return hash;
	}
	/**
	 * @return "ip : port"
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.ip).append(" : ").append(this.port);
		return sb.toString();
	}
}
