package cast.common.packets;

import java.nio.ByteBuffer;

public class EnterBattlePacket extends Packet {

	private int battleId;
	private int backgroundId;

	public EnterBattlePacket() {
		super(ENTER_BATTLE_PACKET);
	}

	public EnterBattlePacket(ByteBuffer bb) {
		super(bb);

		battleId = getInt();
		backgroundId = getInt();
	}

	public int getBattleId() {
		return battleId;
	}

	public int getBackgroundId() {
		return backgroundId;
	}

	public void setBattleId(int battleId) {
		this.battleId = battleId;
		addInt(battleId);
	}

	public void setBackgroundId(int backgroundId) {
		this.backgroundId = backgroundId;
		addInt(backgroundId);
	}

}
