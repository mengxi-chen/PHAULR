package storage.datastructure;

import communication.Configuration;

/**
 * ts_table(p) = latest multipart timestamp received from p
 * @date 2014-10-20
 */
public class TsTable
{
	private MultipartTimestamp[] ts_table;

	public TsTable()
	{
		this.ts_table = new MultipartTimestamp[Configuration.REPLICA_NUM];
	}

	public MultipartTimestamp getReplicaTs(int rid)
	{
		return this.ts_table[rid];
	}

	public void setReplicaTs(int rid, MultipartTimestamp ts)
	{
		this.ts_table[rid] = ts;
	}

	/**
	 * Is this {@link LogRecord} known everywhere?
	 * isknown(r) = \forall replica j, ts_table(j)_{r.node} >= r.ts_{r.node}
	 *
	 * @param log_record
	 * @return <code>true</code> if this {@link LogRecord} is known everywhere;
	 * 	<code>false</code>, otherwise.
	 */
	public boolean isKnown(LogRecord log_record)
	{
		int rid = log_record.getCreatorId();
		MultipartTimestamp ts = log_record.getTs();

		for (MultipartTimestamp rep_ts : this.ts_table)
		{
			if (rep_ts.getComponentByReplica(rid) < ts.getComponentByReplica(rid))
				return false;
		}

		return true;
	}
}
