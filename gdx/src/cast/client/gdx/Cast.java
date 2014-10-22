package cast.client.gdx;

import java.net.UnknownHostException;

import cast.client.gdx.android.NativeHandler;
import cast.client.gdx.android.ConfirmInterface;
import cast.client.gdx.connection.ClientConnectionThread;
import cast.client.gdx.managers.MusicManager;
import cast.client.gdx.managers.PreferencesManager;
import cast.client.gdx.managers.SoundManager;
import cast.client.gdx.screens.GameScreen;
import cast.client.gdx.screens.LobbyScreen;
import cast.client.gdx.screens.LoginScreen;
import cast.client.gdx.screens.MenuScreen;
import cast.client.gdx.screens.OptionsScreen;
import cast.client.gdx.screens.SplashScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class Cast extends Game {

	public static boolean DEV_MODE = true;
	public static final String LOG = "Cast log";

	private ClientConnectionThread connection;
	private World world;

	private PreferencesManager preferencesManager;
	private SoundManager soundManager;
	private MusicManager musicManager;
	private NativeHandler handler;

	/**
	 * Screen to show. If 0 - no change. Otherwise show that screen. After
	 * change, goes back to 0.
	 */
	private volatile int screenToShow = 0;
	public Player player;

	public static final int NO_SCREEN_CHANGE = 0;
	public static final int SPLASH_SCREEN = 1;
	public static final int LOGIN_SCREEN = 2;
	public static final int MENU_SCREEN = 3;
	public static final int LOBY_SCREEN = 4;
	public static final int GAME_SCREEN = 5;
	public static final int OPTIONS_SCREEN = 6;

	/**
	 * Constructor called from the desktop project.
	 */
	public Cast() {
		// remove requirement that images have power of 2 dimensions
		Texture.setEnforcePotImages(false);

		handler = new DesktopHandler();
	}

	/**
	 * Constructor called from the android project.<br>
	 * Note: sets {@link cast.client.gdx.Cast#DEV_MODE} to false.
	 * 
	 * @param handler
	 *            - {@link cast.client.gdx.android.NativeHandler} Pointer to android application. Used
	 *            to handle native android functionality.
	 */
	public Cast(NativeHandler handler) {
		this();
		this.handler = handler;
		DEV_MODE = false;
	}

	public PreferencesManager getPreferencesManager() {
		return preferencesManager;
	}

	public MusicManager getMusicManager() {
		return musicManager;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}

	/**
	 * Used only from {@link cast#render()} to change current screen based on
	 * {@link cast.client.gdx.Cast#screenToShow}<br>
	 * This is workaround to change screens from threads with no openGl context
	 * (e.g. {@link cast.client.gdx.connection.ClientConnectionThread})
	 */
	private void switchScreen() {
		if (screenToShow == 0)
			return;

		switch (screenToShow) {
			case SPLASH_SCREEN:
				setScreen(new SplashScreen(this));
				break;
			case LOGIN_SCREEN:
				setScreen(new LoginScreen(this));
				break;
			case MENU_SCREEN:
				setScreen(new MenuScreen(this));
				break;
			case LOBY_SCREEN:
				setScreen(new LobbyScreen(this, connection));
				break;
			case GAME_SCREEN:
				setScreen(new GameScreen(this, connection));
				break;
			case OPTIONS_SCREEN:
				setScreen(new OptionsScreen(this));
				break;
			default:
				break;
		}

		screenToShow = 0;
	}

	/**
	 * Used to change current screen.
	 * 
	 * @param screen
	 *            - screen to show
	 */
	public void switchScreen(int screen) {
		screenToShow = screen;
	}

	@Override
	public void create() {
		preferencesManager = new PreferencesManager();

		musicManager = new MusicManager();
		musicManager.setVolume(preferencesManager.getVolume());
		musicManager.setEnabled(preferencesManager.isMusicEnabled());

		soundManager = new SoundManager();
		soundManager.setVolume(preferencesManager.getVolume());
		soundManager.setEnabled(preferencesManager.isSoundEnabled());

		player = new Player();

		if (DEV_MODE) {
			switchScreen(LOGIN_SCREEN);
		} else {
			switchScreen(SPLASH_SCREEN);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		Cast.log("Resizing game to: " + width + " x " + height);
	}

	@Override
	public void render() {
		super.render();
		switchScreen();
	}

	@Override
	public void pause() {
		super.pause();
		if (DEV_MODE)
			Cast.log("Pausing game");
	}

	@Override
	public void resume() {
		super.resume();
		if (DEV_MODE)
			Cast.log("Resuming game");
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
		if (DEV_MODE)
			Cast.log("Setting screen: " + screen.getClass().getSimpleName());
	}

	@Override
	public void dispose() {
		super.dispose();
		if (DEV_MODE)
			Cast.log("Disposing game");

		musicManager.dispose();
		soundManager.dispose();
	}

	public static void log(String str) {
		Gdx.app.log(Cast.LOG, str);
	}

	public NativeHandler getNativeHandler() {
		return handler;
	}

	/**
	 * Show native android confirm dialog on exit.
	 * 
	 * @see cast.client.gdx.android.NativeHandler
	 * @see cast.client.gdx.android.ConfirmInterface
	 */
	public void confirmExit() {
		getNativeHandler().showConfirm(new ConfirmInterface() {
			@Override
			public void yes() {
				Gdx.app.exit();
			}

			@Override
			public void no() {
			}
		}, "Exit?", "Are toy sure you want to exit?");
	}

	public World getWorld() {
		return world;
	}

	public World createWorld(int background) {
		world = new World(this, background);
		return world;
	}

	public ClientConnectionThread getConnection() {
		if (connection == null) {
			try {
				connection = new ClientConnectionThread(this);
				new Thread(connection).start();
			} catch (UnknownHostException e) {
				connection = null;
			}
		}
		return connection;
	}

}
