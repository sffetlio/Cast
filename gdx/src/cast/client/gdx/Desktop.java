package cast.client.gdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Desktop {
	public static void main(String[] args) {
		new LwjglApplication(new Cast(), "CastGL", Settings.VIRTUAL_WIDTH, Settings.VIRTUAL_HEIGHT, true);
		// new LwjglApplication(new Cast(), "CastGL", 480, 320, true);
	}
}
