package cast.server;

import java.nio.channels.SelectionKey;

import cast.common.packets.AuthenticationFromClientPacket;
import cast.common.packets.AuthenticationFromServerPacket;
import cast.common.packets.ClientEnteredBattlePacket;
import cast.common.packets.ClientsListPacket;
import cast.common.packets.CoordinatesUpdatePacket;
import cast.common.packets.EnterBattlePacket;
import cast.common.packets.Packet;
import cast.common.packets.PacketFactory;
import cast.server.connection.ServerConnection;
import cast.server.players.Client;
import cast.server.rooms.GameRoom;
import cast.server.rooms.TrainingRoom;

public class PacketProcessor {

	private final ServerConnection serverConnection;

	public PacketProcessor(ServerConnection serverConnectionThread) {
		this.serverConnection = serverConnectionThread;
	}

	public void processPacket(Packet packet, SelectionKey key) {
		Packet returnPacket;
		Client player;
		GameRoom gameRoom;

		switch (packet.getType()) {
			case Packet.AUTHENTICATION_CLIENT_PACKET:
				player = GameState.getUser(key);
				String username = ((AuthenticationFromClientPacket) packet).getUsername();
				String password = ((AuthenticationFromClientPacket) packet).getPassword();
				boolean authenticated = GameServer.getDatabase().authenticateUser(player, username, password);

				if (!authenticated) {
					// System.out.println("NOT authenticated: " + username);
					returnPacket = PacketFactory.createNewPacket(Packet.AUTHENTICATION_SERVER_PACKET);
					((AuthenticationFromServerPacket) returnPacket).setUserId(AuthenticationFromServerPacket.WRONG_USER_OR_PASSWORD);
					serverConnection.write(key, returnPacket);
					break;
				}

				returnPacket = PacketFactory.createNewPacket(Packet.AUTHENTICATION_SERVER_PACKET);
				((AuthenticationFromServerPacket) returnPacket).setUserId(player.getId());
				((AuthenticationFromServerPacket) returnPacket).setMaxLife(player.getMaxLife());
				((AuthenticationFromServerPacket) returnPacket).setMaxEnergy(player.getMaxEnergy());
				serverConnection.write(key, returnPacket);
				break;
			case Packet.TOUCH_START_PACKET:
				GameState.getUser(key).getPoints().clear();
			case Packet.TOUCH_MOVE_PACKET:
				if (GameState.getUser(key).getPoints().size() > Settings.POINTS_TO_RECEIVE) {
					break;
				}
				GameState.getUser(key).addPoint(((CoordinatesUpdatePacket) packet).getX(), ((CoordinatesUpdatePacket) packet).getY(), ((CoordinatesUpdatePacket) packet).getTime());
				break;
			case Packet.TOUCH_END_PACKET:
				Client caster = GameState.getUser(key);
				caster.getGameRoom().processSpellCast(caster);
				
				caster.addPoint(((CoordinatesUpdatePacket) packet).getX(), ((CoordinatesUpdatePacket) packet).getY(), ((CoordinatesUpdatePacket) packet).getTime());
				break;
			case Packet.ENTER_BATTLE_PACKET:
				// TODO Do not use battleId to find out what is selected
				// battleId: 0 - find battle btn, 2 - fight ai opponent
				int battleId = ((EnterBattlePacket) packet).getBattleId();
				
				player = GameState.getUser(key);
				if (battleId == 0) {
					gameRoom = GameState.findFreeBattle(player);
				}else if(battleId == 2){
					gameRoom = GameState.addUserInBattle(player, null);
				}else{
					// for now this sould not happen
					gameRoom = null;
					// TODO replace this with: join specified by client battle (implement battle search or list)
				}

				returnPacket = PacketFactory.createNewPacket(Packet.ENTER_BATTLE_PACKET);
				((EnterBattlePacket) returnPacket).setBattleId(gameRoom.getId());

				((EnterBattlePacket) returnPacket).setBackgroundId(gameRoom.getBackgroundId());
				serverConnection.write(key, returnPacket);

				// create list of currently connected clients and send it to the
				// newly connected current client
				ClientsListPacket clientsListPacket = (ClientsListPacket) PacketFactory.createNewPacket(Packet.CLIENTS_LIST_PACKET);
				for (Client otherUser : gameRoom.getUsers().values()) {
					// skip own client
					if (player.getId() == otherUser.getId()) {
						continue;
					}
					clientsListPacket.addUser(otherUser.getId(), otherUser.getUsername());
				}
				// if there are more then current clients, send the packet
				if (!clientsListPacket.isEmpty()) {
					// System.out.println("Sent info about other clients to: " +
					// user.getUsername());
					serverConnection.write(player.getKey(), clientsListPacket);
				}

				// broadcast new connected client to others
				Packet clientEnteredBattlePacket = PacketFactory.createNewPacket(Packet.CLIENT_ENTERED_BATTLE_PACKET);
				((ClientEnteredBattlePacket) clientEnteredBattlePacket).setUserId(player.getId());
				((ClientEnteredBattlePacket) clientEnteredBattlePacket).setUsername(player.getUsername());
				// System.out.println("Sent info about: " + user.getId() + ":" +
				// user.getUsername() + " to other clients");
				serverConnection.broadcast(clientEnteredBattlePacket, gameRoom.getUsers(), key);
				break;
			case Packet.START_TRAINING_PACKET:
				player = GameState.getUser(key);
				TrainingRoom trainingRoom = GameState.createTrainingRoom(player);
				
				returnPacket = PacketFactory.createNewPacket(Packet.ENTER_BATTLE_PACKET);
				((EnterBattlePacket) returnPacket).setBattleId(trainingRoom.getId());
				((EnterBattlePacket) returnPacket).setBackgroundId(trainingRoom.getBackgroundId());
				serverConnection.write(key, returnPacket);
				trainingRoom.addTrainer();
				break;
			case Packet.EXIT_BATTLE_PACKET:
				player = GameState.getUser(key);
				GameState.removeUserFromBattle(player);
				break;
			default:
				break;
		}
	}

}
