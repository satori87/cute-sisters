package com.bbg.sisters.scenes;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.World;
import com.bbg.sisters.AssetLoader;
import com.bbg.sisters.Block;
import com.bbg.sisters.Debris;
import com.bbg.sisters.Game;
import com.bbg.sisters.Input;
import com.bbg.sisters.Item;
import com.bbg.sisters.Player;
import com.bbg.sisters.iface.Scene;

public class GameScene extends Scene {

	long moveStamp = 0;
	public World world;
	public Player player;

	long dashStamp = 0;
	long brakeStamp = 0;
	long dashCool = 0;

	public boolean piping = false;
	public boolean unpiping = false;
	public int pipeDir = 0;
	public float pipeX = 0;
	public float pipeY = 0;
	public long pipeStamp = 0;
	public int pipeTX = 0;
	public int pipeTY = 0;

	Input input;
	float camY = 320;

	public GameScene(Game game, Player player, Input input) {
		this.game = game;
		this.player = player;
		this.input = input;
	}

	public void start(long tick) {
		super.start(tick);
	}

	public void buttonPressed(int id) {

	}

	public void buttonReleased(int id) {

	}

	boolean isLadder() {
		int tx = (int) player.x / 32;
		int ty = (int) player.y / 32;
		int tile = game.map().tile[tx][ty][0];
		int set = game.map().set[tx][ty][0];
		if (set == 4) {
			switch (tile) {
			case 1:
			case 2:
			case 3:
			case 9:
			case 10:
			case 17:
			case 18:
			case 25:
			case 26:
				// ladder
				if (player.climbing) {
					player.body.applyForceToCenter(0, 0.3f, true);
				} else {
					player.climbing = true;
					player.body.setLinearVelocity(0, 0);
					player.body.setGravityScale(0);
				}
				return true;
			}
		}
		return false;
	}

	public boolean isDoor() {
		int tx = (int) player.x / 32;
		int ty = (int) player.y / 32;
		int tile = game.map().tile[tx][ty][0];
		int set = game.map().set[tx][ty][0];
		int[] att = game.map().att[tx][ty];
		if (att[0] == 2) { // door
			game.changeMap(att[3], att[1] * 32, att[2] * 32);

			return true;
		}
		return false;
	}

	public boolean isPipe(int dir, int x, int y) {
		if (!game.validCoord(x, y)) {
			return false;
		}
		int tile = game.map().tile[x][y][0];
		int set = game.map().set[x][y][0];
		int[] att = game.map().att[x][y];
		if (att[0] == 2) { // make sure its got an att!@
		} else {
			return false;
		}
		if (dir == 3) { // right
			switch (tile) {
			case 91:
			case 101:
			case 107:
				return true;
			}
		} else if (dir == 2) { // left
			switch (tile) {
			case 92:
			case 102:
			case 108:
				return true;
			}
		} else if (dir == 0) {
			switch (tile) {
			case 90:
			case 94:
			case 106:
				return true;
			}
		} else if (dir == 1) {
			switch (tile) {
			case 89:
			case 93:
			case 105:
				return true;
			}
		}
		return false;
	}

