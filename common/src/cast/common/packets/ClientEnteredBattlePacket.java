package cast.common.packets;

import java.nio.ByteBuffer;

public class ClientEnteredBattlePacket extends Packet {

	private int userId;
	private String username;

	public ClientEnteredBattlePacket() {
		super(CLIENT_ENTERED_BATTLE_PACKET);
	}

	public ClientEnteredBattlePacket(ByteBuffer bb) {
		super(bb);

		userId = getInt();
		username = getString();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
		addInt(userId);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		addString(username);
	}
}
