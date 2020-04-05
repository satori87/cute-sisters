package com.bbg.sisters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Bullet {

	Game game;

	public float x = 0;
	public float y = 0;
	float width = 2;
	float height = 2;

	public int type = 0;

	public int dir = 0;

	public int damage = 4;
	public int hitColor = 0;

	public boolean player = true;

	public boolean active = true;
	public boolean done = false;
	public long doneAt = 0;

	long rStamp = 0;
	int r = 0;

	public Body body;

	public Bullet(Game game, float x, float y, int type, int dir, boolean player) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.type = type;
		this.dir = dir;
		this.player = player;
		width = 1;
		height = 1;
		createBody();
	}

	public void createBody() {
		body = game.createBody(BodyType.DynamicBody, x + width / 2, y + height / 2);
		Fixture f;
		PolygonShape p = new PolygonShape();
		short cat = Game.CAT_MBULLET;
		short mask = Game.MASK_MBULLET;
		if (player) {
			cat = Game.CAT_PBULLET;
			mask = Game.MASK_PBULLET;
		}
		p.setAsBox((width / Game.physicsScale) / 2f, (height / Game.physicsScale) / 2f, new Vector2(0, 0), 0);
		f = game.addFixture(body, false, 0.9f, game.fric, game.rest, p, cat, mask);
		rStamp = game.tick + 100;
		f.setUserData(this);
		body.setUserData(this);
		body.setFixedRotation(true);
		body.setLinearDamping(0f);
		body.setGravityScale(0);
	}

	public void pre(long tick) {
		if (!active) {
			return;
		}
	}

	public void die() {
		active = false;
		doneAt = game.tick + 300;
		float d = -1f;
		if(dir == 0) d = 1f;
		x += (d*8)-10;
		r = 0;
	}

	public void post(long tick) {
		if (!active) {
			return;
		}
		x = body.getPosition().x * Game.physicsScale;
		y = body.getPosition().y * Game.physicsScale;
	}

	public void render() {
		if (done) {
			return;
		}
		float dx = x;
		float dy = game.height - y;
		if (active) {
			if (game.tick > rStamp) {
				rStamp = game.tick + 100;
				r++;
				if (r > 3) {
					r = 1;
				}
			}
			game.draw(AssetLoader.bullet, dx, dy, r * 12, type * 12, 12, 12, false, true);
		} else {
			if (game.tick > doneAt) {
				done = true;
				return;
			} else if (doneAt - game.tick < 150) {
				r = 1;
			}
			game.draw(AssetLoader.smallhit, dx, dy - 5, r * 20, hitColor * 20, 20, 20, false, true);
		}
	}

}
