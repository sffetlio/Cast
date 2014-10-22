package cast.client.gdx.screens;

import cast.client.gdx.Cast;
import cast.client.gdx.managers.SoundManager.CastSound;

import com.badlogic.gdx.scenes.scene2d.ActorEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuScreen extends AbstractScreen {
	private TextButton startGameButton;
	private TextButton optionsButton;

	public MenuScreen(Cast castGame) {
		super(castGame);

		startGameButton = new TextButton("Start game", getSkin());
		optionsButton = new TextButton("Options", getSkin());

		startGameButton.addListener(new DefaultActorListener() {
			@Override
			public void touchUp(ActorEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				game.getSoundManager().play(CastSound.CLICK);
				game.switchScreen(Cast.LOBY_SCREEN);
			}
		});
		optionsButton.addListener(new DefaultActorListener() {
			@Override
			public void touchUp(ActorEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				game.getSoundManager().play(CastSound.CLICK);
				game.switchScreen(Cast.OPTIONS_SCREEN);
			}
		});
	}

	@Override
	public void show() {
		super.show();

		Table table = super.getTable();
		table.add("Welcome to Cast").spaceBottom(50);

		table.row();
		table.add(startGameButton).size(300, 60).uniform().spaceBottom(10);

		table.row();
		table.add(optionsButton).uniform().fill().spaceBottom(10);
	}

	@Override
	public void processBackKey() {
		game.confirmExit();
	}
}
