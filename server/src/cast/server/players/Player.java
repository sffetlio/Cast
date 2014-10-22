package cast.server.players;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cast.common.TouchPoint;
import cast.server.rooms.GameRoom;

/**
 * Abstract base class representing a player.
 */
public abstract class Player {

	protected int id;
	protected List<TouchPoint> points;
	protected GameRoom gameRoom;
	
	public Player() {
		points = Collections.synchronizedList(new LinkedList<TouchPoint>());
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public synchronized List<TouchPoint> getPoints() {
		return points;
	}

	public synchronized void addPoint(float x, float y, long time) {
		points.add(new TouchPoint(x, y, time));
	}

	public GameRoom getGameRoom() {
		return gameRoom;
	}

	public void setGameRoom(GameRoom gameRoom) {
		this.gameRoom = gameRoom;
	}
	
}
