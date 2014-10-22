package cast.common.packets;

import java.nio.ByteBuffer;

public class AuthenticationFromServerPacket extends Packet {

	/**
	 * Username not registered or password doesn't match username.
	 */
	public static final int WRONG_USER_OR_PASSWORD = -1;
	/**
	 * Username not registered.
	 */
	public static final int WRONG_USER = -2;
	/**
	 * Password doesn't match username.
	 */
	public static final int WRONG_PASSWORD = -3;

	private int userId;
	private float maxLife;
	private float maxEnergy;

	public AuthenticationFromServerPacket() {
		super(AUTHENTICATION_SERVER_PACKET);
	}

	public AuthenticationFromServerPacket(ByteBuffer bb) {
		super(bb);

		userId = getInt();
		if (userId > 0) {
			maxLife = getFloat();
			maxEnergy = getFloat();
		} else {
			maxLife = 0;
			maxEnergy = 0;
		}
	}

	public int getUserId() {
		return userId;
	}

	public float getMaxLife() {
		return maxLife;
	}

	public float getMaxEnergy() {
		return maxEnergy;
	}

	public void setUserId(int userId) {
		this.userId = userId;
		addInt(userId);
	}

	public void setMaxLife(float maxLife) {
		this.maxLife = maxLife;
		addFloat(maxLife);
	}

	public void setMaxEnergy(float maxEnergy) {
		this.maxEnergy = maxEnergy;
		addFloat(maxEnergy);
	}

}
