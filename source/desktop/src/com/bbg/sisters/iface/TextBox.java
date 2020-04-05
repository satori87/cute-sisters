package com.bbg.sisters.iface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.bbg.sisters.AssetLoader;
import com.bbg.sisters.Game;

public class TextBox {
	
	Game game;
	
	public int id = 0;
	
	public int x = 0;
	public int y = 0;
	
	public long tick = 0;
	
	public boolean focus = false;
	public boolean blink = false;
	public long blinkStamp = 0;
	
	public int max = 10;
	
	public int width = 32;

	public String text = "";
	
	public TextBox(Game game, int id, int max, boolean focus, int x, int y, int width) {
		this.game = game;
		this.max = max;
		this.focus = focus;
		this.id = id;
		this.x = x - width/2;
		this.y = y + 16;
		this.width = width;

	}
	
	static public boolean inCentered(int x, int y, int centerX, int centerY, int width, int height) {
		int topY = centerY - (height/2);
		int bottomY = centerY + (height/2);
		int leftX = centerX - (width/2);
		int rightX = centerX + (width/2);
		if(x > leftX && x < rightX && y > topY && y < bottomY) {
			return true;
		}
		return false;
	}

	static public boolean inBox(int x, int y, int lowerX, int upperX, int lowerY, int upperY) {
		return (x >= lowerX && x <= upperX && y >= lowerY && y <= upperY);
	}
	
	public void update(long tick) {
		this.tick = tick;
		int mX = game.input.getMouseX();
		int mY = game.input.getMouseY();	
		if(game.input.wasMouseJustClicked) {
			if(inBox(mX,mY,x,x+width,y-7,y+10+36)) {			
				game.input.wasMouseJustClicked = false;
				for(TextBox t : game.scene.textboxes) {
					t.focus = false;
				}
				Gdx.input.setOnscreenKeyboardVisible(true);				
				focus = true;				
			}
		}
		if(tick > blinkStamp) {
			blink = !blink;
			blinkStamp = tick + 400;
		}
		if(focus) {
			processKeyPress(max);
		}
	}
	
	public void processKeyPress(int max) {
		int a = 0;
		for(Character c : game.input.keyTyped) {
			a = (int) c;
			switch(a) {
			case 32:
				text += " ";
				break;
			case 8:
				if(text.length() > 0) {
					text = text.substring(0, text.length() - 1);
				}
				break;
			case 10:
				focus = false;
				Gdx.input.setOnscreenKeyboardVisible(false);
				break;				
			case 13:
				focus = false;
				Gdx.input.setOnscreenKeyboardVisible(false);
				break;	
			default:
				if(a >= 33 && a <= 126) {
					text += c;
				}
				break;
			}			
		}
		game.input.keyTyped.clear();
	}
	
	public void render() {
		//render thyself, peasant
		int l = 0;
		game.drawRegion(AssetLoader.field[l][0], x, y, false, 0, 2);
		for (int b = 42; b < width - 42; b += 32) {
			game.drawRegion(AssetLoader.field[l][1], x + b, y, false, 0, 2);
		}
		game.drawRegion(AssetLoader.field[l][2], x + width - 42, y, false, 0, 2);

		game.drawFont(0, x+width/2, y+28-2, text, true, 1.6f, Color.WHITE);
		if(focus && blink) {game.drawFont(0,(int)(x+width/2+(AssetLoader.getStringWidth(text, 1.6f, 1, 0))/2),y+28,"|",true,1.6f,Color.WHITE);}
		
	}
	
	
}
