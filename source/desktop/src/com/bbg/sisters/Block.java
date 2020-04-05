package com.bbg.sisters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Block {

	Game game;

	public int tx = 0;
	public int ty = 0;

	public float x = 0;
	public float y = 0;
	float sy = 0;

	public int type = 0;
	public int color = 0;
	public int itemType = 0;
	public int item = 0;

	public boolean has = true;
	public boolean hit = false;
	public boolean up = false;
	public Body body;

	public boolean active = true;

	public boolean lock = false;
	public int lockType = 0;

	long moveStamp = 0;

	public Block(Game game, int tx, int ty, int type, int color, int itemType, int item, boolean lock, int lockType) {
		this.tx = tx;
		this.ty = ty;
		this.type = type;
		this.color = color;
		this.itemType = itemType;
		this.item = item;
		sy = ty * 32 + 16;
		y = sy;
		x = tx * 32 + 16;
		this.game = game;
		this.lock = lock;
		this.lockType = lockType;
		createBody();
	}

	public void createBody() {

		PolygonShape p;
		Fixture f;

		body = game.createBody(BodyType.StaticBody, x, y);

		// open sensor
		if (has && !lock) {
			p = new PolygonShape();
			p.setAsBox((29f / game.physicsScale) / 2f, (2f / game.physicsScale) / 2f,
					new Vector2(0, -15f / game.physicsScale), 0);

			f = game.addFixture(body, true, 0.00001f, game.fric, 0, p, Game.CAT_SOLID, Game.MASK_SOLID);
			f.setUserData(this);
		}

		if (lock) {
			p = new PolygonShape();
			p.setAsBox((32f / Game.physicsScale) / 2f, (32f / Game.physicsScale) / 2f, new Vector2(0, 0), 0);
			f = game.addFixture(body, false, 1f, game.fric, 0, p, Game.CAT_SOLID, Game.MASK_SOLID);
			f.setUserData(this);
		}

		body.setFixedRotation(true);
		body.setLinearDamping(10f);
		body.setGravityScale(0);
	}

	public void update(long tick) {
		if (destroyBody) {
			destroyBody = false;
			game.destroyBody(body);
			body = null;
		}
		if (!hit || !active) {
			return;
		}
		if (up) {
			y += 2;
			if (y - sy > 12) {
				up = false;
				release();
			}
		} else {
			y -= 2;
			if (y - sy <= 1) {
				y = sy;
				hit = false;
			}
		}
	}

	public boolean destroyBody = false;

	public void hit() {
		if (hit || !has) {
			return;
		}
		if (lock) {
			System.out.println("check for key");
			if(game.player.key[lockType] > 0) {
				destroyBody = true;
				active = false;
				game.map().tile[tx][ty][0] = 0;
				game.map().att[tx][ty][0] = 0;
				game.player.key[lockType]--;
				
			}
		} else {
			hit = true;
			up = true;
		}
	}

	void release() {
		if (has) {
			has = false;
			type = 0;
			Item i = new Item(game, tx, ty + 1, itemType, item);
			game.items.add(i);
			i.body.setLinearVelocity(0, 3.6f);
			i.active = true;
		}
	}

	public void render() {
		if (!active) {
			return;
		}
		int s = color;
		int t = type * 64;
		float dx = x;
		float dy = game.height - (y);
		if (lock) {
			// draw teh lock
			game.draw(AssetLoader.locks, dx - 8, dy - 8, lockType * 16, 0, 16, 16, false, true);
		} else {
			game.draw(AssetLoader.tiles[4], dx - 16, dy - 16, (s % 8) * 32, 128 + t + 32, 32, 32, false, true);
		}
	}

}
