package cast.client.gdx.managers;

import cast.client.gdx.Cast;
import cast.client.gdx.managers.SoundManager.CastSound;
import cast.client.gdx.utils.LRUCache;
import cast.client.gdx.utils.LRUCache.CacheEntryRemovedListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

/**
 * A service that manages the sound effects.
 */
public class SoundManager implements CacheEntryRemovedListener<CastSound, Sound>, Disposable {
	/**
	 * The available sound files.
	 */
	public enum CastSound {
		CLICK("sound/click.wav");

		private final String fileName;

		private CastSound(String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return fileName;
		}
	}

	/**
	 * The volume to be set on the sound.
	 */
	private float volume = 1f;

	/**
	 * Whether the sound is enabled.
	 */
	private boolean enabled = true;

	/**
	 * The sound cache.
	 */
	private final LRUCache<CastSound, Sound> soundCache;

	/**
	 * Creates the sound manager.
	 */
	public SoundManager() {
		soundCache = new LRUCache<CastSound, Sound>(10);
		soundCache.setEntryRemovedListener(this);
	}

	/**
	 * Plays the specified sound.
	 */
	public void play(CastSound sound) {
		// check if the sound is enabled
		if (!enabled)
			return;

		// try and get the sound from the cache
		Sound soundToPlay = soundCache.get(sound);
		if (soundToPlay == null) {
			FileHandle soundFile = Gdx.files.internal(sound.getFileName());
			soundToPlay = Gdx.audio.newSound(soundFile);
			soundCache.add(sound, soundToPlay);
		}

		// play the sound
		Gdx.app.log(Cast.LOG, "Playing sound: " + sound.name());
		soundToPlay.play(volume);
	}

	/**
	 * Sets the sound volume which must be inside the range [0,1].
	 */
	public void setVolume(float volume) {
		Gdx.app.log(Cast.LOG, "Adjusting sound volume to: " + volume);

		// check and set the new volume
		if (volume < 0 || volume > 1f) {
			throw new IllegalArgumentException("The volume must be inside the range: [0,1]");
		}
		this.volume = volume;
	}

	/**
	 * Enables or disabled the sound.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	// EntryRemovedListener implementation

	@Override
	public void notifyEntryRemoved(CastSound key, Sound value) {
		Gdx.app.log(Cast.LOG, "Disposing sound: " + key.name());
		value.dispose();
	}

	/**
	 * Disposes the sound manager.
	 */
	public void dispose() {
		Gdx.app.log(Cast.LOG, "Disposing sound manager");
		for (Sound sound : soundCache.retrieveAll()) {
			sound.stop();
			sound.dispose();
		}
	}
}
