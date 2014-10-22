package cast.server;

import java.util.Vector;

import cast.server.gestures.Gesture;
import cast.server.gestures.GestureClass;

public class Spell {

	private static final int SELF_TARGET = 1;
	private static final int OPPONENT_TARGET = 2;
	private int id;
	private String name;
	private float selfLife;
	private float selfEnergy;
	private float opponentLife;
	private float opponentEnergy;
	private int target;
	private GestureClass gestureClass;

	public Spell(int id, String name, float selfLife, float selfEnergy, float opponentLife, float opponentEnergy, String target, String gesture) {
		this.id = id;
		this.name = name;
		this.selfLife = selfLife;
		this.selfEnergy = selfEnergy;
		this.opponentLife = opponentLife;
		this.opponentEnergy = opponentEnergy;
		this.target = (target.equalsIgnoreCase("self")) ? Spell.SELF_TARGET : Spell.OPPONENT_TARGET;

		this.gestureClass = new GestureClass(this.id);
		Gesture gestureExample = new Gesture();
		String[] pointsStr = gesture.split(",");
		for (int i = 0; i < pointsStr.length; i++) {
			String[] point = pointsStr[i].split(" ");
			gestureExample.addPoint(Float.parseFloat(point[0]), Float.parseFloat(point[1]));
		}
		this.gestureClass.addExample(gestureExample);
	}

	public String getName() {
		return name;
	}

	public Vector<Gesture> getGestureExamples() {
		return this.gestureClass.getGestures();
	}

	public float getSelfLife() {
		return selfLife;
	}

	public float getSelfEnergy() {
		return selfEnergy;
	}

	public float getOpponentLife() {
		return opponentLife;
	}

	public float getOpponentEnergy() {
		return opponentEnergy;
	}

	public int getTarget() {
		return target;
	}

	public int getId() {
		return id;
	}

}
