package messaging.handler;

import messaging.message.IPMessage;

public interface IMessageHandler {

	public abstract void handleMessage(IPMessage msg);

}