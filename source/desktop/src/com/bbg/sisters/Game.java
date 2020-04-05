package com.bbg.sisters;

import java.util.LinkedList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.bbg.sisters.iface.Scene;
import com.bbg.sisters.scenes.EditScene;
import com.bbg.sisters.scenes.GameScene;
import com.bbg.sisters.scenes.LoadScene;
import com.bbg.sisters.scenes.MainMenu;
import com.esotericsoftware.kryo.Kryo;

public class Game extends ApplicationAdapter {

	public static final float physicsScale = 100f;
	public static final float velocityThresholdFactor = 0.2f;

	static public final short CAT_SOLID = 0x0001;
	static public final short CAT_PLAYER = 0x0002;
	static public final short CAT_MONSTER = 0x0004;
	static public final short CAT_ITEM = 0x0008;
	// static public final short CAT_PBULLET = 0x0010;
	// static public final short CAT_MBULLET = 0x0100;

	static public final short MASK_SOLID = CAT_SOLID | CAT_PLAYER | CAT_MONSTER | CAT_ITEM;
	static public final short MASK_PLAYER = CAT_SOLID | CAT_MONSTER;
	static public final short MASK_MONSTER = CAT_SOLID | CAT_PLAYER;
	static public final short MASK_ITEM = CAT_SOLID;
	static public final short MASK_PBULLET = CAT_SOLID | CAT_MONSTER;
	static public final short MASK_MBULLET = CAT_SOLID | CAT_PLAYER;

	public Input input;

	public OrthographicCamera cam;
	public ShapeRenderer shapeRenderer;
	public SpriteBatch batcher;
	public float viewWidth, viewHeight;
	public float width, height;
	public int originX = 0;
	public int originY = 0;

	public boolean running = true;
	public long tick = 0;

	public Scene mainMenu;
	public Scene scene;
	public LoadScene loadScene;
	public GameScene gameScene;
	public EditScene editScene;

	public Kryo kryo;
	private World world;
	Listener listener;
	public Player player;

	public int curMap = 0;

	public Map[] maps;

	public final float rest = 0.0f;
	public final float fric = 0.1f;

	int[][] vedge = new int[100][100];
	int[][] hedge = new int[100][100];

	boolean notSetup = true;

	public LinkedList<Block> blocks = new LinkedList<Block>();
	public LinkedList<Debris> debris = new LinkedList<Debris>();
	public LinkedList<Item> items = new LinkedList<Item>();

	@Override
	public void create() {

		// get the actual screen dimensions
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		viewWidth = screenWidth;
		viewHeight = screenHeight;
		System.out.println(screenWidth + "," + screenHeight);
		float r = (float) screenWidth / (float) screenHeight; // calculate the screen ratio
		if (r == 1.5) { // 3:2
			originX = 0;
			originY = 0;
			viewWidth = 1080;
			viewHeight = 720;
		} else if (r > 1.5) { // 16:10, 17:10, 16:9
			viewWidth = 720 * r;
			viewHeight = 720;
			originX = (int) ((viewWidth - 1080) / 2.0f);
			originY = 0;
		} else if (r < 1.5) { // 4:3
			viewWidth = 1080;
			viewHeight = 1080 / r;
			originX = 0;
			originY = (int) ((viewHeight - 720) / 2.0f);
		}
		cam = new OrthographicCamera();
		cam.setToOrtho(true, viewWidth, viewHeight); // now we are good on any resolution
		batcher = new SpriteBatch();
		batcher.setProjectionMatrix(cam.combined);
		width = viewWidth;
		height = viewHeight;
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cam.combined);
		input = new Input(this);
		Gdx.input.setInputProcessor(input);
		// any game initialization code here

		mainMenu = new MainMenu(this);

		loadScene = new LoadScene(this);
		editScene = new EditScene(this);

		scene = loadScene;
		batcher.enableBlending();
		setupKryo();

