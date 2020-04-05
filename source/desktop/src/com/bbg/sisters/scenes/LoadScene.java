package com.bbg.sisters.scenes;

import com.badlogic.gdx.graphics.Color;
import com.bbg.sisters.Game;
import com.bbg.sisters.iface.Label;
import com.bbg.sisters.iface.Scene;

public class LoadScene extends Scene {

	public LoadScene(Game game) {
		this.game = game;
		labels.add(new Label(game, 540, 244, 4f, "Loading", Color.WHITE, true));
	}

	public void buttonPressed(int id) {
	
	}

	public void buttonReleased(int id) {

	}

	public void update(long tick) {
		super.update(tick);
		super.render();
	}

}
