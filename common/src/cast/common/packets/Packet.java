package cast.common.packets;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;

import cast.common.Convertor;

public class Packet {

	/**
	 * Sent after client connects to the server. Includes username, password,
	 * max life and energy.
	 */
	public static final int AUTHENTICATION_CLIENT_PACKET = 1;
	/**
	 * Server respond to AUTHENTICATION_CLIENT_PACKET. Returns userId
	 */
	public static final int AUTHENTICATION_SERVER_PACKET = 2;
	/**
	 * Sent on client touch start
	 */
	public static final int TOUCH_START_PACKET = 3;
	/**
	 * Sent on client touch drag (move)
	 */
	public static final int TOUCH_MOVE_PACKET = 4;
	/**
	 * Sent on client touch end
	 */
	public static final int TOUCH_END_PACKET = 5;
	/**
	 * Server regular update (ticks) to clients. Heartbeat.
	 */
	public static final int SERVER_TICK_PACKET = 6;
	/**
	 * Server send to client information about another client joining the
	 * battle.
	 */
	public static final int CLIENT_ENTERED_BATTLE_PACKET = 7;
	/**
	 * Server send to client information about client leaving the battle.
	 */
	public static final int CLIENT_EXITED_BATTLE_PACKET = 8;
	/**
	 * Server send to client information about currently connected clients.
	 */
	public static final int CLIENTS_LIST_PACKET = 9;
	/**
	 * Send to client on spell cast.
	 */
	public static final int SPELL_CAST = 11;
	/**
	 * Send to server when joining a battle.
	 */
	public static final int ENTER_BATTLE_PACKET = 12;
	/**
	 * Send to server when starting practice battle.
	 */
	public static final int START_TRAINING_PACKET = 13;
	/**
	 * Send to server when exiting a battle.
	 */
	public static final int EXIT_BATTLE_PACKET = 14;

	private int type;
	private LinkedList<byte[]> dataList;
	private int readPointer;
	private byte[] byteArray;
	private int size;

	private static final byte BYTE_TERMINATE = '\000';

	public Packet(int type) {
		this.type = type;
		byteArray = null;
		dataList = new LinkedList<byte[]>();
		size = 0;
		readPointer = 0;

		addInt(type);
	}

	public Packet(ByteBuffer bb) {
		bb.rewind();
		byteArray = bb.array();
		type = bb.getInt();
		dataList = null;
		size = byteArray.length;
		readPointer = 4;
	}

	public int getType() {
		return type;
	}

	public boolean isEmpty() {
		if (size == 0 || size == 4) {
			return true;
		} else {
			return false;
		}
	}

	protected void addInt(Integer i) {
		dataList.add(Convertor.toByta(i));
		size += Integer.SIZE / 8;
	}

	protected void addFloat(Float f) {
		dataList.add(Convertor.toByta(f));
		size += Float.SIZE / 8;
	}

	protected void addLong(long l) {
		dataList.add(Convertor.toByta(l));
		size += Long.SIZE / 8;
	}

	protected void addString(String s) {
		byte[] strBytes = s.getBytes(Charset.forName("UTF-8"));
		byte[] tmp = new byte[strBytes.length + 1];
		System.arraycopy(strBytes, 0, tmp, 0, strBytes.length);
		tmp[strBytes.length] = BYTE_TERMINATE;
		dataList.add(tmp);
		size += tmp.length;
	}

	private byte[] readBytes() {
		int start = readPointer;
		int end = 0;
		byte[] tmpBytes;
		int count;
		for (int i = readPointer; i < size; i++) {
			if (byteArray[i] == BYTE_TERMINATE) {
				end = i;
				tmpBytes = new byte[end - start];
				count = 0;
				for (int j = start; j < end; j++) {
					tmpBytes[count++] = byteArray[j];
				}
				readPointer = i + 1;
				return tmpBytes;
			}
		}
		return null;
	}

	private byte[] readBytes(int count) {
		byte[] tmpBytes = new byte[count];
		int c = 0;
		for (int i = readPointer; i < readPointer + count; i++) {
			tmpBytes[c++] = byteArray[i];
		}
		readPointer += count;
		return tmpBytes;
	}

	protected int getInt() {
		return Convertor.toInt(readBytes(Integer.SIZE / 8));
	}

	protected float getFloat() {
		return Convertor.toFloat(readBytes(Float.SIZE / 8));
	}

	protected long getLong() {
		return Convertor.toLong(readBytes(Long.SIZE / 8));
	}

	protected String getString() {
		byte[] bytes = readBytes();
		if (bytes == null) {
			return new String();
		} else {
			return new String(bytes);
		}
	}

	/**
	 * Convert stored fields to byte[]
	 * 
	 * @return stored fields as byte[]
	 */
	public byte[] getBytes() {
		if (isEmpty()) {
			System.out.println("Sending empty packet");
		}
		if (byteArray != null && byteArray.length > 0) {
			return byteArray;
		}
		byteArray = new byte[size];
		int index = 0;
		for (Iterator<byte[]> iterator = dataList.iterator(); iterator.hasNext();) {
			byte[] bytes = iterator.next();

			System.arraycopy(bytes, 0, byteArray, index, bytes.length);
			index += bytes.length;
		}
		return byteArray;
	}

	public boolean hasMore() {
		return readPointer < size;
	}

}