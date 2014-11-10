package communication;

public class AddressPlusRid
{
	private Address addr;
	private int rid;

	public AddressPlusRid(Address addr, int rid)
	{
		this.addr = addr;
		this.rid = rid;
	}

	public static AddressPlusRid[] attachRids(Address[] addres)
	{
		AddressPlusRid[] addr_rid_pool = new AddressPlusRid[addres.length];

		for (int index = 0; index < addres.length; index++)
			addr_rid_pool[index] = new AddressPlusRid(addres[index], index);

		return addr_rid_pool;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(addr.toString()).append(" : ").append(rid);
		return sb.toString();
	}
}
