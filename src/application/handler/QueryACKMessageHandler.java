package application.handler;

import messaging.handler.IMessageHandler;
import messaging.message.IPMessage;
import application.Client;
import application.message.QueryAckMessage;

public class QueryACKMessageHandler implements IMessageHandler {

	@Override
	public void handleMessage(IPMessage msg)
	{
		QueryAckMessage query_ack_msg = (QueryAckMessage) msg;

//		System.out.println("************ Receive an QueryAckMessage: " + query_ack_msg.toString());

		Client.INSTANCE.receiveQueryAckMessage(query_ack_msg);
	}

}
