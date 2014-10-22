package cast.client.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class CameraHelper {

	private static OrthographicCamera camera;
	/**
	 * Bigger number - smaller amplitude.<br/>
	 * Default 300f.
	 */
	private static float shakeAmplitude = 300.0f;
	/**
	 * Reset to default to start shake animation and rendering.<br/>
	 * Default -40.0f.
	 */
	private static volatile float shakeTimeElapsed = 0f;
	/**
	 * Used to calculate delta angle for camera.rotate().
	 */
	private static float lastAngle = 0;
	private static float xLetterbox = 0;
	private static float yLetterbox = 0;
	private static float scale = 1;

	public static OrthographicCamera getCamera(float virtualWidth, float virtualHeight) {
		float viewportWidth = virtualWidth;
		float viewportHeight = virtualHeight;
		float physicalWidth = Gdx.graphics.getWidth();
		float physicalHeight = Gdx.graphics.getHeight();
		float aspect = virtualWidth / virtualHeight;
		scale = 1;

		// This is to maintain the aspect ratio.
		// If the virtual aspect ration does not match with the aspect ratio
		// of the hardware screen then the viewport would scaled to
		// meet the size of one dimension and other would not cover full
		// dimension
		// If we stretch it to meet the screen aspect ratio then textures will
		// get distorted either become fatter or elongated
		if (physicalWidth / physicalHeight >= aspect) {
			viewportWidth = viewportHeight * physicalWidth / physicalHeight;
			viewportHeight = virtualHeight;

			scale = physicalHeight / viewportHeight;

			// Letterbox left and right.
			xLetterbox = (viewportWidth - virtualWidth) / 2;
		} else {
			viewportWidth = virtualWidth;
			viewportHeight = viewportWidth * physicalWidth / physicalHeight;

			scale = physicalWidth / viewportWidth;

			// Letterbox above and below.
			yLetterbox = (viewportHeight - virtualHeight) / 2;
		}

		camera = new OrthographicCamera(viewportWidth, viewportHeight);
		camera.position.set(virtualWidth / 2, virtualHeight / 2, 0);
		camera.update();
		return camera;
	}

	public static void unproject(Vector3 touchPoint) {
		if (camera != null) {
			camera.unproject(touchPoint);
		}
	}

	public static OrthographicCamera getCamera() {
		return camera;
	}

	public static void renderCameraShake(float deltaTime) {
		if (shakeTimeElapsed >= 0) {
			return;
		}
		shakeTimeElapsed += deltaTime * 100; // speed
		float angle = (float) (shakeTimeElapsed * shakeTimeElapsed * Math.sin(shakeTimeElapsed));
		camera.rotate((float) (angle - lastAngle) / shakeAmplitude);
		lastAngle = angle;
		camera.update();
	}

	public static void shakeCamera() {
		shakeTimeElapsed = -40.0f;
	}

	public static float getScale() {
		return scale;
	}

	public static float getLetterBoxX() {
		return xLetterbox;
	}

	public static float getLetterBoxY() {
		return yLetterbox;
	}
}