	void doPipes() {
		if (unpiping) {
			if (pipeDir == 3 || pipeDir == 2) {
				if (pipeDir == 3)
					player.x -= 1f;
				if (pipeDir == 2)
					player.x += 1f;
				if (Math.abs(player.x - pipeX) <= 1f && Math.abs(player.y - pipeY) <= 16f)
					unpiping = false;
				if (!unpiping)
					player.body.setTransform(player.x / game.physicsScale, player.y / game.physicsScale, 0);
			} else if (pipeDir == 1 || pipeDir == 0) {
				if (pipeDir == 0)
					player.y += 1f;
				if (pipeDir == 1)
					player.y -= 1f;
				if (Math.abs(player.x - pipeX) <= 16f && Math.abs(player.y - pipeY) <= 1f) {
					unpiping = false;
				}
				if (!unpiping) {
					player.body.setTransform(player.x / game.physicsScale, player.y / game.physicsScale, 0);
				}
			}
		} else if (piping) {
			if (pipeDir == 3 || pipeDir == 2) {
				if (pipeDir == 3)
					player.x += 1f;
				if (pipeDir == 2)
					player.x -= 1f;
				if (Math.abs(player.x - pipeX) <= 1f && Math.abs(player.y - pipeY) <= 16f)
					piping = false;
			} else if (pipeDir == 1 || pipeDir == 0) {
				if (pipeDir == 0)
					player.y += 1f;
				if (pipeDir == 1)
					player.y -= 1f;
				if (Math.abs(player.x - pipeX) <= 16f && Math.abs(player.y - pipeY) <= 1f)
					piping = false;
				if (!unpiping)
					player.body.setTransform(player.x / game.physicsScale, player.y / game.physicsScale, 0);
			}
			if (!piping) {
				int att[] = game.map().att[pipeTX][pipeTY];
				int px = att[1];
				int py = att[2];
				game.changeMap(att[3], px * 32, py * 32);
				int pt = game.map().tile[px][py][0];
				int pset = game.map().set[px][py][0];
				int d = -1;
				if (pset == 3) {
					switch (pt) {
					case 91:
					case 101:
					case 107:
						d = 3;
						break;
					case 92:
					case 102:
					case 108:
						d = 2;
						break;
					case 89:
					case 93:
					case 105:
						d = 0;
						break;
					case 90:
					case 94:
					case 106:
						d = 1;
						break;
					}
					if (d >= 0) {
						unpiping = true;
						pipeDir = d;
						if (d == 3) {
							player.dir = 1;
							player.x = px * 32 + 16;
							player.y = py * 32 + 16;
							pipeTX = px - 1;
							pipeTY = py;
							pipeX = (px - 1) * 32 + 16;
							pipeY = py * 32;
						} else if (d == 2) {
							player.dir = 0;
							player.x = px * 32 + 16;
							player.y = py * 32 + 16;
							pipeTX = px + 1;
							pipeTY = py;
							pipeX = (px + 1) * 32 + 16;
							pipeY = py * 32;
						} else if (d == 0) {
							player.x = px * 32 + 16;
							player.y = py * 32 + 16;
							pipeTX = px;
							pipeTY = py + 1;
							pipeX = px * 32 + 16;
							pipeY = (py + 1) * 32 + 16;
						} else if (d == 1) {
							player.x = px * 32 + 16;
							player.y = py * 32 + 16;
							pipeTX = px;
							pipeTY = py - 1;
							pipeX = px * 32 + 16;
							pipeY = (py - 1) * 32 + 16;
						}
					}
				}
			}
		}
	}

