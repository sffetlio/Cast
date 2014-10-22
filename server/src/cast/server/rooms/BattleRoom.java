package cast.server.rooms;

import java.nio.channels.SelectionKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cast.common.TouchPoint;
import cast.common.packets.ClientExitedBattlePacket;
import cast.common.packets.Packet;
import cast.common.packets.PacketFactory;
import cast.common.packets.ServerTickPacket;
import cast.common.packets.SpellCastPacket;
import cast.server.GameServer;
import cast.server.GameState;
import cast.server.Spell;
import cast.server.connection.ServerConnection;
import cast.server.players.AIEnemy;
import cast.server.players.Client;

/**
 * Game room representing a battle. It has 2 cast.server.players. They can be two {@link Client} or one {@link Client} and one {@link AIEnemy}.
 */
public class BattleRoom extends GameRoom {

	protected Map<SelectionKey, Client> users;

	public BattleRoom(ServerConnection connection, int battleId, int backgroundId) {
		super(connection, battleId, backgroundId);
		users = Collections.synchronizedMap(new HashMap<SelectionKey, Client>(2));
	}

	public synchronized void addUser(Client user) {
		System.out.println("added user " + user.getNickname() + " to battle " + id);
		users.put(user.getKey(), user);
	}

	public synchronized Client getUser(SelectionKey key) {
		return users.get(key);
	}

	public synchronized Client removeUser(SelectionKey key) {
		return users.remove(key);
	}

	/**
	 * Process input from all clients in this battle and send packets.
	 */
	public void processRoom() {
		ServerTickPacket serverPacket = (ServerTickPacket) PacketFactory.createNewPacket(Packet.SERVER_TICK_PACKET);
		for (Client user : users.values()) {

			// prevent sending packet if no change in line or energy (if user is full life and energy)
			serverPacket.putUserId(user.getId());
			serverPacket.putLife(user.getLife());
			serverPacket.putEnergy(user.getEnergyAmount());

			List<TouchPoint> points = user.getPoints();
			synchronized (points) {
				if (points.size() > 0) {
					// first loop to get the point to send count
					Iterator<TouchPoint> i = points.iterator();
					int count = 0;
					while (i.hasNext()) {
						TouchPoint nextPoint = i.next();
						if (!nextPoint.isProcessed())
							++count;
					}
					serverPacket.putPointsCount(count);

					// second loop to get the actual points
					i = points.iterator();
					while (i.hasNext()) {
						TouchPoint nextPoint = i.next();
						if (nextPoint.isProcessed())
							continue;
						nextPoint.setProcessed(true);
						serverPacket.putPoint(nextPoint);
					}
				} else {
					serverPacket.putPointsCount(0);
				}
			}
		}

		if (!serverPacket.isEmpty()) {
			connection.broadcast(serverPacket, users);
		}

		Packet packet;
		while ((packet = getNextPacket()) != null) {
			connection.broadcast(packet, users);
		}
	}

	public Map<SelectionKey, Client> getUsers() {
		return users;
	}

	/**
	 * Remove all users. End the battle. This doesn't remove it from
	 * {@link GameState#battles}
	 */
	public void end() {
		for (Client user : users.values()) {
			user.setGameRoom(null);
			user.stopTimers();
		}
		Packet clientExitedBattlePacket = PacketFactory.createNewPacket(Packet.CLIENT_EXITED_BATTLE_PACKET);
		((ClientExitedBattlePacket) clientExitedBattlePacket).setUserId(0);
		connection.broadcast(clientExitedBattlePacket, users);
		users.clear();
	}

	@Override
	public void processSpellCast(Client caster) {
		if (caster.getPoints().size() == 0) 
			return;
		
		Spell spellCast = caster.classifyGesture();
		if (spellCast == null)
			return;
			
		GameServer.logDebug("cast.server.Spell cast: " + spellCast.getName() + " distance: " + caster.getGestureDistance());
		
		caster.modifyLifeAmount(spellCast.getSelfLife());
		caster.modifyEnergyAmount(spellCast.getSelfEnergy());
		for (Client enemy : getUsers().values()) {
			if (enemy.getId() == caster.getId())
				continue;
			enemy.modifyLifeAmount(spellCast.getOpponentLife());
			enemy.modifyEnergyAmount(spellCast.getOpponentEnergy());
		}
		Packet spellCastPacket = PacketFactory.createNewPacket(Packet.SPELL_CAST);
		((SpellCastPacket) spellCastPacket).setCasterId(caster.getId());
		((SpellCastPacket) spellCastPacket).setSpellName(spellCast.getName());
		((SpellCastPacket) spellCastPacket).setTarget(spellCast.getTarget());

		queuePacket(spellCastPacket);
	}

}
