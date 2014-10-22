package cast.client.gdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Handles the game preferences.
 */
public class PreferencesManager {
	private static final String PREFS_NAME = "cast";
	private static final String PREF_VOLUME = "volume";
	private static final String PREF_MUSIC_ENABLED = "music.enabled";
	private static final String PREF_SOUND_ENABLED = "sound.enabled";
	private static final String PREF_IP = "ip";
	private static final String REMEMBER_USERNAME = "remember.username";
	private static final String REMEMBER_PASSWORD = "remember.password";
	private static final String USERNAME = "user.username";
	private static final String PASSWORD = "user.password";
	private Preferences preferences;

	public PreferencesManager() {
	}

	protected Preferences getPrefs() {
		if (preferences == null) {
			preferences = Gdx.app.getPreferences(PREFS_NAME);
		}

		return preferences;
	}

	public boolean isSoundEnabled() {
		return getPrefs().getBoolean(PREF_SOUND_ENABLED, true);
	}

	public void setSoundEnabled(boolean enabled) {
		getPrefs().putBoolean(PREF_SOUND_ENABLED, enabled);
		getPrefs().flush();
	}

	public boolean isMusicEnabled() {
		return getPrefs().getBoolean(PREF_MUSIC_ENABLED, true);
	}

	public void setMusicEnabled(boolean enabled) {
		getPrefs().putBoolean(PREF_MUSIC_ENABLED, enabled);
		getPrefs().flush();
	}

	public float getVolume() {
		return getPrefs().getFloat(PREF_VOLUME, 0.5f);
	}

	public void setVolume(float volume) {
		getPrefs().putFloat(PREF_VOLUME, volume);
		getPrefs().flush();
	}

	public void setIp(String ip) {
		getPrefs().putString(PREF_IP, ip);
		getPrefs().flush();
	}

	public String getIp() {
		return getPrefs().getString(PREF_IP);
	}

	public void setRememberUsername(boolean enabled) {
		getPrefs().putBoolean(REMEMBER_USERNAME, enabled);
		getPrefs().flush();
	}

	public boolean getRememberUsername() {
		return getPrefs().getBoolean(REMEMBER_USERNAME);
	}

	public void setRememberPassword(boolean enabled) {
		getPrefs().putBoolean(REMEMBER_PASSWORD, enabled);
		getPrefs().flush();
	}

	public boolean getRememberPassword() {
		return getPrefs().getBoolean(REMEMBER_PASSWORD);
	}

	public void setUsername(String username) {
		getPrefs().putString(USERNAME, username);
		getPrefs().flush();
	}

	public String getUsername() {
		return getPrefs().getString(USERNAME);
	}

	public void setPassword(String password) {
		getPrefs().putString(PASSWORD, password);
		getPrefs().flush();
	}

	public String getPassword() {
		return getPrefs().getString(PASSWORD);
	}

}
