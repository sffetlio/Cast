package cast.server.gestures;

/**
 * A score is a couple (int, double).
 */
public class Score {

	int classId;
	double score;

	/**
	 * Builds a Score.
	 * 
	 * @param classId
	 *            The score classId.
	 * @param score
	 *            The score value.
	 */
	public Score(int name, double score) {
		super();
		this.classId = name;
		this.score = score;
	}

	/**
	 * @return this score value.
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @return this score classId.
	 */
	public int getName() {
		return classId;
	}

}
