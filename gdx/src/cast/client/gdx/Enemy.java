package cast.client.gdx;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cast.common.TouchPoint;

public class Enemy {
	private int id;
	private String username;
	private List<TouchPoint> points = Collections.synchronizedList(new LinkedList<TouchPoint>());
	private TouchPoint lastPoint = null;
	public long touchStartTime = 0;
	
	/**
	 * current life
	 */
	private volatile float life;
	/**
	 * current energy
	 */
	private volatile float energy;
	/**
	 * max life
	 */
	private float maxLife;
	/**
	 * max energy
	 */
	private float maxEnergy;

	public Enemy() {
		life = maxLife = 60;
		energy = maxEnergy = 100;
	}

	public Enemy(int id) {
		this();
		setId(id);
	}

	public Enemy(int userId, String username) {
		this(userId);
		this.username = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<TouchPoint> getPoints() {
		return points;
	}

	public TouchPoint addPoint(float x, float y) {
		TouchPoint point = new TouchPoint(x, y, System.currentTimeMillis() - touchStartTime);
		points.add(point);
		lastPoint = point;
		return point;
	}

	public void addPoint(float x, float y, long time) {
		TouchPoint point = new TouchPoint(x, y, time);
		points.add(point);
		lastPoint = point;
	}

	public void clearPoints() {
		points.clear();
		lastPoint = null;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public TouchPoint getLastPoint() {
		return lastPoint;
	}

	/**
	 * Modify life.
	 * 
	 * @param life
	 *            +/- amount to change
	 * @return new value for life
	 */
	public float modifyLifeAmount(float life) {
		this.life += life;
		if (this.life >= maxLife)
			this.life = maxLife;
		if (this.life <= 0) {
			this.life = 0;
		}
		return this.life;
	}

	/**
	 * Modify life.
	 * 
	 * @param life
	 *            value to set to
	 * @return new value for life
	 */
	public float modifyLifeAbsolute(float life) {
		this.life = life;
		if (this.life >= maxLife)
			this.life = maxLife;
		if (this.life <= 0) {
			this.life = 0;
		}
		return this.life;
	}

	public void modifyEnergy(float energy) {
		this.energy += energy;
		if (this.energy <= 0)
			this.energy = 0;
		if (this.energy >= maxEnergy)
			this.energy = maxEnergy;
	}

	/**
	 * Returns current life in percentages
	 * 
	 * @return float - percentages life
	 */
	public float getLife() {
		return (life / maxLife) * 100;
	}

	/**
	 * Returns current energy in percentages
	 * 
	 * @return float - percentages energy
	 */
	public float getEnergy() {
		return (energy / maxEnergy) * 100;
	}

	public void startTouch() {
		touchStartTime = System.currentTimeMillis();
	}

	public void touchEnd() {
		touchStartTime = 0;
	}

	private boolean isAlive() {
		return life > 0;
	}
}
