package cast.server.gestures;

import java.util.Vector;

/**
 * A basic class of cast.server.gestures.
 */
public class GestureClass {

	protected Vector<Gesture> gestures;
	protected int classId;

	/**
	 * Builds a new labeled class of cast.server.gestures.
	 */
	protected GestureClass() {
		gestures = new Vector<Gesture>();
	}

	/**
	 * Builds a new labeled class of cast.server.gestures.
	 * 
	 * @param n
	 *            The label of the class of cast.server.gestures.
	 */
	public GestureClass(int n) {
		this();
		classId = n;
	}

	/**
	 * Returns the classId of this class of cast.server.gestures.
	 * 
	 * @return the classId of this class of cast.server.gestures.
	 */
	public int getName() {
		return classId;
	}

	/**
	 * Removes an example of this class of cast.server.gestures.
	 * 
	 * @param gesture
	 *            The gesture to remove from this gesture class.
	 * @return True if this gesture has been found in this class and removed,
	 *         false if the gesture has not been found in this class.
	 */
	public boolean removeExample(Gesture gesture) {
		return gestures.remove(gesture);
	}

	/**
	 * Adds an example of this class of cast.server.gestures.
	 * 
	 * @param gesture
	 *            The gesture to add to this gesture class.
	 */
	public void addExample(Gesture gesture) {
		gestures.add(gesture);
	}

	/**
	 * Sets the classId of this gesture class.
	 * 
	 * @param classId
	 *            the classId.
	 */
	public void setName(int name) {
		this.classId = name;
	}

	/**
	 * @return the number of examples contained in this gesture class.
	 */
	public int getNumberOfExamples() {
		return gestures.size();
	}

	/**
	 * @return the set of gesture examples contained in this gesture class.
	 */
	public Vector<Gesture> getGestures() {
		return gestures;
	}

}
