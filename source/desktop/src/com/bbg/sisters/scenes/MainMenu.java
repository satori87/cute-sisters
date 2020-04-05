package com.bbg.sisters.scenes;

import com.badlogic.gdx.graphics.Color;
import com.bbg.sisters.Game;
import com.bbg.sisters.iface.Frame;
import com.bbg.sisters.iface.Label;
import com.bbg.sisters.iface.Scene;

public class MainMenu extends Scene {

	public MainMenu(Game game) {
		this.game = game;

	}

	public void start(long tick) {
		labels.add(new Label(game, 540, 244, 4f, "Cute Sisters", Color.WHITE, true));

		frames.add(new Frame(game, 540, 360, 512, 416, true, true));
		addButtons(540, 424, 448, 48, 12, new String[] { "New Game", "", "", "Map Editor" },
				new int[] { 0, 1, 2, 3 });

		//buttons.get(2).disabled = true;
		//buttons.get(3).disabled = true;
		super.start(tick);
	}

	public void buttonPressed(int id) {
		switch (id) {
		case 0: // play now
			game.newGame();			
			break;
		case 1: //			
			break;
		case 2: 
			break;
		case 3: 
			game.changeScene(game.editScene);
			break;
		}
	}

	public void buttonReleased(int id) {

	}

	public void update(long tick) {
		super.update(tick);
		super.render();
	}

}
