/**
 * @author hengxin
 * @date 2014-04-22; 2014-05-17
 * @description generate workload with different statistical distributions;
 *   Hope: it will support real workload collected from open-source/commercial data stores
 */
package test;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;

import messaging.message.IPMessage;
import messaging.message.MessageGid;
import messaging.message.QueryMessage;
import messaging.message.UpdateMessage;

import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.ExponentialGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;

import storage.datastructure.MultipartTimestamp;
import application.Client;

public class PoissonWorkloadGenerator implements Runnable
{
	// synchronous blocking queue between {@link PoissonWorkloadGenerator} and {@link ClientWithWorkload}
	private BlockingQueue<IPMessage> request_queue = new LinkedBlockingDeque<IPMessage>();

	private int total_requests;
	private int rate;

	// used to generate number sequence accordance with some specified distribution (here, it is exponential distribution)
	private NumberGenerator<Double> exp_interarrival_gen = null;
	private final long oneMinute = 1000;

	private Random rand_type = new Random();

	/**
	 * constructor of {@link PoissonWorkloadGenerator}
	 *
	 * @param request_queue {@link #request_queue}: synchronous blocking queue
	 * 	between {@link PoissonWorkloadGenerator} and {@link Executor}
	 * @param total_requests {@link #total_requests}: total number of requests in the workload to generate
	 * @param rate {@link #rate}: arrival rate of requests (Poisson process)
	 */
	public PoissonWorkloadGenerator(BlockingQueue<IPMessage> request_queue, int total_requests, int rate)
	{
		this.request_queue = request_queue;
		this.total_requests = total_requests;
		this.rate = rate;

		this.exp_interarrival_gen = new ExponentialGenerator(this.rate, new MersenneTwisterRNG());
	}

	/**
	 * Generate "prev" for
	 *
	 * @return An
	 * @throws InterruptedException
	 *
	 */
	private IPMessage generateNextRequest() throws InterruptedException
	{
		long interval = Math.round(exp_interarrival_gen.nextValue() * oneMinute);

		Thread.sleep(interval);

		// generate "deps" and "prev"
		Set<MessageGid> deps = Client.INSTANCE.getFrontEnd().generateRandomDeps();
		MultipartTimestamp prev = Client.INSTANCE.getFrontEnd().generateMpts(deps);
		
		boolean type = this.rand_type.nextBoolean();
		if (type)	// issue an update request
			return new UpdateMessage(deps, prev, "UPDATE");
		else	// issue a query request
			return new QueryMessage(deps, prev, "QUERY");
	}

	/**
	 * generate requests and put them into a synchronized queue
	 */
	@Override
	public void run()
	{
		for (int num = 0; num < this.total_requests; num++)
		{
			try
			{
				this.request_queue.put(this.generateNextRequest());
			} catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
	}
}
