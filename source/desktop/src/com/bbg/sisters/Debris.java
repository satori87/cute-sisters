package com.bbg.sisters;

public class Debris {

	Game game;

	public float x = 0;
	public float y = 0;

	public int tx = 0;
	public int ty = 0;

	public int type = 0;

	public float vx = 0;
	public float vy = 0;

	public long diesAt = 0;
	public boolean active = true;

	public Debris(Game game, int tx, int ty, int type, float vx, float vy) {
		this.game = game;
		this.tx = tx;
		this.ty = ty;
		this.type = type;
		x = tx * 32 + 16;
		y = ty * 32 + 16;
		this.vx = vx;
		this.vy = vy;
		diesAt = game.tick + 150;
	}

	public void update(long tick) {
		if (tick > diesAt) {
			active = false;
		}
		if (!active) {
			return;
		}
		x += vx;
		y += vy;
	}

	public void render() {
		if (!active) {
			return;
		}
		game.draw(AssetLoader.debris, x, game.height - y - 32, type * 10, 0, 10, 10, false, true);
	}

}