	void checkKeys(long tick) {
		int tx = (int) player.x / 32;
		int ty = (int) player.y / 32;
		int tile = game.map().tile[tx][ty][0];
		int set = game.map().set[tx][ty][0];
		float fx = 0;
		float fy = 0;
		if (input.keyDown[22]) { // right
			fx = 0.7f;
			if (!piping && isPipe(3, tx + 1, ty) && player.onFloor()) {
				if ((tx + 1) * 32 - player.x < 8) {
					pipeDir = 3;
					pipeX = (tx + 1) * 32 + 16;
					pipeY = (ty) * 32 + 16;
					pipeTX = tx + 1;
					pipeTY = ty;
					piping = true;
					return;
				}
			}
			// player.dir = 0;
		} else if (input.keyDown[21]) { // left
			fx = -0.7f;
			if (!piping) {
				if (isPipe(2, tx - 1, ty)) {
					if (player.onFloor()) {
						if (player.x - ((tx - 1) * 32) <= 40) {
							pipeDir = 2;
							pipeX = (tx - 1) * 32 - 16;
							pipeY = (ty) * 32 + 16;
							pipeTX = tx - 1;
							pipeTY = ty;
							piping = true;
							return;
						}
					}
				}
			}
		}
		if (player.climbing) {
			if (set == 4) {
				switch (tile) {
				case 1:
				case 2:
				case 3:
				case 9:
				case 10:
				case 17:
				case 18:
				case 25:
				case 26:
					// ladder
					break;
				default:
					player.climbing = false;
					player.body.setGravityScale(1f);
					break;
				}
			} else {
				player.climbing = false;
				player.body.setGravityScale(1f);
			}

		}
		fy = 0.0f;
		if (input.keyDown[19]) {
			if (!piping) {
				if (isPipe(0, tx, ty + 1)) {
					if ((ty + 1) * 32 - player.y <= 16) {
						pipeX = (tx) * 32 + 16;
						if (Math.abs((player.x) - pipeX) <= 8) {
							// player.x = pipeX;
							pipeDir = 0;
							pipeY = (ty + 1) * 32 + 16;
							pipeTX = tx;
							pipeTY = ty + 1;
							piping = true;
							return;
						}
					}
				}
			}
			if (player.climbing) {
				fy = 0.3f;
				player.climbSteps++;
			} else {
				isLadder();
				// isDoor();
			}
		}
		if (tick > player.jumpStamp) {
			player.jumping = false;
		}
		if(player.jumping) {
			fy = 2f + ((float)(player.jumpStamp-tick) / 150f);
		}
		for (Integer ii : input.keyPress) {
			if (ii == 62) {
				if (player.onFloor() && !player.jumping) {
					player.jumpStamp = tick + 200;
					if (player.swimming) {
						player.jumpStamp = tick + 200;
					}
					fy = 3f;
					player.jumping = true;
					player.climbing = false;
					player.body.setGravityScale(1f);
				}
			} else if (ii == 19) {
				isDoor();
			} else if (ii == 247) {
				game.changeScene(game.editScene);
			}
		}
		input.keyPress.clear();

		if (input.keyDown[20]) {
			if (!piping) {
				if (isPipe(1, tx, ty - 1)) {
					if (player.onFloor()) {
						if (player.y - ((ty - 1) * 32) <= 48) {
							pipeX = (tx) * 32 + 16;
							if (Math.abs((player.x) - pipeX) <= 8) {
								// player.x = pipeX;
								pipeDir = 1;
								pipeY = (ty - 1) * 32 + 16;
								pipeTX = tx;
								pipeTY = ty - 1;
								piping = true;
								return;
							}
						}
					}
				}
			}
			if (player.climbing) {
				fy = -0.3f;
				player.climbSteps++;
			} else {
				player.crouch = true;
				fx *= 0.6f;
				fy *= 0.6f;
			}
		} else {
			if (player.dashing && tick > dashStamp) {
				player.dashing = false;
				// player.braking = true;
				brakeStamp = tick + 400;
			}
			if (player.braking && tick > brakeStamp) {
				player.braking = false;
			}
			if (!player.dashing) {
				if (fx != 0 && player.onFloor() && input.keyDown[60]) {
					if (tick > dashStamp && tick > dashCool) {
						dashStamp = tick + 350;
						dashCool = tick + 1500;
						player.dashing = true;
					}
				}
			}
			if (player.dashing) {
				// if (player.onFloor()) {
				fx *= 4f;
				// }
			}
			player.crouch = false;
		}

		if (player.climbSteps > 7) {
			player.climbSteps = 0;
			player.climbStep = 1 - player.climbStep;
		}
		if (player.swimming) {
			player.body.setLinearDamping(5f);
			fx /= 3f;
			fy /= 3f;
		} else {
			player.body.setLinearDamping(10f);
		}
		if (fx != 0) {
			player.inputStampX = tick + 100;
			player.hasInputX = true;
		}
		if (fy != 0) {
			player.inputStampY = tick + 200;
			player.hasInputY = true;
		}
		if (tick > player.inputStampX) {
			player.hasInputX = false;
		}
		if (tick > player.inputStampY) {
			player.hasInputY = false;
		}
		player.body.applyForceToCenter(fx, fy, true);
		player.braking = false;
		if (fx > 0) {
			if (player.body.getLinearVelocity().x < -0.1f) {
				player.braking = true;
			}
		} else if (fx < 0) {
			if (player.body.getLinearVelocity().x > 0.1f) {
				player.braking = true;
			}
		}
		if (player.body.getLinearVelocity().x > 0.1f) {
			player.dir = 0;
		} else if (player.body.getLinearVelocity().x < -0.1f) {
			player.dir = 1;
		}
	}

