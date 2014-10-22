package cast.common.packets;

import java.nio.ByteBuffer;

public class StartTrainingPacket extends Packet {

	public StartTrainingPacket() {
		super(START_TRAINING_PACKET);
	}

	public StartTrainingPacket(ByteBuffer bb) {
		super(bb);
	}

	public void setBattleId(int battleId) {
	}

}
