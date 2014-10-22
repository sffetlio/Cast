package cast.common.packets;

import java.nio.ByteBuffer;

public class AuthenticationFromClientPacket extends Packet {

	private String username;
	private String password;

	public AuthenticationFromClientPacket() {
		super(AUTHENTICATION_CLIENT_PACKET);
	}

	public AuthenticationFromClientPacket(ByteBuffer bb) {
		super(bb);

		username = getString();
		password = getString();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
		addString(username);
	}

	public void setPassword(String password) {
		this.password = password;
		addString(password);
	}

}
