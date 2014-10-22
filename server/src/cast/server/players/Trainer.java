package cast.server.players;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import cast.server.GameState;
import cast.server.Settings;
import cast.server.Spell;
import cast.server.rooms.TrainingRoom;

/**
 * AI player similar to {@link AIEnemy}, but used {@link TrainingRoom}. It casts spells but with no damage or energy. It's used only to display the gesture of a spell.
 */
public class Trainer extends Player {
	
	private long lastCastTime;
	private Vector<Point2D> selectedSpell;
	
	public Trainer() {
		super();
		setId(1);
		setLastCastTime(0);
	}
	
	public void castSpell(){
		if(selectedSpell == null){
			changeSpell();
		}
		
		setLastCastTime(System.currentTimeMillis());
		getPoints().clear();
		int time = 0;
		for (Point2D point : selectedSpell) {
			addPoint((float) point.getX(), (float)( Settings.VIRTUAL_HEIGHT - point.getY()), time);
			time += 10;
		}
		addPoint((float) 0, (float) 0, time);
	}

	/**
	 * When was the last time the {@link cast.server.players.Trainer} cast a spell.
	 * @return
	 */
	public long getLastCastTime() {
		return lastCastTime;
	}

	/**
	 * Set the last time the {@link cast.server.players.Trainer} cast a spell.
	 * @param lastCastTime
	 */
	public void setLastCastTime(long lastCastTime) {
		this.lastCastTime = lastCastTime;
	}

	public void changeSpell() {
		HashMap<Integer, Spell> spells = GameState.getSpells();
		int randomSpellId = new Random(System.currentTimeMillis()).nextInt(spells.size()) + 1;
		selectedSpell = spells.get(randomSpellId).getGestureExamples().get(0).getPoints();
	}

	public void castDone() {
		getPoints().clear();
	}

}
