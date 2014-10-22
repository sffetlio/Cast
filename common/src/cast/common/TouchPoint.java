package cast.common;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents point on the screen with coordinates and timestamp.
 */
public class TouchPoint {
	private float x;
	private float y;
	private long time;
	private boolean processed = false;

	public TouchPoint(float x, float y) {
		this.setX(x);
		this.setY(y);
	}

	public TouchPoint(float x, float y, long time) {
		this.setX(x);
		this.setY(y);
		this.setTime(time);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Vector3 getVector3() {
		return new Vector3(x, y, 0);
	}

	public Vector2 getVector2() {
		return new Vector2(x, y);
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * If the point has been processed in some way (is sent; is displayed)
	 * 
	 * @return boolean processed flag
	 */
	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public float getDistane(TouchPoint t2) {
		return this.getVector2().dst(t2.getVector2());
	}

}
