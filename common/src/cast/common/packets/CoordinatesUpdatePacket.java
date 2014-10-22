package cast.common.packets;

import java.nio.ByteBuffer;

import cast.common.TouchPoint;

public class CoordinatesUpdatePacket extends Packet {

	private int userId;
	private float x;
	private float y;
	private long time;

	public CoordinatesUpdatePacket(int type) {
		super(type);
	}

	public CoordinatesUpdatePacket(ByteBuffer bb) {
		super(bb);

		userId = getInt();
		x = getFloat();
		y = getFloat();
		time = getLong();
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public long getTime() {
		return time;
	}

	// public void setXY(float x, float y) {
	// this.x = x;
	// this.y = y;
	// addFloat(x);
	// addFloat(y);
	// }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
		addInt(userId);
	}

	// public void setTime(long time) {
	// this.time = time;
	// addLong(time);
	// }

	public void setPoint(TouchPoint touchPoint) {
		this.x = touchPoint.getX();
		this.y = touchPoint.getY();
		this.time = touchPoint.getTime();
		addFloat(this.x);
		addFloat(this.y);
		addLong(this.time);
	}

}
