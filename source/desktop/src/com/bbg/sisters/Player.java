package com.bbg.sisters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player {
	public Game game;

	public float x = 0;
	public float y = 0;
	public float direction; // rads, d00d

	public boolean remove = false;

	// public boolean moving = false;
	public boolean crouch = false;
	public boolean dashing = false;
	public boolean braking = false;
	public boolean climbing = false;
	public boolean swimming = false;
	public boolean hasInputX = false;
	public boolean attacking = false;
	public int spike = 0;
	public long inputStampX = 0;
	public boolean hasInputY = false;
	public boolean jumping = false;
	public long jumpStamp = 0;
	public long inputStampY = 0;
	public int dir = 0; // 0 left 1 right
	int walkStep = 0;
	int crouchStep = 0;
	long walkStamp = 0;
	int twoStep = 0;
	long twoStamp = 0;
	public int climbStep = 0;
	public int climbSteps = 0;
	public Body body;
	public int footCount = 1;
	public Fixture foot;

	public int key[] = new int[6];

	public Player(Game game) {
		this.game = game;
		this.x = 64;
		this.y = 64;
		resetKeys();
	}

	public void resetKeys() {
		key = new int[6];
		for (int i = 0; i < 6; i++) {
			key[i] = 0;
		}
	}

	public static void fix(Vector2[] vectors, float radius, float physicsScale, float scale) {
		// for user-friendliness, the vectors are defined in terms of 0,0
		// origin, then and then adjusted by the radius to give us
		// the correct transformations
		for (Vector2 v : vectors) {
			v.x -= radius;
			v.y -= radius;
			v.x /= physicsScale;
			v.y /= physicsScale;
			v.x *= scale;
			v.y *= scale;
		}
	}

	public void createBody() {
		body = game.createBody(BodyType.DynamicBody, x + 16f, y + 16f);
		Fixture f;
		Shape c = new CircleShape();
		c.setRadius(12f / Game.physicsScale);
		PolygonShape p = new PolygonShape();
		p.setAsBox((12f / Game.physicsScale) / 2f, (27f / Game.physicsScale) / 2f, new Vector2(0, 0), 0);
		f = game.addFixture(body, false, 0.9f, game.fric, game.rest, p, Game.CAT_PLAYER, Game.MASK_PLAYER);
		f.setUserData(this);
		p = new PolygonShape();
		p.setAsBox((14f / Game.physicsScale) / 2f, (20f / Game.physicsScale) / 2f,
				new Vector2(0, -10f / Game.physicsScale), 0);
		foot = game.addFixture(body, true, 0.00001f, game.fric, 0, p, Game.CAT_PLAYER, Game.MASK_PLAYER);
		foot.setUserData(3);

		p = new PolygonShape();
		p.setAsBox((14f / Game.physicsScale) / 2f, (30f / Game.physicsScale) / 2f, new Vector2(0, 0), 0);
		f = game.addFixture(body, true, 0.000001f, game.fric, game.rest, p, Game.CAT_SOLID, Game.MASK_SOLID);
		f.setUserData(4);

		body.setUserData(this);
		body.setFixedRotation(true);
		body.setLinearDamping(10f);
	}

	public boolean onFloor() {
		return footCount > 0 || swimming || climbing;
	}

	public void destroyBody() {
		game.destroyBody(body);
		body = null;
	}

	public void pre(long tick) {
		if (spike > 0) {
			int tx = (int)x / 32;
			int ty =(int) y / 32;
			switch(game.map().tile[tx][ty][2]) {
			
			}
			body.applyForceToCenter(0, 2.5f, true);
		}
	}

	public void post(long tick) {
		x = body.getPosition().x * Game.physicsScale;
		y = body.getPosition().y * Game.physicsScale;

	}

	public void render() {
		float dx = x;
		float dy = game.height - y - 32;
		if (game.tick > walkStamp) {
			walkStep++;
			crouchStep++;
			if (walkStep > 3) {
				walkStep = 0;
			}
			if (crouchStep > 4) {
				crouchStep = 0;
			}
			walkStamp = game.tick + 200;
		}

		if (game.tick > twoStamp) {
			twoStamp = game.tick + 200;
			twoStep = 1 - twoStep;
		}
		// if (Math.abs(body.getLinearVelocity().y) > 0.1f) {
		boolean clip = false;

		if (game.gameScene.pipeDir == 3) {
			if (game.gameScene.piping) {
				game.clip(0, 0, game.gameScene.pipeTX * 32 + 2, (int) game.height);
				clip = true;
			} else if (game.gameScene.unpiping) {
				game.clip(0, 0, game.gameScene.pipeTX * 32 + 2 + 32, (int) game.height);
				clip = true;
			}
		} else if (game.gameScene.pipeDir == 2) {
			if (game.gameScene.piping) {
				game.clip(game.gameScene.pipeTX * 32 - 2 + 32, 0, 32, (int) game.height);
				clip = true;
			} else if (game.gameScene.unpiping) {
				game.clip(game.gameScene.pipeTX * 32 - 2, 0, 32, (int) game.height);
				clip = true;
			}
		} else if (game.gameScene.pipeDir == 1) {
			if (game.gameScene.piping) {
				game.clip(0, 0, (int) game.width, (int) (game.height - game.gameScene.pipeTY * 32 + 2 - 32));
				clip = true;
			} else if (game.gameScene.unpiping) {
				// System.out.println("Aa");
				game.clip(0, (int) (game.height - game.gameScene.pipeTY * 32 - 2 - 32), (int) game.width,
						(int) game.height);
				clip = true;
			}
		} else if (game.gameScene.pipeDir == 0) {
			if (game.gameScene.piping) {
				game.clip(0, (int) (game.height - game.gameScene.pipeTY * 32 - 2), (int) game.width, (int) game.height);
				clip = true;
			} else if (game.gameScene.unpiping) {
				// System.out.println("Aabb");
				game.clip(0, 0, (int) game.width, (int) (game.height - game.gameScene.pipeTY * 32 + 2));
				clip = true;
			}
		}
		int a = 0;
		if (attacking) {
			a = 1;
			walkStep = twoStep;
			climbStep = 0;
			
		}
		
		// are we in midair?
		if (climbing) {
			game.draw(AssetLoader.sprites[0][10][a], dx - 16, dy + 16, climbStep * 32, 0, 32, 32, dir == 1, true);
		} else {
			if (onFloor() && !swimming) {
				// we are on the ground. are we moving?
				if (body.getLinearVelocity().len() > 0.3f) {
					// we are moving, draw walk step
					if (crouch) {
						game.draw(AssetLoader.sprites[0][7][a], dx - 16, dy + 16, crouchStep * 32, 0, 32, 32, dir == 1,
								true);
					} else {
						if (dashing) {
							game.draw(AssetLoader.sprites[0][8][a], dx - 16, dy + 16, 0, 0, 32, 32, dir == 1, true);
						} else {
							if (braking) {
								game.draw(AssetLoader.sprites[0][9][a], dx - 16, dy + 16, 0, 0, 32, 32, dir == 1, true);
							} else {
								game.draw(AssetLoader.sprites[0][1][a], dx - 16, dy + 16, walkStep * 32, 0, 32, 32,
										dir == 1, true);
							}
						}
					}
				} else {
					if (crouch) {
						game.draw(AssetLoader.sprites[0][6][a], dx - 16, dy + 16, 0, 0, 32, 32, dir == 1, true);
					} else {
						// not moving, draw standing
						game.draw(AssetLoader.sprites[0][0][a], dx - 16, dy + 16, 0, 0, 32, 32, dir == 1, true);
					}
				}
			} else {
				if (swimming) {
					// draw swim swam here!
					if (body.getLinearVelocity().len() > 0.3f && (hasInputX || hasInputY)) {
						game.draw(AssetLoader.sprites[0][14][a], dx - 16, dy + 16, walkStep * 32, 0, 32, 32, dir == 1,
								true);
					} else {
						game.draw(AssetLoader.sprites[0][11][a], dx - 16, dy + 16, 0, 0, 32, 32, dir == 1, true);
					}

				} else {
					// we are in midair. we are moving up?
					if (body.getLinearVelocity().y > 0) {
						if (crouch) {
							game.draw(AssetLoader.sprites[0][6][a], dx - 16, dy + 16, 0, 0, 32, 32, dir == 1, true);
						} else {
							// yes, draw jumping
							game.draw(AssetLoader.sprites[0][2][a], dx - 16, dy + 16, 0, 0, 32, 32, dir == 1, true);
						}
					} else { // no, draw falling
						if (crouch) {
							game.draw(AssetLoader.sprites[0][6][a], dx - 16, dy + 16, 0, 0, 32, 32, dir == 1, true);
						} else {
							game.draw(AssetLoader.sprites[0][4][a], dx - 16, dy + 16, 0, 0, 32, 32, dir == 1, true);
						}
					}
				}
			}
		}
		if (clip) {
			game.endClip();

		}

		// footCount = 0;

	}

	public void give(int type, int item) {
		switch (type) {
		case 0: // item
			switch (item) {
			case 0: // cherry1
				break;
			case 1: // cherry 2
				break;
			case 2: // red apple
				break;
			case 3: // green apple
				break;
			case 4: // orange apple
				break;
			case 5: // grapes 1
				break;
			case 6: // grapes 2
				break;
			case 7: // present 1
				break;
			case 8: // present 2
				break;
			case 9: // present 3
				break;
			case 10: // present 4
				break;
			case 11: // blue gem
				break;
			case 12:// white gem
				break;
			case 13:// green gem
				break;
			case 14:// orange gem
				break;
			case 15:// purple gem
				break;
			case 16:// red gem
				break;
			}
			break;
		case 1: // key
			key[item]++;
			break;
		case 2: // coin
			if (item == 0) { // yellow

			} else { // white

			}
			break;
		}
	}
}
