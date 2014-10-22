package cast.client.gdx.screens;

import cast.client.gdx.Cast;
import cast.client.gdx.connection.ClientConnectionThread;
import cast.client.gdx.managers.SoundManager.CastSound;
import cast.common.packets.AuthenticationFromClientPacket;
import cast.common.packets.Packet;
import cast.common.packets.PacketFactory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ActorEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LoginScreen extends AbstractScreen {

	private ClientConnectionThread connection;

	private final TextField usernameField;
	private final TextField passwordField;
	private final CheckBox rememberUsernameCheckbox;
	private final CheckBox rememberPasswordCheckbox;
	private final TextField ipField;
	private final TextButton loginButton;

	public LoginScreen(Cast castGame) {
		super(castGame);

		usernameField = new TextField("", getSkin());
		if (game.getPreferencesManager().getRememberUsername()) {
			usernameField.setText(game.getPreferencesManager().getUsername());
		}
		passwordField = new TextField("", getSkin());
		if (game.getPreferencesManager().getRememberPassword()) {
			passwordField.setText(game.getPreferencesManager().getPassword());
		}
		rememberUsernameCheckbox = new CheckBox("", getSkin());
		rememberUsernameCheckbox.setChecked(game.getPreferencesManager().getRememberUsername());
		rememberPasswordCheckbox = new CheckBox("", getSkin());
		rememberPasswordCheckbox.setChecked(game.getPreferencesManager().getRememberPassword());
		ipField = new TextField(game.getPreferencesManager().getIp(), getSkin());
		loginButton = new TextButton("Enter", getSkin());

		// set listeners
		rememberUsernameCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean enabled = rememberUsernameCheckbox.isChecked();
				game.getPreferencesManager().setRememberUsername(enabled);
				rememberPasswordCheckbox.setChecked(false);
				game.getPreferencesManager().setRememberPassword(false);
				game.getPreferencesManager().setUsername("");
				game.getPreferencesManager().setPassword("");
			}
		});
		rememberPasswordCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean enabled = rememberPasswordCheckbox.isChecked();
				game.getPreferencesManager().setRememberPassword(enabled);
				rememberUsernameCheckbox.setChecked(true);
				game.getPreferencesManager().setRememberUsername(true);
				game.getPreferencesManager().setPassword("");
			}
		});
		ipField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped(TextField textField, char key) {
				game.getPreferencesManager().setIp(textField.getText());
			}
		});
		loginButton.addListener(new DefaultActorListener() {
			@Override
			public void touchUp(ActorEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				game.getSoundManager().play(CastSound.CLICK);

				game.getNativeHandler().showLoading("", "Please wait...");

				// save username if checkbox is ticked
				if (game.getPreferencesManager().getRememberUsername()) {
					game.getPreferencesManager().setUsername(usernameField.getText());
				} else {
					game.getPreferencesManager().setUsername("");
				}

				// save password if checkbox is ticked
				if (game.getPreferencesManager().getRememberPassword()) {
					game.getPreferencesManager().setPassword(passwordField.getText());
				} else {
					game.getPreferencesManager().setPassword("");
				}

				connection = game.getConnection();

				login(usernameField.getText(), passwordField.getText());
			}
		});
	}

	@Override
	public void show() {
		super.show();

		// Create table
		Table table = super.getTable();
		table.defaults().spaceBottom(15);
		table.columnDefaults(0).padRight(20);
		table.add("Login").colspan(2).padRight(0);

		table.row();
		table.add("Username");
		table.add(usernameField).left();

		table.row();
		table.add("Password");
		table.add(passwordField).left();

		table.row();
		table.add("Remember username");
		table.add(rememberUsernameCheckbox).left();

		table.row();
		table.add("Remember password");
		table.add(rememberPasswordCheckbox).left();

		table.row();
		table.add("Server IP");
		table.add(ipField).colspan(2).left().fill();

		table.row();
		table.add(loginButton).colspan(2).uniform().fill().padRight(0);
	}

	@Override
	public void processBackKey() {
		game.confirmExit();
	}

	public void login(String username, String password) {
		if (connection == null) { // || (connection != null &&
									// !connection.isConnected())
			game.getNativeHandler().showAlert("Unknown Host", "Unknown host: " + game.getPreferencesManager().getIp());
			Cast.log("Unknown host: " + game.getPreferencesManager().getIp());
			game.getNativeHandler().hideLoading();
			return;
		} else {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			if (!connection.isConnected()) {
				game.getNativeHandler().showAlert("Cannot find server", "Cannot find server at: " + connection.getServerAddress());
				Cast.log("Cannot find server at: " + connection.getServerAddress());
				game.getNativeHandler().hideLoading();
				return;
			}
		}

		Packet packet = PacketFactory.createNewPacket(Packet.AUTHENTICATION_CLIENT_PACKET);
		((AuthenticationFromClientPacket) packet).setUsername(username);
		((AuthenticationFromClientPacket) packet).setPassword(password);
		connection.write(packet);
	}
}
