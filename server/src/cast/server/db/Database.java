package cast.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import cast.server.GameState;
import cast.server.Settings;
import cast.server.Spell;
import cast.server.players.Client;

public class Database {

	private Connection con;
	private PreparedStatement psSelectUser;
	private PreparedStatement psGetUserSpells;

	public Database() {
		try {
			con = DriverManager.getConnection(Settings.DB_URL, Settings.DB_USER, Settings.DB_PASS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (con == null) {
			System.out.println("Cannot connect to cast.server.db: " + Settings.DB_URL);
			System.out.println("Exiting.");
			System.exit(0);
		}
	}

	public synchronized boolean authenticateUser(Client user, String username, String password) {
		try {
			if (psSelectUser == null) {
				psSelectUser = con.prepareStatement("select id, username, nickname, life, energy " +
					" from users " +
					" where username = ? " +
					" and password = ? ");
			}

			psSelectUser.setString(1, username);
			psSelectUser.setString(2, password);
			ResultSet result = psSelectUser.executeQuery();

			if (result.next()) {
				user.setId(result.getInt("id"));
				user.setUsername(result.getString("username"));
				user.setNickname(result.getString("nickname"));
				user.setMaxLife(result.getFloat("life"));
				user.setMaxEnergy(result.getFloat("energy"));
			} else {
				return false;
			}
			
			if (psGetUserSpells == null) {
				psGetUserSpells = con.prepareStatement("select * " +
					" from user_spells " +
					" where user_id = ? ");
			}
			
			psGetUserSpells.setInt(1, user.getId());
			result = psGetUserSpells.executeQuery();

			HashMap<Integer, Spell> spells = new HashMap<Integer, Spell>();
			while (result.next()) {
				Spell spell = GameState.getSpells().get(result.getInt("spell_id"));
				spells.put(spell.getId(), spell);
			}
			user.setSpells(spells);
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public synchronized HashMap<Integer, Spell> loadSpells() {
		HashMap<Integer, Spell> spells = new HashMap<Integer, Spell>(20);
		try {
			PreparedStatement psLoadSpells = con.prepareStatement(
				"select spells.id, name, self_life, self_energy, opponent_life, opponent_energy, target, data" +
				" from spells, gestures" +
				" where spells.gesture_id = gestures.id"
			);

			ResultSet result = psLoadSpells.executeQuery();

			while (result.next()) {
				spells.put(result.getInt("id"), new Spell(result.getInt("id"), result.getString("name"), result.getFloat("self_life"), result.getFloat("self_energy"), result.getFloat("opponent_life"), result.getFloat("opponent_energy"), result.getString("target"), result.getString("data")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return spells;
	}
}
