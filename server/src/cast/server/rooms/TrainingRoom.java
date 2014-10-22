package cast.server.rooms;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cast.common.TouchPoint;
import cast.common.packets.ClientEnteredBattlePacket;
import cast.common.packets.Packet;
import cast.common.packets.PacketFactory;
import cast.common.packets.ServerTickPacket;
import cast.common.packets.SpellCastPacket;
import cast.server.GameServer;
import cast.server.Spell;
import cast.server.connection.ServerConnection;
import cast.server.players.Client;
import cast.server.players.Trainer;

/**
 * Training room where {@link Client} trains to learn new spells.
 */
public class TrainingRoom extends GameRoom {
	
	private Trainer trainer;
	private Client client;
	
	public TrainingRoom(ServerConnection connection, int id) {
		super(connection, id, 10);
	}
	
	public void addTrainer(){
		trainer = new Trainer();

		// send the AI client to other client
		Packet clientEnteredBattlePacket = PacketFactory.createNewPacket(Packet.CLIENT_ENTERED_BATTLE_PACKET);
		((ClientEnteredBattlePacket) clientEnteredBattlePacket).setUserId(trainer.getId());
		((ClientEnteredBattlePacket) clientEnteredBattlePacket).setUsername("");
		connection.write(client.getKey(), clientEnteredBattlePacket);
	}
	
	@Override
	public void processRoom() {
		ServerTickPacket serverPacket = (ServerTickPacket) PacketFactory.createNewPacket(Packet.SERVER_TICK_PACKET);
		
		List<TouchPoint> points;
		
		// process client
		// TODO send only if changed
		serverPacket.putUserId(client.getId());
		serverPacket.putLife(client.getLife());
		serverPacket.putEnergy(client.getEnergyAmount());
		serverPacket.putPointsCount(0);
//
//		List<cast.common.TouchPoint> points = client.getPoints();
//		synchronized (points) {
//			if (points.size() > 0) {
//				// first loop to get the point to send count
//				Iterator<cast.common.TouchPoint> i = points.iterator();
//				int count = 0;
//				while (i.hasNext()) {
//					cast.common.TouchPoint nextPoint = i.next();
//					if (!nextPoint.isProcessed())
//						++count;
//				}
//				serverPacket.putPointsCount(count);
//
//				// second loop to get the actual points
//				i = points.iterator();
//				while (i.hasNext()) {
//					cast.common.TouchPoint nextPoint = i.next();
//					if (nextPoint.isProcessed())
//						continue;
//					nextPoint.setProcessed(true);
//					serverPacket.putPoint(nextPoint);
//				}
//			} else {
//				serverPacket.putPointsCount(0);
//			}
//		}
		
		// process trainer
		if(System.currentTimeMillis() > trainer.getLastCastTime() + 3000){
			trainer.castSpell();
//		}
			
//		if(!trainer.finishedCasting()){
			serverPacket.putUserId(trainer.getId());
			serverPacket.putLife(0);
			serverPacket.putEnergy(0);
			
			points = trainer.getPoints();
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
					nextPoint.setProcessed(true);
					serverPacket.putPoint(nextPoint);
				}
			} else {
				serverPacket.putPointsCount(0);
			}
			trainer.castDone();
		}
		
		if (!serverPacket.isEmpty()) {
			connection.write(client.getKey(), serverPacket);
		}

		Packet packet;
		while ((packet = getNextPacket()) != null) {
			connection.write(client.getKey(), packet);
		}
	}

	/**
	 * Add client in training room
	 * @param client
	 */
	@Override
	public void addUser(Client client) {
		this.client = client;
	}

	/**
	 * Returns the only client in the training room
	 * @return Client
	 */
	public Client getUser() {
		return this.client;
	}
	
	/**
	 * @deprecated Use {@link cast.server.rooms.TrainingRoom#getUser()}.</br>
	 * Returns the only {@link Client} in the training room.
	 * @param key set this to null
	 * @return Client
	 */
	@Override
	@Deprecated
	public Client getUser(SelectionKey key) {
		return getUser();
	}
	
	/**
	 * Removes the only {@link Client}
	 * @return Client
	 */
	public Client removeUser() {
		Client tmp = client;
		client = null;
		return tmp;
	}

	/**
	 * @deprecated Use {@link cast.server.rooms.TrainingRoom#removeUser()}.</br>
	 * Removes the only {@link Client}
	 * @param key set this to null
	 * @return Client
	 */
	@Override
	@Deprecated
	public Client removeUser(SelectionKey key) {
		return removeUser();
	}

	@Override
	public void end() {
		client.setGameRoom(null);
		client.stopTimers();
		client = null;
		trainer = null;
	}

	@Override
	public Map<SelectionKey, Client> getUsers() {
		Map<SelectionKey, Client> users = new HashMap<SelectionKey, Client>();
		users.put(client.getKey(), client);
		return users;
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
		
		trainer.changeSpell();

		Packet spellCastPacket = PacketFactory.createNewPacket(Packet.SPELL_CAST);
		((SpellCastPacket) spellCastPacket).setCasterId(caster.getId());
		((SpellCastPacket) spellCastPacket).setSpellName(spellCast.getName());
		((SpellCastPacket) spellCastPacket).setTarget(spellCast.getTarget());

		queuePacket(spellCastPacket);
	}

}
