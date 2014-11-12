package test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import messaging.message.IPMessage;
import communication.Configuration;

public class ConsProBenchmarkRunner
{
	private int total_requests;
	private int rate;
	private int write_ratio;

	/**
	 * @param total_requests total number of requests 
	 * @param rate	the rate in which requests are generated and arrive
	 * @param write_ratio	the ratio of write requests in percentage
	 */
	public ConsProBenchmarkRunner(int total_requests, int rate, int write_ratio)
	{
		this.total_requests = total_requests;
		this.rate = rate;
		this.write_ratio = write_ratio;
	}

	public void GenerateBenchmark()
	{
		BlockingQueue<IPMessage> request_queue = new LinkedBlockingDeque<>();
		new Thread(new PoissonWorkloadGenerator(request_queue, this.total_requests, this.rate, this.write_ratio)).start();

		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException ie)
		{
			ie.printStackTrace();
		}

		new Thread(new ClientWithWorkload(request_queue, this.total_requests)).start();
	}

	public void start()
	{
//		Configuration.INSTANCE.configSystem();
		this.GenerateBenchmark();
	}

	public static void main(String[] args)
	{
		Configuration.INSTANCE.configSystem();
		new ConsProBenchmarkRunner(10000, 20, 90).start();
	}
}
