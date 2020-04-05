package com.bbg.sisters;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class Input implements InputProcessor {

	Game game;
	
	public List<Integer> keyPress; //store in parallel so we can process first-time clicks
	
	public List<Character> keyTyped;
	
	public boolean [] keyDown;  //as well as whats still being held down
	public boolean mouseDown[];
	public boolean wasMouseJustClicked = false;
	public boolean wasMouseJustReleased = false;
	
	public int mouseDownX = 0;
	public int mouseDownY = 0;
	public int mouseUpX = 0;
	public int mouseUpY = 0;
	public int mouseX = 0;
	public int mouseY = 0;
	
	public Input(Game game) {
		this.game = game;
		mouseDown = new boolean[20];
		for(int i = 0; i < 20; i++) {
			mouseDown[i] = false;
		}
		keyPress = new ArrayList<Integer>();
		keyTyped = new ArrayList<Character>();
		keyDown = new boolean[256];
		for(int i=0;i<256;i++){keyDown[i]=false;}
	}
	
	public synchronized int getMouseX() {
		return mouseX - game.originX;
	}
	
	public synchronized int getMouseY() {
		return mouseY - game.originY;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		keyDown[keycode] = true;
		keyPress.add(keycode);
		//return true;
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		keyDown[keycode] = false;
		//return true;
		return false;
	}
	
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    	mouseX = (int)(((float) screenX / Gdx.graphics.getWidth()) * game.viewWidth);
    	mouseY =  (int)(((float) screenY / Gdx.graphics.getHeight()) * game.viewHeight);
    	mouseDown[button] = true;
    	mouseDownX = mouseX;
    	mouseDownY = mouseY;
		wasMouseJustClicked = true;
        return true; // Return true to say we handled the touch.
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    	mouseX = (int)(((float) screenX / Gdx.graphics.getWidth()) * game.viewWidth);
    	mouseY =  (int)(((float) screenY / Gdx.graphics.getHeight()) * game.viewHeight);
    	mouseUpX = mouseX;
    	mouseUpY = mouseY;
    	mouseDown[button] = false;
    	wasMouseJustReleased = true;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
    	mouseX = (int)(((float) screenX / Gdx.graphics.getWidth()) * game.viewWidth);
    	mouseY =  (int)(((float) screenY / Gdx.graphics.getHeight()) * game.viewHeight);
        return false;
    }

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouseX = (int)(((float) screenX / Gdx.graphics.getWidth()) * game.viewWidth);
    	mouseY =  (int)(((float) screenY / Gdx.graphics.getHeight()) * game.viewHeight);
		return false;
	}
	
    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
    	keyTyped.add(character);
        return true;
    }
    	
}
