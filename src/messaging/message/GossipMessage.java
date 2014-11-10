package messaging.message;

import communication.Address;

import storage.Replica;
import storage.datastructure.Logs;
import storage.datastructure.MultipartTimestamp;

public class GossipMessage extends IPMessage
{
	private static final long serialVersionUID = -8508794926871099904L;

	private MultipartTimestamp sender_replica_ts;	//sender's timestamp
	private Logs sender_replica_log;	//sender's log
	private int sender_replica_rid;	//sender's rid

	public GossipMessage()
	{
		this(Replica.INSTANCE);
	}

	private GossipMessage(Replica sender_replica)
	{
		this(sender_replica.getAddress(), sender_replica.getRid(), sender_replica.getRepTs(), sender_replica.getLogs());
	}

	private GossipMessage(Address sender_addr, int rid, MultipartTimestamp replica_ts, Logs replica_log)
	{
		super(sender_addr);

		this.sender_replica_rid = rid;
		this.sender_replica_ts = replica_ts;
		this.sender_replica_log = replica_log;
	}

	public MultipartTimestamp getReplicaTs(){
		return sender_replica_ts;
	}

	public Logs getLogs(){
		return sender_replica_log;
	}

	public int getRid(){
		return sender_replica_rid;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("SenderReplicaId: ").append(this.sender_replica_rid).append(";")
			.append("SenderReplicaTs: ").append(this.sender_replica_ts.toString());

		return sb.toString();

	}
}
