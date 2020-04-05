package com.bbg.sisters.iface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.bbg.sisters.AssetLoader;
import com.bbg.sisters.Game;

public class Button {

	Game game;

	public int id = 0;

	public boolean dialog = false;

	public boolean sel = false;
	public boolean disabled = false;

	public int x = 0;
	public int y = 0;

	public boolean click = false;

	public int width = 32;
	public int height = 32;
	public int type = 0;
	
	public float fontSize = 32f / 24f;
	
	public boolean toggle = false;

	public String text = "button";

	public Button(Game game, int id,  int x, int y, int width, int height, String text) {
		this.game = game;
		this.id = id;
		this.x = x;
		this.y = y;
		this.type = type;
		this.width = width;
		this.height = height;
		this.text = text;
		fontSize = height / 24f;
	}
	
	
	static public boolean inCentered(int x, int y, int centerX, int centerY, int width, int height) {
		int topY = centerY - (height / 2);
		int bottomY = centerY + (height / 2);
		int leftX = centerX - (width / 2);
		int rightX = centerX + (width / 2);
		if (x > leftX && x < rightX && y > topY && y < bottomY) {
			return true;
		}
		return false;
	}

	static public boolean inBox(int x, int y, int lowerX, int upperX, int lowerY, int upperY) {
		return (x >= lowerX && x <= upperX && y >= lowerY && y <= upperY);
	}

	public void update(long tick) {
		
		click = false;
		int mX = game.input.getMouseX();
		int mY = game.input.getMouseY();
		if (disabled) {
			return;
		}
		if (game.input.wasMouseJustClicked) {
			if (inCentered(mX, mY, x, y, width, height)) {
				game.input.wasMouseJustClicked = false;
				if (!toggle) {
					click = true;
				}
				if (dialog) {
					if (game.scene.activeDialog != null) {
						game.scene.activeDialog.choose(this);
					}
				} else {
					game.scene.buttonPressed(id);
				}
			} else {
				// System.out.println(mX + "," + mY + " not " + x + "," + y);
			}
		}
	}

	public void render() {
		// render thyself, peasant
		x -= (width / 2);
		y -= (height / 2);
		TextureRegion[][] button = AssetLoader.button;
		int p = 0;
		if (click || sel || disabled) {
			p = 1;
		} else {
			p = 0;
		}
		for (int a = 8; a < height - 8; a += 8) {
			for (int b = 8; b < width - 8; b += 8) {
				game.drawRegion(button[p][8], x + b, y + a, false, 0, 1);
			}
		}
		// draw top left
		game.drawRegion(button[p][0], x, y, false, 0, 1);
		// top right
		game.drawRegion(button[p][1], x + width - 8, y, false, 0, 1);
		// bottom left
		game.drawRegion(button[p][2], x, y + height - 8, false, 0, 1);
		// bottom right
		game.drawRegion(button[p][3], x + width - 8, y + height - 8, false, 0, 1);

		// left side
		for (int b = 8; b < height - 8; b += 8) {
			game.drawRegion(button[p][4], x, y + b, false, 0, 1);
		}
		// right side
		for (int b = 8; b < height - 8; b += 8) {
			game.drawRegion(button[p][5], x + width - 8, y + b, false, 0, 1);
		}
		// top side
		for (int b = 8; b < width - 8; b += 8) {
			game.drawRegion(button[p][6], x + b, y, false, 0, 1);
		}
		// bottom side
		for (int b = 8; b < width - 8; b += 8) {
			game.drawRegion(button[p][7], x + b, y + height - 8, false, 0, 1);
		}

		x += (width / 2);
		y += (height / 2);

		Color c = Color.WHITE;
		if (disabled) {
			c = Color.GRAY;
		}
		game.drawFont(0, x, y, text, true, fontSize, c);
	}
}
