package cast.client.gdx.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

import cast.common.MessageLength;
import cast.common.TwoByteMessageLength;

/**
 * An abstract blocking client, designed to connect to implementations of
 * AbstractServer in its own thread. Since the client only has a single
 * connection to a single server it can use blocking IO. This class provides a
 * set of callback methods for concrete implementations to know the state of the
 * client and its connection and receive messages from the server. This client
 * will automatically handle partially received messages or multiple message at
 * once. A maximum message size is imposed by the server, as handled by the
 * MessageLength parameter, which defaults to the TwoByteMessageLength (and thus
 * a max message of 65535 bytes).
 * 
 * This client does not Cast.log, implementations should handle this.
 * 
 * This client does not support SSL or UDP connections.
 * 
 * @see MessageLength
 */
public abstract class AbstractBlockingClient implements Runnable {

	private enum State {
		STOPPED, STOPPING, RUNNING, CONNECTED
	}

	private static short CONNECTION_RETRIES = 3;
	private static short DEFAULT_MESSAGE_SIZE = 512;
	private static int DEFAULT_PORT = 4444;

	private final AtomicReference<State> state = new AtomicReference<State>(State.STOPPED);
	private final InetAddress server;
	private final int port;
	private final int byteLength;
	private final MessageLength messageLength;
	private final int defaultBufferSize;
	private final AtomicReference<OutputStream> out = new AtomicReference<OutputStream>();
	private final AtomicReference<InputStream> in = new AtomicReference<InputStream>();

	/**
	 * Construct a client which will attempt to connect to the given server on
	 * the given port.
	 * 
	 * @param server
	 *            the server address.
	 * @throws java.net.UnknownHostException
	 */
	public AbstractBlockingClient(String server) throws UnknownHostException {
		this(server, DEFAULT_PORT, new TwoByteMessageLength(), DEFAULT_MESSAGE_SIZE);
	}

	/**
	 * Construct an unstarted client which will attempt to connect to the given
	 * server on the given port.
	 * 
	 * @param server
	 *            the server address.
	 * @param port
	 *            the port on which to connect to the server.
	 * @param messageLength
	 *            how to construct and parse message lengths.
	 * @param defaultBufferSize
	 *            the default buffer size for reads. This should as small as
	 *            possible value that doesn't get exceeded often - see class
	 *            documentation.
	 * @throws java.net.UnknownHostException
	 */
	public AbstractBlockingClient(String server, int port, MessageLength messageLength, int defaultBufferSize) throws UnknownHostException {
		this.server = InetAddress.getByName(server);
		this.port = port;
		this.messageLength = messageLength;
		this.defaultBufferSize = defaultBufferSize;
		this.byteLength = messageLength.byteLength();
	}

	/**
	 * Returns the port to which this client will connect.
	 * 
	 * @return the port to which this client will connect.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the host to which this client will connect.
	 * 
	 * @return the host to which this client will connect.
	 */
	public String getServerAddress() {
		return server.getHostAddress();
	}

	/**
	 * Returns true if this client is running.
	 * 
	 * @return true if this client is running.
	 */
	public boolean isRunning() {
		return state.get() == State.RUNNING;
	}

	/**
	 * Returns true if this client is connected.
	 * 
	 * @return true if this client is connected.
	 */
	public boolean isConnected() {
		return state.get() == State.CONNECTED;
	}

	/**
	 * Returns true if this client is stopped.
	 * 
	 * @return true if this client is stopped.
	 */
	public boolean isStopping() {
		return state.get() == State.STOPPING;
	}

	/**
	 * Returns true if this client is stopped.
	 * 
	 * @return true if this client is stopped.
	 */
	public boolean isStopped() {
		return state.get() == State.STOPPED;
	}

