package com.bbg.sisters.iface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bbg.sisters.AssetLoader;
import com.bbg.sisters.Game;

public class Frame {

	Game game;

	public List<Frame> frames;
	public List<Button> buttons;
	public List<Label> labels;
	public List<TextBox> textboxes;
	public boolean visible = true;
	public boolean useBackground = true;
	public boolean centered = false;

	public int x = 0;
	public int y = 0;

	public int width = 32;
	public int height = 32;

	public Frame(Game game, int x, int y, int width, int height, boolean useBackground, boolean centered) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.useBackground = useBackground;
		this.centered = centered;
		frames = new LinkedList<Frame>();
		buttons = new LinkedList<Button>();
		labels = new LinkedList<Label>();
		textboxes = new LinkedList<TextBox>();
	}

	public void update(long tick) {
		if(!visible) {
			return;
		}
		for (Frame d : frames) {
			d.update(tick);
		}
		for (Button b : buttons) {
			b.update(tick);
		}

		for (TextBox t : textboxes) {
			t.update(tick);
		}
	}

	public void render() {
		if(!visible) {
			return;
		}
		if (centered) {
			this.x -= (width / 2);
			this.y -= (height / 2);
		}
		TextureRegion[] frame = AssetLoader.frame;

		if (useBackground) {
			for (int a = 0; a < height; a += 8) {
				for (int b = 0; b < width; b += 8) {
					game.drawRegion(frame[8], x + b, y + a, false, 0, 1);

				}
			}
		}

		// draw top left
		game.drawRegion(frame[0], x, y, false, 0, 1);
		// top right
		game.drawRegion(frame[1], x + width - 32, y, false, 0, 1);
		// bottom left
		game.drawRegion(frame[2], x, y + height - 32, false, 0, 1);
		// bottom right
		game.drawRegion(frame[3], x + width - 32, y + height - 32, false, 0, 1);

		// left side
		for (int b = 32; b <= height - 32; b += 32) {
			game.drawRegion(frame[4], x, y + b, false, 0, 1);
		}
		// right side
		for (int b = 32; b <= height - 32; b += 32) {
			game.drawRegion(frame[5], x + width - 32, y + b, false, 0, 1);
		}
		// top side
		for (int b = 32; b <= width - 32; b += 32) {
			game.drawRegion(frame[6], x + b, y, false, 0, 1);
		}
		// bottom side
		for (int b = 32; b <= width - 32; b += 32) {
			game.drawRegion(frame[7], x + b, y + height - 32, false, 0, 1);
		}
		if (centered) {
			this.x += (width / 2);
			this.y += (height / 2);
		}

		for (Frame d : frames) {
			d.render();
		}
		for (Button b : buttons) {
			b.render();
		}
		for (Label l : labels) {
			l.render();
		}
		for (TextBox t : textboxes) {
			t.render();
		}
		
	}

}
