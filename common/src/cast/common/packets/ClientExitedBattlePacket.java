package cast.common.packets;

import java.nio.ByteBuffer;

public class ClientExitedBattlePacket extends Packet {

	private int userId;

	public ClientExitedBattlePacket() {
		super(CLIENT_EXITED_BATTLE_PACKET);
	}

	public ClientExitedBattlePacket(ByteBuffer bb) {
		super(bb);

		userId = getInt();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
		addInt(userId);
	}
}
