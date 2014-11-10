package application.handler;

import org.apache.log4j.Logger;

import application.Client;
import application.message.UpdateAckMessage;
import messaging.handler.IMessageHandler;
import messaging.message.IPMessage;

public class UpdateAckMessageHandler implements IMessageHandler
{
	private static Logger logger = Logger.getLogger(UpdateAckMessageHandler.class.getName());

	@Override
	public void handleMessage(IPMessage msg)
	{
		UpdateAckMessage update_ack_msg = (UpdateAckMessage) msg;

		System.out.println(" *********** Receive an UpdateAckMessage: " + update_ack_msg.toString() + " **************");
		logger.info(" *********** Receive an UpdateAckMessage: " + update_ack_msg.toString() + " **************");

		Client.INSTANCE.receiveUpdateAckMessage(update_ack_msg);
	}

}
