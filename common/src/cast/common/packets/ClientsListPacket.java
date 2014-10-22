package cast.common.packets;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class ClientsListPacket extends Packet {

	public class User {
		private int id;
		private String username;

		public User(int id, String username) {
			this.setId(id);
			this.setUsername(username);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
	}

	private LinkedList<User> users = new LinkedList<User>();

	public ClientsListPacket() {
		super(CLIENTS_LIST_PACKET);
	}

	public ClientsListPacket(ByteBuffer bb) {
		super(bb);

		while (hasMore()) {
			getUsers().add(new User(getInt(), getString()));
		}
	}

	public void addUser(int id, String username) {
		addInt(id);
		addString(username);
	}

	public LinkedList<User> getUsers() {
		return users;
	}

}