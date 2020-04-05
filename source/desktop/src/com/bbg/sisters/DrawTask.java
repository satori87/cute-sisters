package com.bbg.sisters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DrawTask {
	
	Game game;
	
	public boolean centered, flipX, flipY;
	public Texture texture;
	public TextureRegion region;
	public int type, font, x, y, srcX, srcY, width, height, srcWidth, srcHeight;
	public float rotation, scale;
	public Color col = Color.WHITE;
	public String text;
	
	
	public DrawTask(Game game, Texture texture, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY, Color col) {
		this.game = game;
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.srcX = srcX;
		this.srcY = srcY;
		this.srcWidth = srcWidth;
		this.srcHeight = srcHeight;
		this.flipX = flipX;
		this.flipY = flipY;
		this.col = col;
		type = 1;
	}
	
	public DrawTask(Game game, TextureRegion region, int x, int y, boolean centered, float rotation, float scale, Color col) {
		this.game = game;
		this.region = region;
		this.x = x;
		this.y = y;
		this.centered = centered;
		this.rotation = rotation;
		this.scale = scale;
		this.col = col;
		type = 0;
	}
	
	public DrawTask(Game game, int font, int x, int y, String text, boolean centered, float scale, Color col) {
		this.game = game;
		this.font = font;
		this.x = x;
		this.y = y;
		this.text = text;
		this.centered = centered;
		this.scale = scale;
		this.col = col;
		this.type = 2;
	}
	
	public void render() {
		switch(type) {
		case 0:
			game.drawRegion(region, x, y, centered, rotation, scale);
			break;
		case 1:
			game.batcher.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
			break;
		case 2:
			game.drawFont(font, x, y, text, centered, scale);
			break;
		}
	}
	
}
