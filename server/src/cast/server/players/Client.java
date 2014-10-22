package cast.server.players;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import cast.common.TouchPoint;
import cast.server.GameState;
import cast.server.Settings;
import cast.server.Spell;
import cast.server.gestures.Dollar1Classifier;
import cast.server.gestures.Gesture;

/**
 * Human player.
 */
public class Client extends Player {

	private String username;
	private String password;
	private String nickname;
	/**
	 * Connection key. Used for sending and receiving data.
	 */
	private SelectionKey key;
	/**
	 * Spells that current user is allowed to cast.
	 */
	private HashMap<Integer, Spell> spells;
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
	private TimerTask lifeTask;
	private TimerTask energyTask;
	private Timer timer;
	private Gesture gesture;
	private Dollar1Classifier classifier;

	public Client(SelectionKey key) {
		super();
		this.key = key;
		spells = null;
		life = maxLife;
		energy = maxEnergy;

		gesture = new Gesture();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SelectionKey getKey() {
		return key;
	}

	public void setKey(SelectionKey key) {
		this.key = key;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	/**
	 * 
	 * @return absolute life amount
	 */
	public float getLife() {
		return life;
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
			die();
		}
		return this.life;
	}

	/**
	 * Mark user as dead. Set life to 0 and stop timers.
	 */
	private void die() {
		life = 0;
		stopTimers();
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
			die();
		}
		return this.life;
	}

	/**
	 * Returns current energy in percentages
	 * 
	 * @return float - energy (0f - 1f)
	 */
	public float getEnergy() {
		return energy / maxEnergy;
	}

	/**
	 * Returns current energy amount
	 * 
	 * @return float - energy amount
	 */
	public float getEnergyAmount() {
		return energy;
	}

	public void modifyEnergyAmount(float energy) {
		this.energy += energy;
		if (this.energy <= 0)
			this.energy = 0;
		if (this.energy >= maxEnergy)
			this.energy = maxEnergy;
	}

	public void startTimers(Timer timer) {
		life = maxLife;
		energy = maxEnergy;

		this.timer = timer;
		lifeTask = new TimerTask() {
			@Override
			public void run() {
				if (life < maxLife) {
					life += lifeRegen / 2;
				}
			}
		};

		energyTask = new TimerTask() {
			@Override
			public void run() {
				if (energy < maxEnergy) {
					energy += energyRegen / 2;
				}
			}
		};

		timer.scheduleAtFixedRate(lifeTask, 0, 500);
		timer.scheduleAtFixedRate(energyTask, 0, 500);
	}

	public void stopTimers() {
		if (lifeTask != null)
			lifeTask.cancel();
		if (energyTask != null)
			energyTask.cancel();
		if (timer != null)
			timer.purge();
	}

	public void disconected() {
		stopTimers();
		GameState.removeUserFromBattle(this);
	}

	/**
	 * Load max life from cast.server.db
	 * 
	 * @param string
	 */
	public void setMaxLife(float life) {
		maxLife = life;
	}

	public float getMaxLife() {
		return maxLife;
	}

	/**
	 * Load max energy from cast.server.db
	 * 
	 * @param energy
	 */
	public void setMaxEnergy(float energy) {
		maxEnergy = energy;
	}

	public float getMaxEnergy() {
		return maxEnergy;
	}

	public Spell classifyGesture() {
		gesture.reset();
		if (getPoints().size() < 6) {
			return null;
		}
		for (Iterator<TouchPoint> iterator = getPoints().iterator(); iterator.hasNext();) {
			TouchPoint point = iterator.next();
			// here cast.server.Settings.VIRTUAL_HEIGHT - point.getY() is to correct
			// coordinates:
			// client starts from bottom left, but training program starts top
			// left (0,0)
			gesture.addPoint(point.getX(), Settings.VIRTUAL_HEIGHT - point.getY());
		}
		int spellId = classifier.classify(gesture);
		return spells.get(spellId);
	}

	public double getGestureDistance() {
		return classifier.getCurrentDistance();
	}

	/**
	 * Load all spells that the current user is allowed to cast.
	 * 
	 * @param spell
	 */
	public void setSpells(HashMap<Integer, Spell> spells) {
		this.spells = spells;
		classifier = new Dollar1Classifier(this.spells);
		classifier.setMaximumDistance(15);
	}

}