	/**
	 * Attempt to connect to the server and receive messages. If the client is
	 * already running, it will not be started again. This method is designed to
	 * be called in its own thread and will not return until the client is
	 * stopped.
	 * 
	 * @throws RuntimeException
	 *             if the client fails
	 */
	public void run() {
		// if the client is running or connected do nothing
		if (isRunning() || isConnected()) {
			return;
		}

		int connectionRetries = 0;
		while (!isStopping()) {
			if (connectionRetries++ > CONNECTION_RETRIES) {
				state.set(State.STOPPING);
				break;
			}

			makeConnection();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		state.set(State.STOPPED);
	}

	/**
	 * Connect to the server and process requests.<br>
	 * Returns when connection is closed or server is stopping
	 */
	private void makeConnection() {
		Socket socket = null;
		try {
			socket = new Socket(server, port);
			socket.setKeepAlive(true);
			out.set(socket.getOutputStream());
			in.set(socket.getInputStream());
			int limit = 0;
			byte[] inBuffer = new byte[defaultBufferSize];

			state.set(State.CONNECTED);
			connected();

			while (isConnected()) {
				limit += in.get().read(inBuffer, limit, inBuffer.length - limit);
				if (limit != -1 && limit >= byteLength) {
					int messageLen;
					do {
						byte[] lengthBytes = new byte[byteLength];
						System.arraycopy(inBuffer, 0, lengthBytes, 0, byteLength);
						messageLen = (int) messageLength.bytesToLength(lengthBytes);
						if (limit >= messageLen) {
							// enough data to extract the message
							byte[] message = new byte[messageLen];
							System.arraycopy(inBuffer, byteLength, message, 0, messageLen);
							messageReceived(ByteBuffer.wrap(message));
							// compact inBuffer
							byte[] temp = new byte[inBuffer.length];
							System.arraycopy(inBuffer, messageLen + byteLength, temp, 0, limit - messageLen - byteLength);
							inBuffer = temp;
							limit = limit - messageLen - byteLength;
						} else if (messageLen > inBuffer.length) {
							byte[] temp = new byte[messageLen + byteLength];
							System.arraycopy(inBuffer, 0, temp, 0, inBuffer.length);
							inBuffer = temp;
						}
					} while (messageLen < limit);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
			}
			state.set(State.RUNNING);
			disconnected();
		}
		// } catch (ClosedByInterruptException ie) {
		// Cast.log("ClosedByInterruptException");
		// // do nothing
		// } catch (ConnectException ce) {
		// Cast.log("ConnectException");
		// // throw new RuntimeException(ce.getMessage());
		// } catch (SocketException se) {
		// Cast.log("SocketException");
		// // do nothing
		// } catch (IOException ioe) {
		// Cast.log("IOException");
		// // throw new RuntimeException("Client failure: "+ioe.getMessage());
		// } catch (ArrayIndexOutOfBoundsException oob) {
		// Cast.log("ArrayIndexOutOfBoundsException");
		// oob.printStackTrace();
		// //throw new RuntimeException("Client failure: "+oob.getMessage());
		// } catch (Exception oob) {
		// oob.printStackTrace();
		// Cast.log("Exception" + oob.getMessage());
		// //throw new RuntimeException("Client failure: "+oob.getMessage());
		// } finally {
		// Cast.log("error");
		//
		// // wait 1s for next connection
		//
		// // try {
		// // socket.close();
		// // state.set(State.STOPPED);
		// // } catch (Exception e) {
		// // // do nothing - server failed
		// // }
		// }
	}

	/**
	 * Stop the client in a graceful manner. After this call the client may
	 * spend some time in the process of stopping. A disconnected callback will
	 * occur when the client actually stops.
	 */
	public void stop() {
		state.set(State.STOPPING);
		// try {in.get().close();} catch (Exception e) {};
	}

	/**
	 * Send the given message to the server.
	 * 
	 * @param buffer
	 *            the message to send.
	 * @return true if the message was sent to the server.
	 */
	synchronized boolean write(byte[] buffer) {
		if (!isConnected()) {
			return false;
		}

		int len = buffer.length;
		byte[] lengthBytes = messageLength.lengthToBytes(len);
		try {
			byte[] outBuffer = new byte[len + byteLength];
			System.arraycopy(lengthBytes, 0, outBuffer, 0, byteLength);
			System.arraycopy(buffer, 0, outBuffer, byteLength, len);
			out.get().write(outBuffer);
			return true;
		} catch (Exception e) {
			// socket is closed, message not sent
			stop();
			return false;
		}
	}

	/**
	 * Callback method for when the client receives a message from the server.
	 * 
	 * @param message
	 *            the message from the server.
	 */
	protected abstract void messageReceived(ByteBuffer message);

	/**
	 * Callback method for when the client connects to the server.
	 */
	protected abstract void connected();

	/**
	 * Callback method for when the client disconnects from the server.
	 */
	protected abstract void disconnected();
}
