package cast.server.rooms;

import java.nio.channels.SelectionKey;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cast.common.packets.Packet;
import cast.server.connection.ServerConnection;
import cast.server.players.Client;

/**
 * Abstract base class representing a game room.
 */
public abstract class GameRoom {
	
	protected int id;
	protected int backgroundId;
	protected List<Packet> packetQueue;
	protected ServerConnection connection;
	
	public GameRoom(ServerConnection connection, int battleId, int backgroundId) {
		this.connection = connection;
		this.id = battleId;
		this.backgroundId = backgroundId;
		
		packetQueue = Collections.synchronizedList(new LinkedList<Packet>());
	}
	
	public int getId() {
		return id;
	}

	public int getBackgroundId() {
		return backgroundId;
	}
	
	public void setBackgroundId(int bgId) {
		backgroundId = bgId;
	}
	
	protected Packet getNextPacket() {
		if (packetQueue.size() > 0) {
			return packetQueue.remove(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Add packet to be send on game tick
	 * 
	 * @param packet
	 */
	public void queuePacket(Packet packet) {
		packetQueue.add(packet);
	}
	
	/**
	 * Call in game tick to process this room.
	 */
	public abstract void processRoom();
	
	/**
	 * Process spell cast in the game room
	 * @param caster
	 */
	public abstract void processSpellCast(Client caster);
	
	public abstract void addUser(Client user);
	
	public abstract Client getUser(SelectionKey key);
	
	public abstract Client removeUser(SelectionKey key);
	
	public abstract void end();
	
	public abstract Map<SelectionKey, Client> getUsers();

}
