package cast.server.gestures;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import cast.server.Spell;

/**
 * A classifier that implements $1 algorithm to classify cast.server.gestures.
 */
public class Dollar1Classifier {

	class Dollar1GestureClass extends GestureClass {

		private Vector<Vector<Point2D>> resampledGestures = new Vector<Vector<Point2D>>();

		Dollar1GestureClass() {
			super();
		}

		Dollar1GestureClass(int name) {
			super(name);
		}

		/**
		 * {@inheritDoc} Each time a gesture is added, a vector of points
		 * corresponding to this gesture as resampled, rotated and scaled is
		 * computed and stored in <code>resampledGestures</code>.
		 */
		public void addExample(Gesture gesture) {
			super.addExample(gesture);
			Vector<Point2D> newPoints = new Vector<Point2D>();
			GestureUtils.resample(gesture.getPoints(), Dollar1Classifier.this.getNbPoints(), newPoints);
			// GestureUtils.rotateToZero(newPoints, newPoints);
			GestureUtils.scaleToSquare(newPoints, Dollar1Classifier.this.getSizeScaleToSquare(), newPoints);
			GestureUtils.translateToOrigin(newPoints, newPoints);
			resampledGestures.add(newPoints);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean removeExample(Gesture gesture) {
			if (!gestures.contains(gesture))
				return false;
			int index = gestures.indexOf(gesture);
			if (index != -1)
				resampledGestures.remove(index);
			return super.removeExample(gesture);
		}

		/**
		 * @return The vector of gesture examples as resampled, rotated and scaled.
		 * @see cast.server.gestures.Dollar1Classifier.Dollar1GestureClass#addExample(Gesture)
		 */
		public Vector<Vector<Point2D>> getResampledGestures() {
			return resampledGestures;
		}

		/**
		 * @return The average vector of this class. A point#i in this vector is the gravity center of points#i of all examples.
		 */
		public Vector<Point2D> getAverage() {
			int nbPoints = Dollar1Classifier.this.getNbPoints();
			Vector<Point2D> average = new Vector<Point2D>(nbPoints);
			double sumX, sumY;
			for (int i = 0; i < nbPoints; i++) {
				sumX = 0;
				sumY = 0;
				for (Iterator<Vector<Point2D>> iterator = resampledGestures.iterator(); iterator.hasNext();) {
					Point2D pt = iterator.next().get(i);
					sumX += pt.getX();
					sumY += pt.getY();
				}
				average.add(new Point2D.Double(sumX / resampledGestures.size(), sumY / resampledGestures.size()));
			}
			return average;
		}

	}

	protected ArrayList<Dollar1GestureClass> classes = new ArrayList<Dollar1GestureClass>();

	private double theta = Math.PI / 4;
	private double deltaTheta = Math.PI / 90;

	private double currentDistance = -1;
	private double maximumDistance = 30;
	private double sizeScaleToSquare = 100;

	private int nbPoints = 64;

	protected ArrayList<Integer> classesNames = new ArrayList<Integer>();
	protected ArrayList<Vector<Point2D>> templates = new ArrayList<Vector<Point2D>>();

	public Dollar1Classifier(HashMap<Integer, Spell> spells) {
		for (Spell spell : spells.values()) {
			addClass(spell.getId());
			for (Gesture gesture : spell.getGestureExamples()) {
				addExample(spell.getId(), gesture);
			}
		}
	}

	/**
	 * Recognizes a gesture.
	 * 
	 * @param g The gesture to recognize
	 * @return The classId of the class of cast.server.gestures that best fit to g.
	 */
	public int classify(Gesture g) {
		double minScore = Double.MAX_VALUE;
		double currentScore;
		GestureClass recognized = null;

		Vector<Point2D> inputPointsResampled = new Vector<Point2D>();
		GestureUtils.resample(g.getPoints(), nbPoints, inputPointsResampled);
		// GestureUtils.rotateToZero(inputPointsResampled,
		// inputPointsResampled);
		GestureUtils.scaleToSquare(inputPointsResampled, sizeScaleToSquare, inputPointsResampled);
		GestureUtils.translateToOrigin(inputPointsResampled, inputPointsResampled);

		for (Iterator<Dollar1GestureClass> classesIterator = classes.iterator(); classesIterator.hasNext();) {
			Dollar1GestureClass nextClass = classesIterator.next();
			for (Iterator<Vector<Point2D>> gesturesIterator = nextClass.getResampledGestures().iterator(); gesturesIterator.hasNext();) {
				Vector<Point2D> gesturePoints = gesturesIterator.next();
				// currentScore =
				// GestureUtils.distanceAtBestAngle(inputPointsResampled,
				// gesturePoints, -theta, theta, deltaTheta);
				currentScore = GestureUtils.distanceWithNoRotation(inputPointsResampled, gesturePoints);
				if (currentScore < minScore) {
					minScore = currentScore;
					recognized = nextClass;
				}
			}
		}
		currentDistance = minScore;
		if (currentDistance > maximumDistance)
			return 0;
		return recognized.getName();
	}

	/**
	 * Adds a class of cast.server.gestures to this classifier.
	 * 
	 * @param className The classId of the class of cast.server.gestures to add.
	 * @return the index of this class in the list of classes (-1 if this class already exists and thus has not been added).
	 */
	public int addClass(int classId) {
		classesNames.add(classId);
		templates.add(null);
		int index = classesNames.size() - 1;

		if (index == -1)
			return -1;
		Dollar1GestureClass gcr = new Dollar1GestureClass(classId);
		classes.add(gcr);
		return index;
	}

