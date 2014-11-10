package test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import messaging.message.IPMessage;
import communication.Configuration;

public class ConsProBenchmarkRunner
{
	private int total_requests;
	private int rate;

	public ConsProBenchmarkRunner(int total_requests, int rate)
	{
		this.total_requests = total_requests;
		this.rate = rate;
	}

	public void GenerateBenchmark()
	{
		BlockingQueue<IPMessage> request_queue = new LinkedBlockingDeque<>();
		new Thread(new PoissonWorkloadGenerator(request_queue, this.total_requests, this.rate)).start();

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
		new ConsProBenchmarkRunner(100, 5).start();
	}
}
