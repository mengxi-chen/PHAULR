package communication;

import messaging.handler.GossipMessageHandler;
import messaging.handler.QueryMessageHandler;
import messaging.handler.UpdateMessageHandler;
import messaging.message.GossipMessage;
import messaging.message.IPMessage;
import messaging.message.QueryMessage;
import messaging.message.UpdateMessage;
import application.handler.QueryACKMessageHandler;
import application.handler.UpdateAckMessageHandler;
import application.message.UpdateAckMessage;

public class MessageDispatcher
{
	private IPMessage msg = null;

	public MessageDispatcher(IPMessage msg) 
	{
		this.msg = msg;
	}

	public void dispatch() {
		// the following messages are processed by replicas
		if (this.msg instanceof UpdateMessage)
			new UpdateMessageHandler().handleMessage(this.msg);
		else if(this.msg instanceof QueryMessage)
			new QueryMessageHandler().handleMessage(this.msg);
		else if(this.msg instanceof GossipMessage)
			new GossipMessageHandler().handleMessage(this.msg);

		// the following messages are processed by clients
		else if (this.msg instanceof UpdateAckMessage)
			new UpdateAckMessageHandler().handleMessage(this.msg);
		else // if (this.msg instanceof QueryAckMessage)
			new QueryACKMessageHandler().handleMessage(this.msg);
	}
}
