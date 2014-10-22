package cast.client.gdx;

import java.util.HashMap;
import java.util.Map;

public class World {

	private Cast game;
	public Player player;
	public Map<Integer, Enemy> enemies;
	public int background;

	public World(Cast game, int background) {
		this.game = game;
		this.background = background;
		this.player = game.player;
		enemies = new HashMap<Integer, Enemy>();
	}

}
