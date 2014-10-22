package cast.client.gdx.screens;

import java.util.Locale;

import cast.client.gdx.Cast;
import cast.client.gdx.managers.MusicManager.CastMusic;
import cast.client.gdx.managers.SoundManager.CastSound;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class OptionsScreen extends AbstractScreen {

	private final Label volumeValue;
	private final CheckBox soundEffectsCheckbox;
	private final CheckBox musicCheckbox;
	private final Slider volumeSlider;

	public OptionsScreen(Cast castGame) {
		super(castGame);

		soundEffectsCheckbox = new CheckBox("", getSkin());
		soundEffectsCheckbox.setChecked(game.getPreferencesManager().isSoundEnabled());
		musicCheckbox = new CheckBox("", getSkin());
		musicCheckbox.setChecked(game.getPreferencesManager().isMusicEnabled());
		volumeSlider = new Slider(0f, 1f, 0.1f, getSkin());
		volumeSlider.setValue(game.getPreferencesManager().getVolume());
		volumeValue = new Label("", getSkin());
		updateVolumeLabel();

		// set listeners
		soundEffectsCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean enabled = soundEffectsCheckbox.isChecked();
				game.getPreferencesManager().setSoundEnabled(enabled);
				game.getSoundManager().setEnabled(enabled);
				game.getSoundManager().play(CastSound.CLICK);
			}
		});
		musicCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean enabled = musicCheckbox.isChecked();
				game.getPreferencesManager().setMusicEnabled(enabled);
				game.getMusicManager().setEnabled(enabled);
				game.getSoundManager().play(CastSound.CLICK);

				// if the music is now enabled, start playing the menu music
				if (enabled)
					game.getMusicManager().play(CastMusic.MENU);
			}
		});
		volumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float value = ((Slider) actor).getValue();
				game.getPreferencesManager().setVolume(value);
				game.getMusicManager().setVolume(value);
				game.getSoundManager().setVolume(value);
				updateVolumeLabel();
			}
		});
	}

	@Override
	public void show() {
		super.show();

		Table table = super.getTable();
		table.defaults().spaceBottom(15);
		table.columnDefaults(0).padRight(20);
		table.add("Options").colspan(3).padRight(0);

		table.row();
		table.add("Sound Effects");
		table.add(soundEffectsCheckbox).colspan(2).left();

		table.row();
		table.add("Music");
		table.add(musicCheckbox).colspan(2).left();

		table.row();
		table.add("Volume");
		table.add(volumeSlider);
		table.add(volumeValue).width(40);
	}

	/**
	 * Updates the volume label next to the slider.
	 */
	void updateVolumeLabel() {
		float volume = (game.getPreferencesManager().getVolume() * 100);
		volumeValue.setText(String.format(Locale.US, "%1.0f%%", volume));
	}

	@Override
	public void processBackKey() {
		game.switchScreen(Cast.MENU_SCREEN);
	}
}
