package cast.server.gestures;

import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * A gesture, i.e. a vector of points with their input time.
 */
public class Gesture {

	protected Vector<Point2D> points;
	protected Point2D min, max; /* bounding box */

	/**
	 * Builds a new gesture.
	 */
	public Gesture() {
		points = new Vector<Point2D>();
		min = new Point2D.Double();
		max = new Point2D.Double();
	}

	/**
	 * Clears this gesture.
	 */
	public void reset() {
		min = new Point2D.Double();
		max = new Point2D.Double();
		points.clear();
	}

	/**
	 * Adds a point to this gesture.
	 * 
	 * @param x
	 *            The x coordinate of the point to add.
	 * @param y
	 *            The y coordinate of the point to add.
	 * @return This gesture.
	 */
	public final Gesture addPoint(float x, float y) {
		addPoint(new Point2D.Double(x, y));
		return this;
	}

	void write(DataOutputStream out) throws IOException {
		out.writeInt(points.size());
		out.writeDouble(min.getX());
		out.writeDouble(min.getY());
		out.writeDouble(max.getX());
		out.writeDouble(max.getY());
		Point2D nextPoint;
		for (Iterator<Point2D> iteratorPoints = points.iterator(); iteratorPoints.hasNext();) {
			nextPoint = iteratorPoints.next();
			out.writeDouble(nextPoint.getX());
			out.writeDouble(nextPoint.getY());
		}
	}

	Object read(DataInputStream in) throws IOException {
		int nPoints = in.readInt();
		// ignore min, max
		// they will be initialized when rebuilding gesture with addPoint method
		for (int i = 0; i < 4; i++)
			in.readDouble();
		points = new Vector<Point2D>();
		for (int i = 0; i < nPoints; i++) {
			Point2D pt = new Point2D.Double(in.readDouble(), in.readDouble());
			addPoint(pt);
		}
		return this;
	}

	protected void addPoint(Point2D p) {
		if (points.size() == 0) {
			min.setLocation(p.getX(), p.getY());
			max.setLocation(p.getX(), p.getY());
		} else {
			min.setLocation(Math.min(p.getX(), min.getX()), Math.min(p.getY(), min.getY()));
			max.setLocation(Math.max(p.getX(), max.getX()), Math.max(p.getY(), max.getY()));
		}
		points.add(p);
	}

	/**
	 * @return The first point of this gesture, null if this gesture is empty.
	 */
	public Point2D getStart() {
		if (points.size() == 0)
			return null;
		return points.get(0);
	}

	/**
	 * @return The last point of this gesture, null if this gesture is empty.
	 */
	public Point2D getEnd() {
		if (points.size() == 0)
			return null;
		return points.get(points.size() - 1);
	}

	/**
	 * @return the points in this gesture.
	 */
	public Vector<Point2D> getPoints() {
		return points;
	}

	/**
	 * @return The upper left point of this gesture's bounding box.
	 */
	public Point2D getMin() {
		return min;
	}

	/**
	 * @return The lower right point of this gesture's bounding box.
	 */
	public Point2D getMax() {
		return max;
	}

}
