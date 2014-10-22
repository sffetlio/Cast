package cast.client.gdx.screens;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cast.client.gdx.CameraHelper;
import cast.client.gdx.Cast;
import cast.client.gdx.Enemy;
import cast.client.gdx.Settings;
import cast.client.gdx.World;
import cast.client.gdx.android.ConfirmInterface;
import cast.client.gdx.connection.ClientConnectionThread;
import cast.common.TouchPoint;
import cast.common.packets.CoordinatesUpdatePacket;
import cast.common.packets.Packet;
import cast.common.packets.PacketFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Class that represents the main game screen. Handles touch input, graphics and
 * connection.
 */
public class GameScreen implements Screen, InputProcessor {

	private final Cast game;
	private final ClientConnectionThread connection;
	private World world;

	private OrthographicCamera camera;
	SpriteBatch spriteBatch = new SpriteBatch();
	private ShapeRenderer shapeRenderer;

	private ParticleEffect particleEffect;
	private ParticleEffect particleEffect2;
	private Texture lifeTexture;
	private Texture manaTexture;
	private Texture backgroundTexture;
	private Texture lavaTexture;
	private BitmapFont font;

	public GameScreen(Cast game, ClientConnectionThread connection) {
		this.game = game;
		this.connection = connection;
		this.world = game.getWorld();

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("particles/fire.p"), Gdx.files.internal("particles"));
		particleEffect.allowCompletion();

		particleEffect2 = new ParticleEffect();
		particleEffect2.load(Gdx.files.internal("particles/fire_blue.p"), Gdx.files.internal("particles"));
		particleEffect2.allowCompletion();

		switch (world.background) {
			case 1:
				backgroundTexture = new Texture(Gdx.files.internal("images/background.png"));
				break;
			case 2:
				backgroundTexture = new Texture(Gdx.files.internal("images/background-1.png"));
				break;
			case 3:
				backgroundTexture = new Texture(Gdx.files.internal("images/background-2.png"));
				break;
			case 4:
				backgroundTexture = new Texture(Gdx.files.internal("images/background-3.png"));
				break;
			case 10:
				backgroundTexture = new Texture(Gdx.files.internal("images/training.png"));
				break;
			default:
				break;
		}

