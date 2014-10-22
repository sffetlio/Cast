package cast.client.gdx;

import java.util.Iterator;
import java.util.LinkedList;

import cast.client.gdx.connection.ClientConnectionThread;
import cast.common.TouchPoint;
import cast.common.packets.AuthenticationFromServerPacket;
import cast.common.packets.ClientEnteredBattlePacket;
import cast.common.packets.ClientExitedBattlePacket;
import cast.common.packets.ClientsListPacket;
import cast.common.packets.ClientsListPacket.User;
import cast.common.packets.EnterBattlePacket;
import cast.common.packets.Packet;
import cast.common.packets.ServerTickPacket;
import cast.common.packets.SpellCastPacket;

import com.badlogic.gdx.Gdx;

public class PacketProcessor {

	private ClientConnectionThread connection;
	private Cast game;
	private World world;

	public PacketProcessor(Cast game, ClientConnectionThread connection) {
		this.game = game;
		this.connection = connection;
	}

	public void processPacket(Packet packet) {
		if (packet == null) {
			return;
		}
		int userId = 0;
		switch (packet.getType()) {
			case Packet.AUTHENTICATION_SERVER_PACKET:
				userId = ((AuthenticationFromServerPacket) packet).getUserId();
				if (userId == AuthenticationFromServerPacket.WRONG_USER_OR_PASSWORD) {
					game.getNativeHandler().hideLoading();
					game.getNativeHandler().showAlert("Cannot log in", "Wrong username or password.");
					Cast.log("Wrong username or password.");
				} else {
					game.player.setId(userId);
					game.player.setMaxLife(((AuthenticationFromServerPacket) packet).getMaxLife());
					game.player.setMaxEnergy(((AuthenticationFromServerPacket) packet).getMaxEnergy());
					Cast.log("Auth. with the server. ID: " + userId);
					game.switchScreen(Cast.MENU_SCREEN);
					game.getNativeHandler().hideLoading();
				}
				break;
			case Packet.SERVER_TICK_PACKET:
				LinkedList<ServerTickPacket.User> users = ((ServerTickPacket) packet).getUsers();
				for (Iterator<ServerTickPacket.User> userIterator = users.iterator(); userIterator.hasNext();) {
					ServerTickPacket.User userFromPacket = userIterator.next();
					// process enemies
					if (getWorld().player.getId() != userFromPacket.getId()) {
						Enemy enemy = getWorld().enemies.get(userFromPacket.getId());
						if (enemy != null) {
							enemy.modifyLifeAbsolute(userFromPacket.getLife());
							LinkedList<TouchPoint> enemyPoints = userFromPacket.getPoints();
							synchronized (enemyPoints) {
								if (enemy.getPoints().size() == 0 && enemyPoints.size() > 0) {
									enemy.startTouch();
								}
								for (Iterator<TouchPoint> pointIterator = enemyPoints.iterator(); pointIterator.hasNext();) {
									TouchPoint point = pointIterator.next();
									enemy.addPoint(point.getX(), point.getY(), point.getTime());
								}
							}
						}
					} else {
						getWorld().player.modifyLifeAbsolute(userFromPacket.getLife());
						getWorld().player.modifyEnergyAbsolute(userFromPacket.getEnergy());
					}
				}
				break;
			case Packet.CLIENT_ENTERED_BATTLE_PACKET:
				userId = ((ClientEnteredBattlePacket) packet).getUserId();
				String username = ((ClientEnteredBattlePacket) packet).getUsername();
				getWorld().enemies.put(userId, new Enemy(userId, username));
				Cast.log("New user connected: " + userId + ":" + username);
				break;
			case Packet.CLIENTS_LIST_PACKET:
				Cast.log("CLIENTS_LIST_PACKET packet received ");
				LinkedList<User> usersList = ((ClientsListPacket) packet).getUsers();
				for (Iterator usersIterator = usersList.iterator(); usersIterator.hasNext();) {
					ClientsListPacket.User user = (ClientsListPacket.User) usersIterator.next();
					if (!getWorld().enemies.containsKey(user.getId())) {
						getWorld().enemies.put(user.getId(), new Enemy(user.getId(), user.getUsername()));
						Cast.log("Already connected : " + user.getId() + ":" + user.getUsername());
					}
				}
				break;
			case Packet.SPELL_CAST:
				// this client is the caster
				if (getWorld().player.getId() == ((SpellCastPacket) packet).getCasterId()) {
					if (((SpellCastPacket) packet).getTarget() == SpellCastPacket.TARGET_SELF) {
						if (!getWorld().player.isAlive()) {
							game.getNativeHandler().showAlert("Game over", "You have been defeated!");
						}
						Cast.log("Casted: " + ((SpellCastPacket) packet).getSpellName());
					}
				} else {
					// caster is an opponent
					if (((SpellCastPacket) packet).getTarget() == SpellCastPacket.TARGET_OPPONENT) {
						Gdx.input.vibrate(100);
						CameraHelper.shakeCamera();
					}
				}
				break;
			case Packet.ENTER_BATTLE_PACKET:
				int battleId = ((EnterBattlePacket) packet).getBattleId();
				int backgroundId = ((EnterBattlePacket) packet).getBackgroundId();
				world = game.createWorld(backgroundId);
				game.switchScreen(Cast.GAME_SCREEN);
				break;
			case Packet.CLIENT_EXITED_BATTLE_PACKET:
				// Battle ended, so clear state and return to lobby
				// getWorld().enemies.remove(((ClientExitedBattlePacket)
				// packet).getUserId());
				getWorld().enemies.clear();
				game.switchScreen(Cast.LOBY_SCREEN);
				game.getNativeHandler().showAlert("Enemy left", "Enemy has left the battle.");
				break;
			default:
				break;
		}
	}

	private World getWorld() {
		return world;
	}

}
