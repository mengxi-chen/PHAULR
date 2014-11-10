package communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Logger;

public enum Configuration {

	INSTANCE;

	private static Logger logger = Logger.getLogger(Configuration.class.getName());

	public static int REPLICA_NUM = 0;

	// for communication
	/**
	 * timeout for sockets communication (default: 5 seconds)
	 */
	public final static int TIMEOUT = 5000;
	/**
	 * initial delay of {@link Broadcast} service (default: 3 seconds)
	 */
	public final static int INITIAL_DELAY = 3000;
	/**
	 * delay of {@link Broadcast} service (default: 2 seconds)
	 */
	public final static int DELAY = 2000;

	// for configuration

	// ip : port \n
	public final static String CONFIG_REPLICA_FILE = "config_replica";
	// ip : port \n ip : port \n
	public final static String CONFIG_CLIENT_FILE = "config_client";
	// ip : port \n ip : port \n ip : port \n ...
	public final static String CONFIG_SYSTEM_FILE = "config_system";

	public final static String PARSE_SPLIT = "\\s*:\\s*";

	public final static String REPLICA_LOG_FILE = "replica.txt";
	public final static String CLIENT_LOG_FILE = "client.txt";

//	private List<Address> replica_pool = new ArrayList<>();

	private Address[] replica_pool;

	/**
	 * Load the {@link #CONFIG_SYSTEM_FILE} and fill the {@link #replica_pool} list
	 *
	 * <b>Note:</b> You should call {@link #configSystem()} explicitly before configure
	 * your clients and replicas.
	 */
	public void configSystem()
	{
		System.out.println("********** Configure system **********");
		logger.debug("********** Configure system **********");

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(new File(Configuration.CONFIG_SYSTEM_FILE)));

			/**
			 * Parse the "config_system" file
			 */

			// the first line is the number of replicas
			String replica_number_line = br.readLine();
			Configuration.REPLICA_NUM = Integer.parseInt(replica_number_line);
			this.replica_pool = new Address[Configuration.REPLICA_NUM];

			// each of the following REPLICA_NUM lines consists of the address of a replica: ip : port
			String replica_addr_line = null;

			for (int index = 0; index < Configuration.REPLICA_NUM; index++)
			{
				replica_addr_line = br.readLine();

				String[] replica_addr = replica_addr_line.split(Configuration.PARSE_SPLIT);
				String ip = String.valueOf(replica_addr[0]);
				int port = Integer.parseInt(replica_addr[1]);

				this.replica_pool[index] = new Address(ip, port);
			}

		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}

		System.out.println("***** Replicas: " + Arrays.toString(this.replica_pool) + " *****");
		logger.debug("***** Replicas: " + Arrays.toString(this.replica_pool) + " *****");
	}

	public Address[] getReplicaPool()
	{
		return this.replica_pool;
	}

	/**
	 * Get a random replica to contact other than the specified one
	 * @param addr the specified replica
	 * @return the {@link Address} of a replica
	 */
	public Address getRandomReplicaOtherThan(Address addr)
	{
		int size = this.replica_pool.length;
		Random rand = new Random();
		Address random_addr = null;

		while(true)
		{
			random_addr = replica_pool[rand.nextInt(size)];
			if (! random_addr.equals(addr))
				return random_addr;
		}
	}

	public static void main(String[] args) throws IOException
	{
//		BufferedReader br = new BufferedReader(new FileReader(new File("config_client")));
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File("config_client_test")));
		bWriter.write("test");
		bWriter.close();
	}
}