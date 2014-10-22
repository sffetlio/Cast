package cast.server;

import java.nio.channels.SelectionKey;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

import cast.server.connection.ServerConnection;
import cast.server.players.Client;
import cast.server.rooms.BattleRoom;
import cast.server.rooms.GameRoom;
import cast.server.rooms.TrainingRoom;

/**
 * Thread for processing active battles.
 */
public class GameState implements Runnable {

	private static Map<Integer, GameRoom> battles;
	private static Map<SelectionKey, Client> lobbyUsers;
	private static ServerConnection connection;
	private static Timer timer;
	private static HashMap<Integer, Spell> spells;

	public GameState(ServerConnection server) {
		GameState.connection = server;
		battles = Collections.synchronizedMap(new HashMap<Integer, GameRoom>());
		lobbyUsers = Collections.synchronizedMap(new HashMap<SelectionKey, Client>());
		timer = new Timer(true);
		spells = GameServer.getDatabase().loadSpells();
	}

	public static synchronized void addUser(SelectionKey key) {
		Client user = new Client(key);
		lobbyUsers.put(key, user);
	}

	static synchronized Client getUser(SelectionKey key) {
		return lobbyUsers.get(key);
	}

	public static synchronized void removeUser(SelectionKey key) {
		Client user = lobbyUsers.get(key);
		lobbyUsers.remove(key);
		if (user == null)
			return;
		user.disconected();
	}

	@Override
	public void run() {
		while (true) {
			for (GameRoom battle : battles.values()) {
				battle.processRoom();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Add user in battle. Create it if it doesn't exist. Use user id.
	 * 
	 * @param user
	 * @param battle
	 * @return battle
	 */
	public static GameRoom addUserInBattle(Client user, GameRoom battle) {
		if (battle == null) {
			int bgId = new Random(System.currentTimeMillis()).nextInt(4) + 1;
			battle = new BattleRoom(connection, user.getId(), bgId);
			battles.put(battle.getId(), battle);
		}
		user.setGameRoom(battle);
		user.startTimers(timer);
		battle.addUser(user);
		return battle;
	}

	/**
	 * Add user in battle. Create it if it doesn't exist. Use user id.
	 * 
	 * @param user
	 * @param battleId
	 * @return battle
	 */
	public static GameRoom addUserInBattle(Client user, int battleId) {
		GameRoom battle = battles.get(battleId);
		return addUserInBattle(user, battle);
	}
	
	private static TrainingRoom addUserInTraining(Client user) {
		TrainingRoom trainingRoom = new TrainingRoom(connection, user.getId());
		battles.put(trainingRoom.getId(), trainingRoom);
		user.setGameRoom(trainingRoom);
		user.startTimers(timer);
		trainingRoom.addUser(user);
		return trainingRoom;
	}

	/**
	 * Removes user form the battle he is in. Removes other clients. Removes the
	 * battle since we have only 1 user in there.
	 */
	public static void removeUserFromBattle(Client user) {
		GameRoom battle = user.getGameRoom();
		if (battle == null)
			return;
		user.stopTimers();
		battle.end();
		battles.remove(battle.getId());
	}

	/**
	 * Create empty battle or join battle with one client.
	 * 
	 * @param user
	 * @return created or joined BattleRoom
	 */
	public static GameRoom findFreeBattle(Client user) {
		Collection<GameRoom> allBattles = GameState.battles.values();
		synchronized (battles) {
			for (GameRoom battle : allBattles) {
				if (battle.getUsers().size() == 1){
					return addUserInBattle(user, battle);
				}
			}
			return addUserInBattle(user, user.getId());
		}
	}
	
	/**
	 * Create empty training room with one client.
	 * 
	 * @param user
	 * @return TrainingRoom
	 */
	public static TrainingRoom createTrainingRoom(Client user) {
		return  addUserInTraining(user);
	}

	public static HashMap<Integer, Spell> getSpells() {
		return spells;
	}

}