	void updateCamera() {
		float cx = player.x;
		if (player.y - camY > 160) {
			camY = player.y - 160;
		}
		if (camY - player.y > 160) {
			camY = player.y + 160;
		}
		if (camY < game.height / 2) {
			camY = game.height / 2;
		}
		if (player.x < game.width / 2f) {
			cx = game.width / 2f;
		}
		if (player.x > 3200f - (game.width / 2f)) {
			cx = 3200f - (game.width / 2f);
		}
		game.moveCameraTo(Math.round(cx), Math.round(720 - camY));
	}

	public void update(long tick) {
		int tx = (int) player.x / 32;
		int ty = (int) player.y / 32;
		if (!game.validCoord(tx, ty))
			return;
		if (piping || unpiping) {
			doPipes();
		} else {
			LinkedList<Debris> drops = new LinkedList<Debris>();
			for (Debris d : game.debris) {
				d.update(tick);
				if (!d.active) {
					drops.add(d);
				}
			}
			for (Debris d : drops) {
				game.debris.remove(d);
			}
			drops.clear();
			if (tick > waterStamp) {
				waterStamp = tick + 180;
				waterStep++;
				waterStep2++;
				if (waterStep > 2) {
					waterStep = 0;
				}
				if (waterStep2 > 1) {
					waterStep2 = 0;
				}
			}
			if (game.isWater(tx, ty)) {
				player.swimming = true;
				if (!player.climbing) {
					player.body.setGravityScale(0.1f);
				}
			} else {
				player.swimming = false;
				if (!player.climbing) {
					player.body.setGravityScale(1);
				}
			}
			checkKeys(tick);
			for (Block b : game.blocks) {
				b.update(tick);
				// b.body.setTransform(b.x/game.physicsScale,b.y/game.physicsScale, 0);
			}
			game.step();
			for (Block b : game.blocks) {
				// b.x = b.body.getPosition().x * game.physicsScale;
				// b.y = b.body.getPosition().y * game.physicsScale;
			}
		}
		updateCamera();
		super.update(tick);
	}

	int waterStep = 0;
	int waterStep2 = 0;
	long waterStamp = 0;

	public void render() {
		Gdx.gl.glClearColor(124f / 255f, 235f / 255f, 255f / 255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				int a = 4;
				int t = game.map().tile[x][y][3];
				float dx = x * 32;
				float dy = game.height - (y * 32) - 32;
				if (t > 0) {
					t -= 1;
					if (t % 8 == 3) {
						t += waterStep;
					}
					if (t % 8 == 1) {
						t += waterStep2;
					}
					game.draw(AssetLoader.tiles[5], dx, dy, (t % 8) * 32, (t / 8) * 32, 32, 32, false, true);
				}
				for (a = 0; a < 2; a++) {
					t = game.map().tile[x][y][a];
					if (t > 0) {
						t -= 1;
						game.draw(AssetLoader.tiles[game.map().set[x][y][a]], dx, dy, (t % 8) * 32, (t / 8) * 32, 32,
								32, false, true);
					}
				}
			}
		}

		drawBlocks();

		for (Item i : game.items) {
			i.render();
		}
		plantStep++;
		if(plantStep > 24) {
			plantStep = 1;
		}
		game.player.render();
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				for (int a = 2; a < 3; a++) {
					int t = game.map().tile[x][y][a];
					float dx = x * 32;
					float dy = game.height - (y * 32) - 32;
					if (t > 0) {
						t -= 1;
						int s = game.map().set[x][y][a];
						if (s == 6 && t > 7) {
							System.out.println("bad2");
							game.draw(AssetLoader.plant[(t-8)/8], dx, dy, (plantStep/8) * 32, (t % 8) * 32, 32, 32, false, true);
						} else {
							System.out.println("bad");
							//game.draw(AssetLoader.tiles[s], dx, dy, (t % 8) * 32, (t / 8) * 32, 32, 32, false, true);
						}
					}
				}
			}
		}
		for (Debris d : game.debris) {
			d.render();
		}
		super.render();

	}

	int plantStep = 0;
	long plantStamp = 0;
	
	void drawBlocks() {
		for (Block b : game.blocks) {
			// b.update(game.tick);
			b.render();
		}
	}

}
