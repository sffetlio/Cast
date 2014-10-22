package cast.client.gdx.connection;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import cast.client.gdx.Cast;
import cast.client.gdx.PacketProcessor;
import cast.common.packets.Packet;
import cast.common.packets.PacketFactory;

/**
 * Thread handling connecting to server, packet sending and receiving.</br>
 * Extends {@link cast.client.gdx.connection.AbstractBlockingClient}.</br> Uses {@link cast.client.gdx.connection.PacketQueue} for
 * separate thread for sending packets.
 * 
 * @see cast.client.gdx.connection.AbstractBlockingClient
 * @see cast.client.gdx.connection.PacketQueue
 * 
 */
public class ClientConnectionThread extends AbstractBlockingClient {

	private final PacketQueue packetQueue;
	private PacketProcessor packetProcessor;

	public ClientConnectionThread(Cast game) throws UnknownHostException {
		super(game.getPreferencesManager().getIp());
		this.packetQueue = new PacketQueue(this);
		packetProcessor = new PacketProcessor(game, this);
	}

	@Override
	protected void messageReceived(ByteBuffer message) {
		if (message.capacity() == 0) {
			return;
		}
		Packet packet = PacketFactory.getPacketByFirstBytes(message);

		packetProcessor.processPacket(packet);
	}

	@Override
	protected void connected() {
		Cast.log("connected");
		new Thread(packetQueue).start();
	}

	/**
	 * Write packet in queue for sending
	 * 
	 * @param packet
	 * @return true if packet was added in queue
	 */
	public synchronized boolean write(Packet packet) {
		if (isConnected()) {
			packetQueue.add(packet);
			return true;
		}
		return false;
	}

	/**
	 * Write packet skipping the packet queue. It is recommended to use
	 * {@link ClientConnectionThread#write(Packet)
	 * write(Packet)}.
	 * 
	 * @param packet
	 * @return true if the message was sent to the server.
	 */
	public boolean writeDirect(Packet packet) {
		return write(packet.getBytes());
	}

	@Override
	protected void disconnected() {
		Cast.log("disconnected");
	}

	@Override
	public void stop() {
		packetQueue.stop();
	}

}
