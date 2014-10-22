package cast.client.gdx;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cast.common.TouchPoint;

public class Player {

	private int id;
	private String username;
	private List<TouchPoint> points = Collections.synchronizedList(new LinkedList<TouchPoint>());
	private TouchPoint lastPoint = null;
	private Timer timer;
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
	/**
	 * Life regeneration per second.
	 */
	private float lifeRegen = 0.5f;
	/**
	 * Energy regeneration per second.
	 */
	private float energyRegen = 5f;

	public long touchStartTime = 0;
	private TimerTask castInterruptionTimer;
	private volatile boolean canCast = true;

	public Player() {
		life = maxLife = 60;
		energy = maxEnergy = 100;
		timer = new Timer();
	}

	public Player(int id) {
		this();
		setId(id);
	}

	public Player(int userId, String username) {
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
		return addPoint(x, y, System.currentTimeMillis());
		// TouchPoint point = new TouchPoint(x, y, System.currentTimeMillis() -
		// touchStartTime);
		// points.add(point);
		// lastPoint = point;
		// return point;
	}

	public TouchPoint addPoint(float x, float y, long time) {
		TouchPoint point = new TouchPoint(x, y, time - touchStartTime);
		points.add(point);
		lastPoint = point;
		return point;
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

	public void modifyEnergyAbsolute(float energy) {
		this.energy = energy;
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
		// abort casting if this timer runs
		castInterruptionTimer = new TimerTask() {
			@Override
			public void run() {
				stopCasting();
				this.cancel();
			}
		};
		try {
			timer.schedule(castInterruptionTimer, 3000);
		} catch (IllegalStateException e) {
		}
	}

	public void touchEnd() {
		touchStartTime = 0;
		if (castInterruptionTimer != null)
			castInterruptionTimer.cancel();
		timer.purge();
		canCast = true;
	}

	// public void startTimers(){
	// timer.scheduleAtFixedRate(new TimerTask() {
	// @Override
	// public void run() {
	// if(life < maxLife){
	// life += lifeRegen/2;
	// }
	// }
	// }, 0, 500);
	//
	// timer.scheduleAtFixedRate(new TimerTask() {
	// @Override
	// public void run() {
	// if(energy < maxEnergy){
	// energy += energyRegen/2;
	// }
	// }
	// }, 0, 500);
	// }

	// public void stopTimers(){
	// timer.cancel();
	// timer.purge();
	// }

	public void stopCasting() {
		canCast = false;
	}

	public boolean canCast() {
		// return energy > 0 && canCast;
		return isAlive() && canCast;
	}

	public boolean isAlive() {
		return life > 0;
	}

	public void setCanCast(boolean canCast) {
		this.canCast = canCast;
	}

	public void setMaxLife(float maxLife) {
		this.maxLife = maxLife;
	}

	public void setMaxEnergy(float maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

}