		maps = new Map[50];
		for (int i = 0; i < 50; i++) {
			maps[i] = new Map();
			editScene.load(i);
		}

		player = new Player(this);

		gameScene = new GameScene(this, player, input);

	}

	public Map map() {
		return maps[curMap];
	}

	public boolean validCoord(int x, int y) {
		return (x >= 0 && y >= 0 && x <= 100 && y <= 100);
	}

	public void destroyBody(Body body) {
		if (world != null && body != null) {
			world.destroyBody(body);
		}
	}

	public void changeMap(int m) {
		changeMap(m, 96, 96);
	}

	public void changeMap(int m, float x, float y) {
		// do any partmap stuff here

		if (curMap != m || notSetup) {
			notSetup = false;
			curMap = m;
			items.clear();
			debris.clear();
			resetWorld();
			blocks.clear();
			addBlocks();
			addWalls();
			player.x = x;
			player.y = y;
			player.footCount = 0;
			player.createBody();
		} else {
			player.x = x;
			player.y = y;
			// player.footCount = 0;
			player.body.setTransform(x / physicsScale, y / physicsScale, 0);
		}

	}

	void addBlocks() {
		Body b;
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				if (map().att[x][y][0] == 3) {
					blocks.add(new Block(this, x, y, map().att[x][y][1], map().att[x][y][2], map().att[x][y][3],
							map().att[x][y][4], false, 0));
				} else if (map().att[x][y][0] == 4) {
					blocks.add(new Block(this, x, y, 0, 0, 0, 0, true, map().att[x][y][1]));
				}
				if (map().set[x][y][2] == 6) {
					int tx = x * 32;
					int ty = y * 32;
					b = addEdge(tx, ty, tx + 32, ty); // bottom
					b.getFixtureList().first().setUserData(5);
					b = addEdge(tx, ty, tx, ty + 12); // left
					b.getFixtureList().first().setUserData(5);
					b = addEdge(tx + 32, ty + 12, tx + 32, ty + 12); // right
					b.getFixtureList().first().setUserData(5);
					b = addEdge(tx, ty + 12, tx + 32, ty + 12); // top
					b.getFixtureList().first().setUserData(5);
				}
			}
		}
	}

	private void setupKryo() {
		kryo = new Kryo();
		kryo.register(Map.class);
	}

	public void changeScene(Scene scene) {
		this.scene = scene;
		scene.clear(tick);
		scene.start(tick);
		Gdx.input.setOnscreenKeyboardVisible(false);

	}

	public void resetWorld() {
		if (world != null) {
			world.dispose();
		}
		Array<Body> bodies = new Array<Body>();
		for (Body b : bodies) {
			world.destroyBody(b);
		}
		world = new World(new Vector2(0, -2500f / physicsScale), true);
		listener = new Listener(this);
		world.setContactListener(listener);
		world.setContinuousPhysics(true);
		World.setVelocityThreshold(World.getVelocityThreshold() * velocityThresholdFactor);
		gameScene.world = world;
	}

	public void newGame() {
		changeScene(gameScene);
		curMap = -1;
		changeMap(0);
	}

	public boolean isFullWall(int s, int i, int x, int y) {
		if (map().att[x][y][0] == 4) {
			return false;
		}
		if (s < 3) {
			switch (i - 1) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 8:
			case 9:
			case 10:
			case 11:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:

			case 56:
			case 57:
			case 58:
			case 59:
			case 60:
			case 64:
			case 65:
			case 72:
			case 73:
			case 74:
			case 75:
			case 76:
			case 77:
			case 78:
				return true;
			case 5:
			case 61:
				addEdge(x * 32, y * 32 + 16, x * 32 + 31, y * 32 + 16);
				addEdge(x * 32, y * 32 + 32, x * 32 + 31, y * 32 + 32);
				addEdge(x * 32, y * 32 + 16, x * 32, y * 32 + 32);
				addEdge(x * 32 + 31, y * 32 + 16, x * 32 + 31, y * 32 + 32);
				return false;
			}
		} else if (s == 3) {
			if (i >= 89 && i <= 126) {
				switch (i) {
				case 91:
				case 101:
				case 107:
				case 92:
				case 102:
				case 108:
				case 89:
				case 93:
				case 105:
				case 90:
				case 94:
				case 106:
					return false;
				}
				return true;
			} else {
				switch (i) {
				case 73: // little rock
					addEdge(x * 32 + 2, y * 32 + 14, x * 32 + 31 - 2, y * 32 + 14);
					addEdge(x * 32 + 2, y * 32, x * 32 + 31 - 2, y * 32);
					addEdge(x * 32 + 2, y * 32 + 14, x * 32 + 2, y * 32);
					addEdge(x * 32 + 31 - 2, y * 32 + 14, x * 32 + 31 - 2, y * 32);
					return false;
				case 74:
					addEdge(x * 32 + 2, y * 32 + 24, x * 32 + 31 - 2, y * 32 + 24);
					addEdge(x * 32 + 2, y * 32, x * 32 + 31 - 2, y * 32);
					addEdge(x * 32 + 2, y * 32 + 24, x * 32 + 2, y * 32);
					addEdge(x * 32 + 31 - 2, y * 32 + 24, x * 32 + 31 - 2, y * 32);
					return false;
				}
			}
		} else if (s == 4) {
			if (i >= 33) {
				return true;
			}
		}
		if (map().att[x][y][0] == 3) { // block
			if (map().att[x][y][1] == 6 && map().att[x][y][3] == 0) {// smashable brick

				return false;
			}
			return true;
		}
		return false;
	}

	public void step() {
		if (player != null) {
			for (Item i : items) {
				i.pre(tick);
			}
			player.pre(tick);
			float timeStep = 1f / 60.0f; // the length of time passed to simulate (seconds)
			int velocityIterations = 20; // how strongly to correct velocity
			int positionIterations = 20; // how strongly to correct position
			world.step(timeStep, velocityIterations, positionIterations);
			player.post(tick);
			LinkedList<Item> drops = new LinkedList<Item>();
			for (Item i : items) {
				i.post(tick);
				if (!i.active)
					drops.add(i);
			}
			for (Item i : drops) {
				items.remove(i);
			}
		}
	}

	public boolean isWater(int x, int y) {
		int t = map().tile[x][y][3];
		int s = map().set[x][y][3];
		if (s == 5) {
			return true;
		}
		return false;
	}

	public void checkWall(int x, int y, int i) {
		Fixture f;
		EdgeShape e;
		Body body;
		switch (i - 1) {
		case 24:
		case 80:

			body = createBody(BodyType.StaticBody, x * 32f, y * 32f);

			e = new EdgeShape();
			e.set(0f, 32f / physicsScale, 17f / physicsScale, 32f / physicsScale);
			e.setHasVertex0(true);
			e.setVertex0(-32F, 32f / physicsScale);
			e.setHasVertex3(true);
			e.setVertex3(107f / physicsScale, 0f);
			f = addFixture(body, false, 1f, fric, rest, e, Game.CAT_SOLID, Game.MASK_SOLID);
			e = new EdgeShape();
			e.set(17f / physicsScale, 29f / physicsScale, 107f / physicsScale, -2f / physicsScale);
			e.setHasVertex0(true);
			e.setVertex0(0f, 32f / physicsScale);
			e.setHasVertex3(true);
			e.setVertex3(128f / physicsScale, 0f);
			f = addFixture(body, false, 1f, fric, rest, e, Game.CAT_SOLID, Game.MASK_SOLID);
			e = new EdgeShape();
			e.set(107f / physicsScale, 0f, 128f / physicsScale, 0f);
			e.setHasVertex0(true);
			e.setVertex0(17f / physicsScale, 29f / physicsScale);
			e.setHasVertex3(true);
			e.setVertex3(160f / physicsScale, 0f);
			f = addFixture(body, false, 1f, fric, rest, e, Game.CAT_SOLID, Game.MASK_SOLID);
			break;
		case 40:
		case 96:
			body = createBody(BodyType.StaticBody, x * 32f, y * 32f);
			e = new EdgeShape();
			e.set(0f, 0f, 21f / physicsScale, 1f / physicsScale);
			e.setHasVertex0(true);
			e.setVertex0(-32F, 0f);
			e.setHasVertex3(true);
			e.setVertex3(112f / physicsScale, 32f / physicsScale);
			f = addFixture(body, false, 1f, fric, rest, e, Game.CAT_SOLID, Game.MASK_SOLID);
			e = new EdgeShape();
			e.set(21f / physicsScale, -2f / physicsScale, 112f / physicsScale, 29f / physicsScale);
			e.setHasVertex0(true);
			e.setVertex0(0F, 0f);
			e.setHasVertex3(true);
			e.setVertex3(128f / physicsScale, 32f / physicsScale);
			f = addFixture(body, false, 1f, fric, rest, e, Game.CAT_SOLID, Game.MASK_SOLID);
			e = new EdgeShape();
			e.set(112f / physicsScale, 32f / physicsScale, 128f / physicsScale, 32f / physicsScale);
			e.setHasVertex0(true);
			e.setVertex0(21f / physicsScale, 1f / physicsScale);
			e.setHasVertex3(true);
			e.setVertex3(160f / physicsScale, 32f / physicsScale);
			f = addFixture(body, false, 1f, fric, rest, e, Game.CAT_SOLID, Game.MASK_SOLID);
			break;
		}
	}

	public void addWalls() {
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				hedge[x][y] = 0;
				vedge[x][y] = 0;
				if (x == 0) {
					// vedge[0][y] = 1;
				}
			}
		}

		// addEdge(0, 0, 3200, 0);
		addEdge(0, 3200, 3200, 3200);
		addEdge(0, 0, 0, 3200);
		addEdge(3200, 0, 3200, 3200);

		// addWallBody(0, 1, 8000, 32);
		// addWallBody(-1, 0, 32, 8000);
		int ex = 0;
		int ey = 0;
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				if (isFullWall(map().set[x][y][0], map().tile[x][y][0], x, y)) {
					hedge[x][y]++;
					vedge[x][y]++;
					ex = x - 1;
					ey = y - 1;
					if (ex >= 0) {
						vedge[ex][y]++;
					}
					if (ey >= 0) {
						hedge[x][ey]++;
					}
				} else {
					// checkWall(map.tile[x][y][0]);
				}
			}
		}
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				if (vedge[x][y] >= 1) {// duplicate edges are ignored
					addVEdge(x, y);
				}
				if (hedge[x][y] >= 1) {// duplicate edges are ignored
					addHEdge(x, y);
				}

			}
		}
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				if (map().set[x][y][0] < 3) {
					checkWall(x, y, map().tile[x][y][0]);
				}
			}
		}

		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				int t = map().tile[x][y][0];
				int tx = x * 32;
				int ty = y * 32;
				switch (t) {
				case 91:
				case 101:
				case 107:
					// ignore first 3 pixels on the left
					addEdge(tx + 3, ty, tx + 31, ty); // bottom
					addEdge(tx + 3, ty, tx + 3, ty + 31); // left
					addEdge(tx + 31, ty, tx + 31, ty + 31); // right
					addEdge(tx + 3, ty + 31, tx + 31, ty + 31); // top
					break;
				case 92:
				case 102:
				case 108:
					// ignore last 3 pixels on the right
					addEdge(tx, ty, tx + 29, ty); // bottom
					addEdge(tx, ty, tx, ty + 31); // left
					addEdge(tx + 29, ty, tx + 29, ty + 31); // right
					addEdge(tx, ty + 31, tx + 29, ty + 31); // top
					break;
				case 89:
				case 93:
				case 105:
					// ignore top 3
					addEdge(tx, ty, tx + 31, ty); // bottom
					addEdge(tx, ty, tx, ty + 30); // left
					addEdge(tx + 31, ty, tx + 31, ty + 30); // right
					addEdge(tx, ty + 30, tx + 31, ty + 30); // top
					break;
				case 90:
				case 94:
				case 106:
					// ignore bottom 3
					addEdge(tx, ty + 3, tx + 31, ty + 3); // bottom
					addEdge(tx, ty + 3, tx, ty + 30); // left
					addEdge(tx + 31, ty + 3, tx + 31, ty + 30); // right
					addEdge(tx, ty + 30, tx + 31, ty + 30); // top
					break;
				}
			}
		}

	}

	public Body addEdge(float x1, float y1, float x2, float y2) {
		Body body = createBody(BodyType.StaticBody, x1, y1);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1f;
		fixtureDef.friction = fric;
		fixtureDef.restitution = rest;
		EdgeShape e;
		e = new EdgeShape();
		e.set(0, 0, (x2 - x1) / physicsScale, (y2 - y1) / physicsScale);
		Fixture f = addFixture(body, false, 1f, fric, rest, e, Game.CAT_SOLID, Game.MASK_SOLID);
		return body;
	}

	public Body addVEdge(float x, float y) {
		Body body = createBody(BodyType.StaticBody, x * 32f + 16f, y * 32f + 16f);
		EdgeShape e;
		float rightx = (float) (16) / physicsScale;
		float topy = (float) (-16) / physicsScale;
		float bottomy = (float) (16) / physicsScale;

		e = new EdgeShape();
		e.set(rightx, topy, rightx, bottomy);
		int ex = (int) x;
		int ey = (int) y - 1;
		if (ey >= 0) {
			if (vedge[ex][ey] >= 1) {
				e.setHasVertex0(true);
				e.setVertex0(rightx, topy - 32f);
			}
		}
		ey = (int) y + 1;
		if (ey < 100) {
			if (vedge[ex][ey] >= 1) {
				e.setHasVertex3(true);
				e.setVertex3(rightx, bottomy + 32f);
			}
		}
		Fixture f = addFixture(body, false, 1f, fric, rest, e, Game.CAT_SOLID, Game.MASK_SOLID);
		return body;
	}

	public Body addHEdge(float x, float y) {
		Body body = createBody(BodyType.StaticBody, x * 32f + 16f, y * 32f + 16f);
		EdgeShape e;
		float leftx = (float) (-16) / physicsScale;
		float rightx = (float) (16) / physicsScale;
		float bottomy = (float) (16) / physicsScale;
		e = new EdgeShape();
		e.set(leftx, bottomy, rightx, bottomy);
		int ex = (int) x - 1;
		int ey = (int) y;
		if (ex >= 0) {
			if (hedge[ex][ey] >= 1) {
				e.setHasVertex0(true);
				e.setVertex0(leftx - 32f, bottomy);
			}
		}
		ex = (int) x + 1;
		if (ex < 100) {
			if (hedge[ex][ey] >= 1) {
				e.setHasVertex3(true);
				e.setVertex3(rightx + 32f, bottomy);
			}
		}
		Fixture f = addFixture(body, false, 1f, fric, rest, e, Game.CAT_SOLID, Game.MASK_SOLID);
		return body;
	}

	@Override
	public void render() {
		tick = System.currentTimeMillis();
		if (AssetLoader.loaded) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batcher.begin();
			scene.update(tick);
			input.keyTyped.clear();
			update();
			scene.render();
			batcher.end();
			drawBars();

		} else {
			AssetLoader.load();
			changeScene(mainMenu);
		}
	}

	void update() {

	}

	public Body createBody(BodyType bodyType, float x, float y) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		bodyDef.position.set((x) / Game.physicsScale, (y) / Game.physicsScale);
		Body body = world.createBody(bodyDef);
		return body;
	}

	public Fixture addFixture(Body body, boolean sensor, float density, float fric, float rest, Shape p, short cat,
			short mask) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = sensor;
		fixtureDef.density = density;
		fixtureDef.friction = fric;
		fixtureDef.restitution = rest;
		fixtureDef.shape = p;
		fixtureDef.filter.categoryBits = cat;
		fixtureDef.filter.maskBits = mask;
		return body.createFixture(fixtureDef);
	}

	public void clip(int x, int y, int width, int height) {
		batcher.flush();
		Rectangle scissors = new Rectangle();
		Rectangle clipBounds = new Rectangle(x, y, width, height);
		ScissorStack.calculateScissors(cam, batcher.getTransformMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(scissors);
	}

	public void endClip() {
		batcher.flush();
		ScissorStack.popScissors();
	}

	private void drawBars() {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, 1);
		if (originY > 0) {
			shapeRenderer.rect(0, 0, viewWidth, originY); // Top bar
			shapeRenderer.rect(0, viewHeight - originY, viewWidth, originY); // Bottom bar
		} else if (originX > 0) {
			shapeRenderer.rect(0, 0, originX, viewHeight); // Left bar
			shapeRenderer.rect(viewWidth - originX, 0, originX, viewHeight); // Right bar
		}
		shapeRenderer.end();

	}

	public void moveCameraTo(int x, int y) {
		// cam.position.lerp(new Vector3(x,y,0.1f), 0.1f);
		cam.position.y = y;
		cam.position.x = x;
		cam.update();
		batcher.setProjectionMatrix(cam.combined);
		shapeRenderer.setProjectionMatrix(cam.combined);
	}

	public void drawRegion(TextureRegion region, int X, int Y, boolean centered, float rotation, float scale) {
		if (region == null) {
			return;
		}
		int width = region.getRegionWidth();
		int height = region.getRegionHeight();
		float eX = X + originX;
		float eY = Y + originY;

		if (centered) {
			eX -= (width / 2);
			eY -= (height / 2);
		}
		if (centered) {
			batcher.draw(region, eX, eY, width / 2, height / 2, width, height, scale, scale, rotation);
		} else {
			batcher.draw(region, eX, eY, 0, 0, width, height, scale, scale, rotation);
		}
	}

	public void draw(Texture texture, float x, float y, int srcX, int srcY, int w, int h, boolean fX, boolean fY) {
		float eX = Math.round(x + originX);
		float eY = Math.round(y + originY);
		// batcher.draw(texture, eX, eY, srcX, srcY, w, h);
		batcher.draw(texture, eX, eY, 0, 0, w, h, 1, 1, 0, srcX, srcY, w, h, fX, fY);
	}

	public void drawFont(int type, int x, int y, String s, boolean centered, float scale, Color col) {
		float curX = x;
		float padding = 0 * scale;
		float spacing = 1.0f * scale;
		float total = 0;
		float oX, oY;
		// get a quick count of width
		if (centered) {
			total = AssetLoader.getStringWidth(s, scale, padding, spacing);
			oX = Math.round(-total / 2);
			oY = Math.round((scale * -16.0f) / 2);
		} else {
			oX = 0;
			oY = 0;
		}

		batcher.setColor(col);
		for (char c : s.toCharArray()) {
			int ascii = (int) c;
			if (AssetLoader.fontWidth[ascii] > 0) {
				drawRegion(AssetLoader.font[type][ascii], Math.round(curX + padding + oX), Math.round((float) y + oY),
						false, 0, scale);
				curX += AssetLoader.fontWidth[ascii] * scale + padding * 2 + spacing;
			}
		}
		batcher.setColor(Color.WHITE);
	}

	public void drawFont(int type, int X, int Y, String s, boolean centered, float scale) {
		drawFont(type, X, Y, s, centered, scale, Color.WHITE);
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
		batcher.dispose();
	}
}
