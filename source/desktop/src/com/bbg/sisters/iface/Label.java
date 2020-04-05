package com.bbg.sisters.iface;


import com.badlogic.gdx.graphics.Color;
import com.bbg.sisters.Game;

public class Label {

	Game game;

	public int x = 0;
	public int y = 0;

	public boolean blinking = false;
	public Color blinkCol = Color.RED;
	public boolean blink = false;
	public int blinkterval = 300;
	public long blinkStamp = 0;
	public boolean centered = false;

	public boolean wrap = false;
	public int wrapw = 0;

	public String text = "bunbun";
	public float scale = 1.0f;
	public Color color = Color.WHITE;

	public Label(Game game, int x, int y, float scale, String text, Color color, boolean centered) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.color = color;
		this.text = text;
		this.centered = centered;
	}

	public void blink(Color c, int b) {
		blinkCol = c;
		blinking = true;
		blinkterval = b;
	}

	public void render() {
		// render thyself, peasant
		Color c = color;
		if (blinking && blink) {
			c = blinkCol;
		}
		if (wrap) {
			int u = 0;
			for(String b : Dialog.wrapText(2,wrapw,text)) {			
				game.drawFont(0,x,y+u*30,b,false,scale,Color.WHITE);
				u++;
			}
		} else {
			game.drawFont(0, x, y, text, centered, scale, c);
		}

	}

	public void update(long tick) {
		if (tick > blinkStamp) {
			blink = !blink;
			blinkStamp = tick + blinkterval;
		}
	}
}