		lifeTexture = new Texture(Gdx.files.internal("images/life.png"));
		manaTexture = new Texture(Gdx.files.internal("images/mana.png"));
		lavaTexture = new Texture(Gdx.files.internal("images/lava.png"));
		camera = CameraHelper.getCamera(Settings.VIRTUAL_WIDTH, Settings.VIRTUAL_HEIGHT);
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		font.setColor(0.0f, 0.0f, 0.5f, 1.0f);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		CameraHelper.renderCameraShake(delta);
		renderBackground(delta);
		renderPlayer(delta);
		renderEnemies(delta);
		renderParticles(delta);
		renderUI(delta);
		renderLetterBox();
	}

	/**
	 * Render black "boxes" left/right or top/bottom. Used to hide background
	 * and effects.
	 */
	private void renderLetterBox() {
		float letterBox = CameraHelper.getLetterBoxX();
		if (letterBox > 0) {
			shapeRenderer.begin(ShapeType.FilledRectangle);
			shapeRenderer.setColor(0f, 0f, 0f, 1f);
			shapeRenderer.filledRect(-letterBox, 0, letterBox, Settings.VIRTUAL_HEIGHT);
			shapeRenderer.filledRect(Settings.VIRTUAL_WIDTH, 0, letterBox, Settings.VIRTUAL_HEIGHT);
			shapeRenderer.end();
		}
		letterBox = CameraHelper.getLetterBoxY();
		if (letterBox > 0) {
			shapeRenderer.begin(ShapeType.FilledRectangle);
			shapeRenderer.setColor(0f, 0f, 0f, 1f);
			shapeRenderer.filledRect(0, -letterBox, Settings.VIRTUAL_WIDTH, letterBox);
			shapeRenderer.filledRect(0, Settings.VIRTUAL_HEIGHT, Settings.VIRTUAL_WIDTH, letterBox);
			shapeRenderer.end();
		}
	}

	private void renderBackground(float delta) {
		spriteBatch.begin();
		spriteBatch.disableBlending();
		// spriteBatch.draw(backgroundTexture, -20, -20,
		// Settings.VIRTUAL_WIDTH+40, Settings.VIRTUAL_HEIGHT+40);
		spriteBatch.draw(backgroundTexture, 0, 0, Settings.VIRTUAL_WIDTH, Settings.VIRTUAL_HEIGHT);
		spriteBatch.end();
	}

	private void renderParticles(float delta) {
		spriteBatch.begin();
		spriteBatch.enableBlending();
		if (!particleEffect.isComplete()) {
			particleEffect.draw(spriteBatch, delta);
		}
		if (!particleEffect2.isComplete()) {
			particleEffect2.draw(spriteBatch, delta);
		}
		spriteBatch.end();
	}

	private void renderPlayer(float delta) {
		TouchPoint point = world.player.getLastPoint();
		if (point != null) {
			particleEffect.setPosition(point.getX(), point.getY());
			particleEffect.start();
		} else {
			particleEffect.allowCompletion();
		}

		TouchPoint prevPoint = null;

		// render dots that connect the thick lines
		shapeRenderer.begin(ShapeType.FilledCircle);
		for (ListIterator iterator = world.player.getPoints().listIterator(world.player.getPoints().size()); iterator.hasPrevious();) {
			TouchPoint tPoint = (TouchPoint) iterator.previous();
			shapeRenderer.setColor(0.19f, 0f, 0f, 1f);
			shapeRenderer.filledCircle(tPoint.getX(), tPoint.getY(), (10 * CameraHelper.getScale()) / 2);
		}
		shapeRenderer.end();

		// render the thick lines
		spriteBatch.begin();
		for (ListIterator iterator = world.player.getPoints().listIterator(world.player.getPoints().size()); iterator.hasPrevious();) {
			TouchPoint tPoint = (TouchPoint) iterator.previous();
			if (prevPoint != null) {
				drawThickLine(tPoint.getX(), tPoint.getY(), prevPoint.getX(), prevPoint.getY(), 10 * CameraHelper.getScale());
			}
			prevPoint = tPoint;
		}
		spriteBatch.end();
	}

	public void drawThickLine(float startScreenX, float startScreenY, float endScreenX, float endScreenY, float width) {
		Vector2 start = new Vector2(startScreenX, startScreenY);
		Vector2 end = new Vector2(endScreenX, endScreenY);

		float dx = startScreenX - endScreenX;
		float dy = startScreenY - endScreenY;

		Vector2 rightSide = new Vector2(dy, -dx);
		if (rightSide.len() > 0) {
			rightSide.nor();
			rightSide.set(rightSide.x * (width / 2), rightSide.y * (width / 2));
		}
		Vector2 leftSide = new Vector2(-dy, dx);
		if (leftSide.len() > 0) {
			leftSide.nor();
			leftSide.set(leftSide.x * (width / 2), leftSide.y * (width / 2));
		}

		Vector2 one = new Vector2();
		one.add(leftSide);
		one.add(start);

		Vector2 two = new Vector2();
		two.add(rightSide);
		two.add(start);

		Vector2 three = new Vector2();
		three.add(rightSide);
		three.add(end);

		Vector2 four = new Vector2();
		four.add(leftSide);
		four.add(end);

		float[] vertex = new float[] { one.x, one.y, new Color(0.5f, 0f, 0f, 1f).toFloatBits(), 0, 0, two.x, two.y, new Color(0.5f, 0f, 0f, 1f).toFloatBits(), 1, 0, three.x, three.y, new Color(0.5f, 0f, 0f, 1f).toFloatBits(), 1, 1, four.x, four.y, new Color(0.5f, 0f, 0f, 1f).toFloatBits(), 0, 1 };
		spriteBatch.draw(lavaTexture, vertex, 0, vertex.length);
	}

	// TODO rewrite point display logic for enemies
	private void renderEnemies(float delta) {
		long currentTimeMillis = System.currentTimeMillis();
		for (Enemy enemy : world.enemies.values()) {
			List<TouchPoint> points = enemy.getPoints();
			synchronized (points) {
				if (points.size() > 0) {
					particleEffect2.start();
					for (Iterator iterator = points.iterator(); iterator.hasNext();) {
						TouchPoint tPoint = (TouchPoint) iterator.next();
						// if(tPoint.isProcessed() == true)
						// continue;
						if (tPoint.getTime() >= currentTimeMillis - enemy.touchStartTime - 150) {
							if (tPoint.getX() == 0 && tPoint.getY() == 0) {
								enemy.touchEnd();
								enemy.clearPoints();
								particleEffect2.allowCompletion();
								break;
							}
							particleEffect2.setPosition(tPoint.getX(), tPoint.getY());
							break;
							// tPoint.setProcessed(true);
						}else{
							if (tPoint.getX() == 0 && tPoint.getY() == 0) {
								enemy.touchEnd();
								enemy.clearPoints();
								particleEffect2.allowCompletion();
								break;
							}
						}
					}
				}
			}
		}
	}

	private void renderUI(float delta) {
		spriteBatch.begin();
		spriteBatch.enableBlending();
		spriteBatch.draw(lifeTexture, 0, Settings.VIRTUAL_HEIGHT - 100, 50, world.player.getLife());
		spriteBatch.draw(manaTexture, Settings.VIRTUAL_WIDTH - 50, Settings.VIRTUAL_HEIGHT - 100, 50, world.player.getEnergy());

		if (world.enemies.size() > 0) {
			Enemy enemy = (Enemy) world.enemies.values().toArray()[0];
			spriteBatch.draw(lifeTexture, Settings.VIRTUAL_WIDTH / 2 - 100, Settings.VIRTUAL_HEIGHT - 20, enemy.getLife() * 2, 50);
		}

		int lifeRound = Math.round(world.player.getLife());
		int lifeOffset;
		if (lifeRound == 100) {
			lifeOffset = 10;
		} else if (lifeRound >= 0 && lifeRound <= 9) {
			lifeOffset = 20;
		} else {
			lifeOffset = 15;
		}

		int energyRound = Math.round(world.player.getEnergy());
		int energyOffset;
		if (energyRound == 100) {
			energyOffset = 35;
		} else if (energyRound >= 0 && energyRound <= 9) {
			energyOffset = 25;
		} else {
			energyOffset = 30;
		}

		font.draw(spriteBatch, "" + lifeRound, lifeOffset, Settings.VIRTUAL_HEIGHT - 45);
		font.draw(spriteBatch, "" + energyRound, Settings.VIRTUAL_WIDTH - energyOffset, Settings.VIRTUAL_HEIGHT - 45);
		spriteBatch.end();
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (!world.player.canCast()) {
			return false;
		}
		Vector3 touchPoint = new Vector3(x, y, 0);
		CameraHelper.unproject(touchPoint);
		world.player.startTouch();
		TouchPoint point = world.player.addPoint(touchPoint.x, touchPoint.y);
		if (connection.isConnected()) {
			Packet packet = PacketFactory.createNewPacket(Packet.TOUCH_START_PACKET);
			((CoordinatesUpdatePacket) packet).setUserId(world.player.getId());
			((CoordinatesUpdatePacket) packet).setPoint(point);
			connection.write(packet);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		if (!world.player.canCast()) {
			return false;
		}
		// limit points sent
		if (world.player.getPoints().size() > Settings.POINTS_TO_SEND) {
			return false;
		}
		Vector3 touchPoint = new Vector3(x, y, 0);
		CameraHelper.unproject(touchPoint);
		TouchPoint point = new TouchPoint(touchPoint.x, touchPoint.y);
		if (point.getDistane(world.player.getLastPoint()) > 10) {
			point = world.player.addPoint(touchPoint.x, touchPoint.y);
			if (connection.isConnected()) {
				Packet packet = PacketFactory.createNewPacket(Packet.TOUCH_MOVE_PACKET);
				((CoordinatesUpdatePacket) packet).setUserId(world.player.getId());
				((CoordinatesUpdatePacket) packet).setPoint(point);
				connection.write(packet);
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		world.player.touchEnd();
		TouchPoint point = world.player.addPoint(0, 0);
		if (connection.isConnected()) {
			Packet packet = PacketFactory.createNewPacket(Packet.TOUCH_END_PACKET);
			((CoordinatesUpdatePacket) packet).setUserId(world.player.getId());
			((CoordinatesUpdatePacket) packet).setPoint(point);
			connection.write(packet);
		}
		world.player.clearPoints();
		return false;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void hide() {
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
			game.getNativeHandler().showConfirm(new ConfirmInterface() {
				@Override
				public void yes() {
					Packet exitBattlePacket = PacketFactory.createNewPacket(Packet.EXIT_BATTLE_PACKET);
					connection.write(exitBattlePacket);
					game.switchScreen(Cast.MENU_SCREEN);
				}

				@Override
				public void no() {
				}
			}, "Return?", "Are you sure you want to return to the menu?");
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public World getWorld() {
		return world;
	}
}
