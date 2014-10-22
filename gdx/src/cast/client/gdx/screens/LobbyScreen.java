package cast.client.gdx.screens;

import cast.client.gdx.Cast;
import cast.client.gdx.connection.ClientConnectionThread;
import cast.client.gdx.managers.SoundManager.CastSound;
import cast.common.packets.EnterBattlePacket;
import cast.common.packets.Packet;
import cast.common.packets.PacketFactory;

import com.badlogic.gdx.scenes.scene2d.ActorEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class LobbyScreen extends AbstractScreen {
	private TextButton fightAiButton;
	private TextButton findOpponentButton;
	private TextButton practiceButton;

	public LobbyScreen(Cast castGame, final ClientConnectionThread connection) {
		super(castGame);

		fightAiButton = new TextButton("Fight AI", getSkin());
		findOpponentButton = new TextButton("Find opponent", getSkin());
		practiceButton = new TextButton("Practice room", getSkin());

		fightAiButton.addListener(new DefaultActorListener() {
			@Override
			public void touchUp(ActorEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				game.getSoundManager().play(CastSound.CLICK);
				
				Packet enterBattlePacket = PacketFactory.createNewPacket(Packet.ENTER_BATTLE_PACKET);
				((EnterBattlePacket) enterBattlePacket).setBattleId(2);
				((EnterBattlePacket) enterBattlePacket).setBackgroundId(0);
				connection.write(enterBattlePacket);
			}
		});
		findOpponentButton.addListener(new DefaultActorListener() {
			@Override
			public void touchUp(ActorEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				game.getSoundManager().play(CastSound.CLICK);

				Packet enterBattlePacket = PacketFactory.createNewPacket(Packet.ENTER_BATTLE_PACKET);
				((EnterBattlePacket) enterBattlePacket).setBattleId(0);
				((EnterBattlePacket) enterBattlePacket).setBackgroundId(0);
				connection.write(enterBattlePacket);
			}
		});
		practiceButton.addListener(new DefaultActorListener() {
			@Override
			public void touchUp(ActorEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				game.getSoundManager().play(CastSound.CLICK);

				Packet startTrainingPacket = PacketFactory.createNewPacket(Packet.START_TRAINING_PACKET);
				connection.write(startTrainingPacket);
			}
		});
	}

	@Override
	public void show() {
		super.show();
		Table table = super.getTable();

		table.row();
		table.add(findOpponentButton).size(300, 60).uniform().spaceBottom(10);
		
		table.row();
		table.add(fightAiButton).size(300, 60).uniform().spaceBottom(10);

		table.row();
		table.add(practiceButton).size(300, 60).uniform().spaceBottom(10);
	}

	@Override
	public void processBackKey() {
		game.switchScreen(Cast.MENU_SCREEN);
	}
}
