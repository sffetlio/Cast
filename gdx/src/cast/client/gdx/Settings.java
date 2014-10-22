package cast.client.gdx;

public class Settings {

	// when changing these, change them in server side.
	// needed for calculating x,y from top left instead of bottom left corner
	// server side
	public static final int VIRTUAL_WIDTH = 480;
	public static final int VIRTUAL_HEIGHT = 320;

	/**
	 * Introduce lag in opponent drawing to compensate for netowrk lag and
	 * server tick interval.
	 */
	public static final long SERVER_LAG = 100;
	/**
	 * Limit points sent to server
	 */
	public static final int POINTS_TO_SEND = 100;

}