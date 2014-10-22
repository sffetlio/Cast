package cast.common.packets;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import cast.common.TouchPoint;

public class ServerTickPacket extends Packet {

	public class User {
		private int id;
		private LinkedList<TouchPoint> points = new LinkedList<TouchPoint>();
		private float life;
		private float energy;

		public User(int id) {
			this.setId(id);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public LinkedList<TouchPoint> getPoints() {
			return points;
		}

		public void addPoint(float x, float y, long time) {
			points.add(new TouchPoint(x, y, time));
		}

		public void setLife(float life) {
			this.life = life;
		}

		public float getLife() {
			return life;
		}

		public void setEnergy(float energy) {
			this.energy = energy;
		}

		public float getEnergy() {
			return energy;
		}
	}

	private LinkedList<User> users = new LinkedList<User>();

	public ServerTickPacket() {
		super(SERVER_TICK_PACKET);
	}

	public ServerTickPacket(ByteBuffer bb) {
		super(bb);

		while (hasMore()) {
			User user = new User(getInt());
			user.setLife(getFloat());
			user.setEnergy(getFloat());
			int pointsCount = getInt();
			for (int i = 0; i < pointsCount; i++) {
				user.addPoint(getFloat(), getFloat(), getLong());
			}
			getUsers().add(user);
		}
	}

	public void putUserId(int userId) {
		addInt(userId);
	}

	public void putLife(float life) {
		addFloat(life);
	}

	public void putEnergy(float energy) {
		addFloat(energy);
	}

	public void putPointsCount(int count) {
		addInt(count);
	}

	public void putPoint(TouchPoint touchPoint) {
		addFloat(touchPoint.getX());
		addFloat(touchPoint.getY());
		addLong(touchPoint.getTime());
	}

	public LinkedList<User> getUsers() {
		return users;
	}

}
