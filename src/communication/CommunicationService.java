package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import messaging.message.IPMessage;

public enum CommunicationService
{
    INSTACNE;

//	private static Executor exec = Executors.newCachedThreadPool();

	/**
	 * Send¡¡an {@link IPMessage}
	 * @param dest_addr {@link Address} of the destination
	 * @param msg	{@link IPMessage} to send
	 */
	public void sendMsg(Address dest_addr, IPMessage msg)
	{
		InetSocketAddress socketAddress = new InetSocketAddress(dest_addr.getIp(), dest_addr.getPort());
		Socket socket = new Socket();
		try
		{
			socket.connect(socketAddress, Configuration.TIMEOUT);
//			System.out.println("Client socket: " + socket.toString());

			//Create an output stream to send data to the server
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(msg);
			oos.flush();
		}
		catch (ConnectException ce) {
			ce.printStackTrace();
		}
		catch (SocketTimeoutException stoe) {
			stoe.printStackTrace();
		}
		catch(UnknownHostException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				socket.close();
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	/**
	 * Start to listen to coming messages
	 * @param addr {@link Address}
	 */
	public void start2Listen(final Address addr)
	{
		new Thread(new Runnable() {
			public void run() {

		ServerSocket serverSocket = null;
		try
		{
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(addr.getIp(), addr.getPort()));

			while(true)
			{
				// Listen to messages from clients and other replicas
				final Socket socket = serverSocket.accept();
//				System.out.println("Accept a socket: " + socket.toString());

				// retrieve the message
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				IPMessage msg = (IPMessage) ois.readObject();

				/**
				 * Modified:
				 * remove threads here;
				 * the correctness of the protocol requires the atomic processing of each message
				 *
				 * @date Oct 17, 2014
				 */
				new MessageDispatcher(msg).dispatch();
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		finally
		{
			try
			{
				serverSocket.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
		}).start();
}
}
