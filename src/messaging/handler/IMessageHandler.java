package messaging.handler;

import messaging.message.IPMessage;

public interface IMessageHandler {

	public abstract boolean handleMessage(IPMessage msg);

}