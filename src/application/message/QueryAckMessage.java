package application.message;

import messaging.message.IPMessage;
import messaging.message.MessageGid;
import storage.datastructure.MultipartTimestamp;

public class QueryAckMessage extends IPMessage
{
	private static final long serialVersionUID = 2710971742479108641L;

	private MultipartTimestamp query_result_ts;

	/**
	 * Constructor of {@link QueryAckMessage}
	 *
	 * @param qmid
	 * @param val_ts timestamp of replica
	 */
	public QueryAckMessage(MessageGid qmid, MultipartTimestamp val_ts)
	{
		super(null);
		super.msg_id = qmid;
		this.query_result_ts = val_ts;
	}

	public MultipartTimestamp getQueryResultTs()
	{
		return this.query_result_ts;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("QACKMessageId: ").append(super.msg_id.toString()).append(";")
			.append("QueryResultTs: ").append(this.query_result_ts.toString());

		return sb.toString();
	}
}