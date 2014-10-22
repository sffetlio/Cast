package cast.server;

public class Settings {

	/**
	 * Server listen port
	 */
	public static final int PORT = 4444;

	// cast.server.db settings
	public static final String DB_ADDRESS = "vps.spopov.eu";
	public static final String DB_PORT = "3306";
	public static final String DB_USER = "";
	public static final String DB_PASS = "";
	public static final String DB_DATABASE = "cast";
	public static final String DB_URL = "jdbc:mysql://" + DB_ADDRESS + ":" + DB_PORT + "/" + DB_DATABASE;

	// when changing these, change them in client side.
	// needed for calculating x,y from top left instead of bottom left corner
	// server side
	public static final int VIRTUAL_WIDTH = 480;
	public static final int VIRTUAL_HEIGHT = 320;

	/**
	 * Max points to receive from a client. Points after that are discarded.
	 */
	public static final int POINTS_TO_RECEIVE = 100;
}