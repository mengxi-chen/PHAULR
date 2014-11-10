package communication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import messaging.message.GossipMessage;


public class Broadcast
{
	private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	/**
	 * Broadcast the gossip messages to all the other replicas
	 */
	Runnable broadcast_task = new Runnable()
	{
		@Override
		public void run()
		{
			for (Address dest_addr : Configuration.INSTANCE.getReplicaPool())
				if (! Broadcast.this.broadcaster.equals(dest_addr))
					CommunicationService.INSTACNE.sendMsg(dest_addr, new GossipMessage());
		}
	};

	private Address broadcaster;

	public Broadcast(Address broadcast_addr)
	{
		this.broadcaster = broadcast_addr;
	}

	/**
	 * Starting the broadcast after the given initial delay,
	 * and subsequently with the given delay between the
	 * termination of one execution and the commencement of the next.
	 *
	 * @param initial_delay	initial delay in milliseconds
	 * @param delay delay in milliseconds
	 */
	public void startBroadcast(int initial_delay, int delay)
	{
		scheduler.scheduleWithFixedDelay(this.broadcast_task, initial_delay, delay, TimeUnit.MILLISECONDS);
	}
}