package cast.trainer;

import java.util.LinkedList;

import cast.trainer.utils.Gesture;

public class GestureGroup {
	String name;
	LinkedList<Gesture> gestures;

	public GestureGroup(String name) {
		this.name = name;
		gestures = new LinkedList<Gesture>();
	}

	public void rename(String name) {
		this.name = name;
	}
}