	/**
	 * Removes a class of cast.server.gestures from this classifier.
	 * 
	 * @param className The classId of the class of cast.server.gestures to remove.
	 */
	public void removeClass(String className) {
		int index = classesNames.indexOf(className);
		if (index == -1)
			return;
		classesNames.remove(index);
		templates.remove(index);
		classes.remove(index);
	}

	/**
	 * @return The number of points used for resampling a gesture during $1 recognition process.
	 */
	public int getNbPoints() {
		return nbPoints;
	}

	/**
	 * @return The size of the bounding box side used for rescaling a gesture during $1 recognition process.
	 */
	public double getSizeScaleToSquare() {
		return sizeScaleToSquare;
	}

	/**
	 * @return The maximum score threshold for recognition.
	 */
	public double getMaximumDistance() {
		return maximumDistance;
	}

	/**
	 * Sets a minimum score threshold for recognition. If the distance is
	 * greater than this maximum distance, the gesture is not recognized (i.e.
	 * method <code>classify</code> returns null.
	 * 
	 * @param maximumDistance The minimum score threshold for recognition.
	 */
	public void setMaximumDistance(double maximumDistance) {
		this.maximumDistance = maximumDistance;
	}

	/**
	 * @return The distance of the last recognized gesture.
	 */
	public double getCurrentDistance() {
		return currentDistance;
	}

	/**
	 * Computes a sorted list of classes contained in this recognizer from the
	 * best match to the the worst match given a gesture.
	 * 
	 * @param g The gesture
	 * @return a vector of scores for all the classes registered in this classifier sorted from the best match (index 0) to the worst
	 * match (index n-1), with n the number of classes. A score is a couple (class_name, distance).
	 */
	public Vector<Score> sortedClasses(Gesture g) {
		Vector<Score> sortedClasses = new Vector<Score>();

		Vector<Point2D> inputPointsResampled = new Vector<Point2D>();
		GestureUtils.resample(g.getPoints(), nbPoints, inputPointsResampled);
		// GestureUtils.rotateToZero(inputPointsResampled,
		// inputPointsResampled);
		GestureUtils.scaleToSquare(inputPointsResampled, sizeScaleToSquare, inputPointsResampled);
		GestureUtils.translateToOrigin(inputPointsResampled, inputPointsResampled);

		double score;
		double minClassScore = 0;
		for (int nc = 0; nc < classes.size(); nc++) {
			minClassScore = Integer.MAX_VALUE;
			for (Iterator<Vector<Point2D>> gesturesIterator = classes.get(nc).getResampledGestures().iterator(); gesturesIterator.hasNext();) {
				Vector<Point2D> gesturePoints = gesturesIterator.next();
				score = GestureUtils.distanceAtBestAngle(inputPointsResampled, gesturePoints, -theta, theta, deltaTheta);
				if (score < minClassScore)
					minClassScore = score;
			}
			if (nc == 0) {
				sortedClasses.add(new Score(classes.get(nc).getName(), minClassScore));
			} else {
				int i = 0;
				while (i < sortedClasses.size() && sortedClasses.get(i).getScore() < minClassScore)
					i++;
				sortedClasses.add(i, new Score(classes.get(nc).getName(), minClassScore));
			}
		}

		return sortedClasses;
	}

	/**
	 * Removes a gesture example from this classifier.
	 * 
	 * @param gesture the gesture to remove
	 */
	public void removeExample(Gesture gesture) {
		for (Iterator<Dollar1GestureClass> iterator = classes.iterator(); iterator.hasNext();) {
			Dollar1GestureClass next = iterator.next();
			if (next != null)
				next.removeExample(gesture);
		}
	}

	/**
	 * Adds a gesture example to this classifier.
	 * 
	 * @param className the gesture example's class
	 * @param example the gesture example
	 */
	public void addExample(int classId, Gesture example) {
		int index = classesNames.indexOf(classId);
		if (index == -1)
			return;
		Dollar1GestureClass gestureClass = classes.get(index);
		if (gestureClass != null)
			gestureClass.addExample(example);
	}

	/**
	 * Renames a class of cast.server.gestures.
	 * 
	 * @param previousClassName The current classId of this class of cast.server.gestures
	 * @param newClassName The new classId of this class of cast.server.gestures
	 */
	public void renameClass(String previousClassName, int newClassName) {
		int index = classesNames.indexOf(previousClassName);
		if (index == -1)
			return;
		Dollar1GestureClass gc = classes.get(index);
		gc.setName(newClassName);

		classesNames.set(index, newClassName);
	}

	/**
	 * Resets this classifier (i.e. removes all the classes of cast.server.gestures).
	 */
	public void reset() {
		classesNames.clear();
		templates.clear();
		classes.clear();
	}

	/**
	 * Returns the vector of gesture examples for a given class.
	 * 
	 * @param className The classId of the class
	 * @return The set of examples for the class <code>className</code>.
	 * @throws UnsupportedOperationException
	 */
	public Vector<Gesture> getExamples(String className) throws UnsupportedOperationException {
		int index = classesNames.indexOf(className);
		if (index == -1)
			return null;
		Dollar1GestureClass gc = classes.get(index);
		return gc.getGestures();
	}
}
