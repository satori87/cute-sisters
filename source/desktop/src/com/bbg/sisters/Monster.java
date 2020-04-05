package com.bbg.sisters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Monster {

	Game game;

	public float x = 0;
	public float y = 0;

	public int type = 0;

	public int hp = 20;
	public int maxhp = 20;
	public int dir = 0;

	public int tx = 0;
	public int ty = 0;

	public boolean friendly = false;
	public boolean aggro = true;
	public boolean target = false; // targeting player

	public float sight = 250f;
	public float minDist = 40f;
	public float maxDist = 80f; // distances to maintain between player

	public Body body;
	public boolean attacking = false;
	int walkStep = 0;
	int walk5Step = 0;

	long walkStamp = 0;
	public int footCount = 1;
	public Fixture foot;
	public int spike = 0;
	public boolean destroyBody = false;
	public boolean dead = false;

	public boolean damaged = false;
	public long damStamp = 0;

	float width = 12;
	float height = 27;

	public boolean active = true;

	public Monster(Game game, int tx, int ty, int type) {
		this.game = game;
		x = tx * 32 + 16;
		y = ty * 32 + 16;
		this.tx = tx;
		this.ty = ty;
		this.type = type;
		createBody();
	}

	public void createBody() {
		body = game.createBody(BodyType.DynamicBody, x + 16f, y + 16f);
		Fixture f;
		width = 34;
		height = 24;
		PolygonShape p = new PolygonShape();
		p.setAsBox((width / Game.physicsScale) / 2f, (height / Game.physicsScale) / 2f, new Vector2(0, 0), 0);
		f = game.addFixture(body, true, 0.9f, game.fric, game.rest, p, Game.CAT_MONSTER, Game.MASK_MONSTER);
		f.setUserData(this);
		p = new PolygonShape();
		p.setAsBox(((width - 2f) / Game.physicsScale) / 2f, ((height - 7) / Game.physicsScale) / 2f,
				new Vector2(0, -10f / Game.physicsScale), 0);
		foot = game.addFixture(body, true, 0.00001f, game.fric, 0, p, Game.CAT_MONSTER, Game.MASK_MONSTER);
		foot.setUserData(6);
		body.setUserData(this);
		body.setFixedRotation(true);
		body.setLinearDamping(10f);
		if (type == 3) {
			body.setGravityScale(0f);
		}
	}

	public void die() {
		if (flying) {
			body.setGravityScale(1f);
		}
		Item i = new Item(game, (int) (x / 32), (int) (y / 32), 0, 14);
		game.items.add(i);
		i.body.setLinearVelocity(0, 3.6f);
		i.body.setGravityScale(0.6f);
		i.active = true;
	}

	public void hit(Bullet b) {
		if (!active) {
			return;
		}
		b.die();
		if (!friendly) {
			target = true;
		}
		hp -= b.damage;
		if (hp <= 0) {
			hp = 0;
			dead = true;
			destroyBody = true;
			// active = false;
		} else {
			damaged = true;
			damStamp = game.tick + 300;
		}
		System.out.println("monster hit with bullet");
	}

	public boolean onFloor() {
		return footCount > 0;
	}

	public boolean flying = true;

	public void pre(long tick) {

		if (!active || dead) {
			return;
		}

		Player p = game.player;
		if (spike > 0 && !flying) {
			// damage goes here
			int tx = (int) x / 32;
			int ty = (int) y / 32;
			if (game.map().set[tx][ty][0] == 6) {
				float vx = body.getLinearVelocity().x;
				float vy = body.getLinearVelocity().y;
				switch (game.map().tile[tx][ty][0] % 8) {
				case 1:
					body.applyForceToCenter(0, 2.5f, true);
					break;
				case 2:
					body.setLinearVelocity(vx, 0f);
					body.applyForceToCenter(0, -5.5f, true);
					break;
				case 3:
					body.setLinearVelocity(0, vy);
					body.applyForceToCenter(-2.5f, 0, true);
					break;
				case 4:
					body.setLinearVelocity(0, vy);
					body.applyForceToCenter(2.5f, 0, true);
					break;
				}
			}
		} else {
			float fX = 0f;
			float dist = (float) Math.hypot(y - p.y, x - p.x);
			if (dist < sight && (target || aggro)) {
				if (flying) {
					float f = 0;
					if (dist > maxDist) { // try to get closer
						f = 1f;
					} else if (dist < minDist) { // try to get away
						f = -1f;
					}
					if (f != 0) {
						f *= 0.6f;
						Vector v = Vector.byChange(p.x - x, p.y - y);
						v = new Vector(v.direction, f);
						body.applyForceToCenter(v.xChange, v.yChange, true);
					}
				} else {
					int s = 1;
					if (x < p.x) {
						s = 1;
					} else {
						s = -1;
					}
					fX *= s;
					if (y / 32 > 0) {
						if (fX > 0) { // moving to the right, lets check floor
							int tx = (int) ((x + width / 2) / 32f);
							int ty = ((int) y / 32) - 1;
							int ss = game.map().set[tx][ty][0];
							int t = game.map().tile[tx][ty][0];

							if (!game.isFullWall(ss, t, tx, ty)) {
								fX = 0;
							}
						} else if (fX < 0) {
							int tx = (int) ((x - width / 2) / 32f);
							int ty = ((int) y / 32) - 1;
							int ss = game.map().set[tx][ty][0];
							int t = game.map().tile[tx][ty][0];

							if (!game.isFullWall(ss, t, tx, ty)) {
								fX = 0;
							}
						}
					}
					body.applyForceToCenter(fX, 0, true);
				}
			} else {
				if (flying) {
					if (tick > dirStamp || (dx == 0 && dy == 0)) {
						dirStamp = tick + 2000 + (int) (Math.random() * 2000f);
						int rx = (int) (Math.random() * 10) - 5;
						int ry = (int) (Math.random() * 10) - 5;
						dx = tx + rx;
						dy = ty + ry;
						if (dx < 1)
							dx = 1;
						if (dy < 1)
							dy = 1;
						if (dx > 98)
							dx = 98;
						if (dy > 98)
							dy = 98;
					}
					Vector v = Vector.byChange(dx*32 - x, dy*32 - y);
					v = new Vector(v.direction, 0.6f);
					body.applyForceToCenter(v.xChange, v.yChange, true);
				} else {
					target = false;
					if (tick > dirStamp) {
						dirStamp = tick + 2000 + (int) (Math.random() * 2000f);
						dir = (int) (Math.random() * 2f);
					}
					fX = 0.4f;
					int d = -1;
					if (dir == 0) {
						d = 1;
					}
					fX *= d;
					int tx = (int) ((x + d * (width / 2)) / 32f);
					int ty = ((int) y / 32) - 1;
					int ss = game.map().set[tx][ty][0];
					int t = game.map().tile[tx][ty][0];

					if (!game.isFullWall(ss, t, tx, ty)) {
						fX = 0;
						dir = 1 - dir;
					}
					body.applyForceToCenter(fX, 0, true);
				}
			}
		}
	}

	int dx = 0;
	int dy = 0;
	long dirStamp = 0;

	public void post(long tick) {
		if (!active) {
			return;
		}
		if (dead && !flying) {
			return;
		}
		x = body.getPosition().x * Game.physicsScale;
		y = body.getPosition().y * Game.physicsScale;

	}

	long dStamp = 0;
	public boolean moving = false;

	public void render() {
		if (!active) {
			return;
		}
		if (body != null) {
			body.setAwake(true);
		}
		float dx = Math.round(x);
		float dy = Math.round(game.height - y - 32);

		if (!dead) {
			if (damaged) {
				if (game.tick > damStamp) {
					damaged = false;
				}
			}
			if (game.tick > dStamp) {
				dStamp = game.tick + 400;
				moving = false;
				if (body.getLinearVelocity().x > 0) {
					dir = 0;
					moving = true;
				} else if (body.getLinearVelocity().x < 0) {
					dir = 1;
					moving = true;
				}
			}
		}

		if (game.tick > walkStamp) {
			walkStep++;
			walk5Step++;
			twoStep = 1 - twoStep;
			if (walkStep > 3) {
				walkStep = 0;
			}
			if (walk5Step > 4) {
				walk5Step = 0;
			}
			walkStamp = game.tick + 300;
		}
		int w = 0;
		if (type == 2 && target) {
			walkStep = walk5Step;
			w = 4;
		}
		if (type == 3) {
			walkStep = twoStep;
			moving = true;
		}

		if (dead) {
			game.draw(AssetLoader.monsters[type][1], dx - 21f, dy + 15f, 0, 0, 42, 30, dir == 1, true);
		} else {
			if (damaged) {
				game.draw(AssetLoader.monsters[type][3], dx - 21f, dy + 15f, 0, 0, 42, 30, dir == 1, true);
			} else {
				if (moving) {
					game.draw(AssetLoader.monsters[type][w], dx - 21f, dy + 15f, walkStep * 42, 0, 42, 30, dir == 1,
							true);
				} else {
					game.draw(AssetLoader.monsters[type][2], dx - 21f, dy + 15f, 0, 0, 42, 30, dir == 1, true);
				}
			}
		}

	}

	int twoStep = 0;
}
