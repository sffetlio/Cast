package cast.server.connection;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Set;

import cast.common.packets.Packet;
import cast.common.packets.PacketFactory;
import cast.server.GameState;
import cast.server.PacketProcessor;
import cast.server.Settings;
import cast.server.players.Client;

public class ServerConnection extends AbstractServer {

	public PacketProcessor packetProcessor;

	public ServerConnection() {
		super(Settings.PORT);
		System.out.println("Starting on " + Settings.PORT);
		packetProcessor = new PacketProcessor(this);
	}

	@Override
	protected void messageReceived(ByteBuffer message, SelectionKey key) {
		// System.out.println("messageReceived");

		Packet packet = PacketFactory.getPacketByFirstBytes(message);
		packetProcessor.processPacket(packet, key);
	}

	@Override
	protected void connection(SelectionKey key) {
		System.out.println("cast/server/connection");
		GameState.addUser(key);
	}

	@Override
	protected void disconnected(SelectionKey key) {
		System.out.println("disconnected");
		GameState.removeUser(key);
	}

	@Override
	protected void started(boolean alreadyStarted) {
		System.out.println("Server started.");
	}

	@Override
	protected void stopped() {
		System.out.println("Server stopped");
	}

	public void write(SelectionKey key, Packet packet) {
		write(key, packet.getBytes());
	}

	/**
	 * Send a packet to all users.
	 * 
	 * @param packet
	 *            - packet to send
	 */
	public void broadcast(Packet packet, Map<SelectionKey, Client> users) {
		Set<SelectionKey> keySet = users.keySet();
		synchronized (keySet) {
			for (SelectionKey userKey : keySet) {
				if(userKey != null)
					write(userKey, packet);
			}
		}
	}

	/**
	 * Send a packet to all users. Exclude client with SelectionKey key.
	 * 
	 * @param packet
	 *            - packet to send
	 * @param users
	 *            - users to broadcast to
	 * @param key
	 *            - skip this client
	 */
	public void broadcast(Packet packet, Map<SelectionKey, Client> users, SelectionKey key) {
		for (SelectionKey userKey : users.keySet()) {
			if (userKey != key) {
				write(userKey, packet);
			}
		}
	}

}
