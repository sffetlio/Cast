package cast.common.packets;

import java.nio.ByteBuffer;

public class SpellCastPacket extends Packet {

	private int casterId;
	private String spellName;
	private int target;

	public SpellCastPacket() {
		super(SPELL_CAST);
	}

	public SpellCastPacket(ByteBuffer bb) {
		super(bb);

		casterId = getInt();
		spellName = getString();
		target = getInt();
	}

	public int getCasterId() {
		return casterId;
	}

	public int getTarget() {
		return target;
	}

	public String getSpellName() {
		return spellName;
	}

	public void setCasterId(int casterId) {
		this.casterId = casterId;
		addInt(casterId);
	}

	public void setTarget(int target) {
		this.target = target;
		addInt(target);
	}

	public void setSpellName(String name) {
		this.spellName = name;
		addString(spellName);
	}

	public static final int TARGET_SELF = 1;
	public static final int TARGET_OPPONENT = 2;
}
