package cast.common.packets;

import java.nio.ByteBuffer;

public class ExitBattlePacket extends Packet {

	public ExitBattlePacket() {
		super(EXIT_BATTLE_PACKET);
	}

	public ExitBattlePacket(ByteBuffer bb) {
		super(bb);
	}
}
