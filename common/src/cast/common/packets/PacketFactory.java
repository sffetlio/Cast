package cast.common.packets;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Returns packet object based on passed type or ByteBuffer structure.
 */
public class PacketFactory {

	/**
	 * Creates new packet object based on passed type.
	 * 
	 * @param type
	 * @return Packet
	 * 
	 * @see Packet
	 */
	public static Packet createNewPacket(int type) {
		switch (type) {
			case Packet.AUTHENTICATION_CLIENT_PACKET:
				return new AuthenticationFromClientPacket();
			case Packet.AUTHENTICATION_SERVER_PACKET:
				return new AuthenticationFromServerPacket();
			case Packet.TOUCH_START_PACKET:
			case Packet.TOUCH_MOVE_PACKET:
			case Packet.TOUCH_END_PACKET:
				return new CoordinatesUpdatePacket(type);
			case Packet.SERVER_TICK_PACKET:
				return new ServerTickPacket();
			case Packet.CLIENT_ENTERED_BATTLE_PACKET:
				return new ClientEnteredBattlePacket();
			case Packet.CLIENT_EXITED_BATTLE_PACKET:
				return new ClientExitedBattlePacket();
			case Packet.CLIENTS_LIST_PACKET:
				return new ClientsListPacket();
			case Packet.SPELL_CAST:
				return new SpellCastPacket();
			case Packet.ENTER_BATTLE_PACKET:
				return new EnterBattlePacket();
			case Packet.START_TRAINING_PACKET:
				return new StartTrainingPacket();
			case Packet.EXIT_BATTLE_PACKET:
				return new ExitBattlePacket();
			default:
				return null;
		}
	}

	/**
	 * Reads first 4 bytes (one int) from the begining of the ByteBuffer,
	 * converts it to Packet.type and returns new Packet object filled with the
	 * rest of the ByteBuffer.
	 * 
	 * @param message
	 * @return Packet
	 * 
	 * @see Packet
	 */
	public static Packet getPacketByFirstBytes(ByteBuffer message) {
		// message.rewind();
		int type = 0;
		try {
			type = message.getInt();
		} catch (BufferUnderflowException e) {
			System.out.println("Packet problem: can't get type");
			e.printStackTrace();
			return null;
		}

		switch (type) {
			case Packet.AUTHENTICATION_CLIENT_PACKET:
				return new AuthenticationFromClientPacket(message);
			case Packet.AUTHENTICATION_SERVER_PACKET:
				return new AuthenticationFromServerPacket(message);
			case Packet.TOUCH_START_PACKET:
			case Packet.TOUCH_MOVE_PACKET:
			case Packet.TOUCH_END_PACKET:
				return new CoordinatesUpdatePacket(message);
			case Packet.SERVER_TICK_PACKET:
				return new ServerTickPacket(message);
			case Packet.CLIENT_ENTERED_BATTLE_PACKET:
				return new ClientEnteredBattlePacket(message);
			case Packet.CLIENT_EXITED_BATTLE_PACKET:
				return new ClientExitedBattlePacket(message);
			case Packet.CLIENTS_LIST_PACKET:
				return new ClientsListPacket(message);
			case Packet.SPELL_CAST:
				return new SpellCastPacket(message);
			case Packet.ENTER_BATTLE_PACKET:
				return new EnterBattlePacket(message);
			case Packet.START_TRAINING_PACKET:
				return new StartTrainingPacket(message);
			case Packet.EXIT_BATTLE_PACKET:
				return new ExitBattlePacket(message);
			default:
				System.out.println("Unknown type");
				return null;
		}
	}

}
