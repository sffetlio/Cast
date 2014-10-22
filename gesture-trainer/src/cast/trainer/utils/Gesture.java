package cast.trainer.utils;

import java.awt.Point;
import java.util.LinkedList;

public class Gesture {
	private LinkedList<Point> points;

	public Gesture() {
		points = new LinkedList<Point>();
	}

	public LinkedList<Point> getPoints() {
		return points;
	}

}
