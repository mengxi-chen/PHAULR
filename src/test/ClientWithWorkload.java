package test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import messaging.message.IPMessage;
import messaging.message.QueryMessage;
import messaging.message.UpdateMessage;
import application.Client;

/**
 * A client keeps issuing requests.
 * @date 2014-10-14
 */
public class ClientWithWorkload implements Runnable
{
	private BlockingQueue<IPMessage> request_queue = new LinkedBlockingDeque<IPMessage>();

	private int total_requests;

	public ClientWithWorkload(BlockingQueue<IPMessage> request_queue, int total_requests)
	{
		this.total_requests = total_requests;
		this.request_queue = request_queue;
	}

	@Override
	public void run()
	{
		Client.INSTANCE.start();

		for (int cnt = 0; cnt < this.total_requests; cnt++)
		{
			IPMessage msg;
			try
			{
				msg = this.request_queue.take();

				if (msg instanceof UpdateMessage)	// issue an update request
					Client.INSTANCE.issueUpdateRequest((UpdateMessage) msg);
				else	// issue a query request
					Client.INSTANCE.issueQueryRequest((QueryMessage) msg);

			} catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
	}

}
