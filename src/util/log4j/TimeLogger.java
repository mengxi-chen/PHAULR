package util.log4j;

import org.apache.log4j.Logger;

import messaging.message.IPMessage;

public class TimeLogger 
{
	private static Logger logger = Logger.getLogger(TimeLogger.class.getName());
	
	public static void recordIssueTime(IPMessage msg)
	{
		logger.info(msg.getMsgGid() + "\t Issue time: \t" + System.currentTimeMillis() + "\t Dep numbers: \t" + msg.getDepsNum());
	}
	
	public static void recordEventualTime(IPMessage msg)
	{
		logger.info(msg.getMsgGid() + "\t Eventual time: \t" + System.currentTimeMillis() + "\t Dep numbers: \t" + msg.getDepsNum());
	}
	
	public static void recordDepTime(IPMessage msg)
	{
		logger.info(msg.getMsgGid() + "\t Dep time: \t" + System.currentTimeMillis() + "\t Dep numbers: \t" + msg.getDepsNum());
	}
	
	public static void recordCausalTime(IPMessage msg)
	{
		logger.info(msg.getMsgGid() + "\t Causal time: \t" + System.currentTimeMillis() + "\t Dep numbers: \t" + msg.getDepsNum());
	}
	
	public static void recordAckTime(IPMessage msg)
	{
		logger.info(msg.getMsgGid() + "\t Ack time: \t" + System.currentTimeMillis() + "\t Dep numbers: \t" + msg.getDepsNum());
	}
}
