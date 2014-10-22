package cast.server;

import java.io.IOException;

import cast.server.connection.ServerConnection;
import cast.server.db.Database;

public class GameServer {

	private static final boolean DEBUG_MODE = true;
	private static Database db;
	private static ServerConnection serverConnection;
	private static GameState gameState;

	public static void main(String[] args) throws IOException {
		db = new Database();

		serverConnection = new ServerConnection();
		new Thread(serverConnection, "Connection Thread").start();

		gameState = new GameState(serverConnection);
		new Thread(gameState, "Game State Thread").start();
	}

	public static Database getDatabase() {
		return db;
	}

	public static ServerConnection getServerConnection() {
		return serverConnection;
	}

	public static void logDebug(String str) {
		if (DEBUG_MODE) {
			System.out.println("Debug: " + str);
		}
	}

}