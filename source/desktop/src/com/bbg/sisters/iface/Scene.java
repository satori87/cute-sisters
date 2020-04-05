package com.bbg.sisters.iface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bbg.sisters.Game;

public abstract class Scene {

	public Game game;

	public List<Frame> frames;
	public List<Button> buttons;
	public List<Label> labels;
	public List<TextBox> textboxes;
	public List<Dialog> dialogs;

	public Label lblStatus;
	public long statusStamp = 0;

	public long startStamp = 0;

	public abstract void buttonPressed(int id);


	public int dialogX = 540;
	public int dialogY = 360;

	public Dialog activeDialog;

	public Scene() {
		frames = new LinkedList<Frame>();
		buttons = new LinkedList<Button>();
		labels = new LinkedList<Label>();
		textboxes = new LinkedList<TextBox>();
		dialogs = new ArrayList<Dialog>();
	}

	public List<Button> addButtons(boolean centered, boolean updown, int type, int x, int y, int width, int height, int padding, String[] text, int[] ids) {
		List<Button> bl = new LinkedList<Button>();
		int n = text.length;
		int bX = 0;
		int bY = 0;
		if (centered) {
			if (updown) {
				bX = x;
				bY = y - (padding / 2) - (height / 2) - (((n - 1) / 2) * (padding + height));
			} else {
				bX = x - (padding / 2) - (width / 2) - (((n - 1) / 2) * (padding + width));
				bY = y;
			}
		} else {
			bX = x;
			bY = y;
		}
		if (n % 2 != 0) {
			if (updown) {
				bY += (padding + height) / 2;
			} else {
				bX += (padding + width) / 2;
			}
		}
		for (int c = 0; c < n; c++) {
			Button b = new Button(game, ids[c], bX, bY, width, height, text[c]);
			bl.add(b);
			buttons.add(b);
			if (updown) {
				bY += (height + padding);
			} else {
				bX += (width + padding);
			}
		}
		return bl;
	}
	
	public void update(long tick) {
		// if (!started) {
		// return;
		// }
		List<Dialog> drops = new LinkedList<Dialog>();
		for (Dialog d : dialogs) {
			if (!d.done) {
				if (d.active) {
					d.update(tick);
				} else if (activeDialog == null) {
					if (d.timed && (tick > d.timeStamp)) {
						System.out.println(tick + "," + game.tick);
						d.start(tick);
					}
				}
			} else {
				drops.add(d);
			}
		}

		drops.clear();
		for (Frame d : frames) {
			d.update(tick);
		}
		for (Button b : buttons) {
			b.update(tick);
		}

		for (TextBox t : textboxes) {
			t.update(tick);
		}
		if (game.input.wasMouseJustClicked) { // none of the scene objects caught this
			game.input.wasMouseJustClicked = false;
			touchDown(game.input.mouseDownX, game.input.mouseDownY);
		}

	}
	
	public void render() {
		//overload only in some scenes
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
		if (activeDialog != null) {
			activeDialog.render();
		}
		if (game.tick > statusStamp) {
			if (lblStatus != null) {
				lblStatus.text = "";
			}
		}
	}

	public void touchDown(int x, int y) {
		// non dialog, non button, non text touches. overload this in specific scene
	}

	public void clear(long tick) {
		dialogs.clear();
		frames.clear();
		buttons.clear();
		labels.clear();
		textboxes.clear();
		startStamp = tick;
		activeDialog = null;
	}
	
	public void start(long tick) {
		startStamp = tick;
		activeDialog = null;
		for (Dialog d : dialogs) {
			if (d.timed) {
				System.out.println(tick + "," + game.tick);
				d.timeStamp = tick + d.time;
			}
		}
	}

	public Dialog addDialog(int id, int nextType, int next, int pic, int nextTime, String text) {
		Dialog d;
		d = new Dialog(game, id, nextType, next, nextTime, pic, 600, text);
		d.x = dialogX;
		d.y = dialogY;
		dialogs.add(id, d);
		return d;
	}
	
	public void addButtons(int x, int y, int width, int height, int padding, String[] text, int[] ids) {
		int n = text.length;
		int bX = x;
		int bY = y - (padding / 2) - (height / 2) - (((n - 1) / 2) * (padding + height));
		if (n % 2 != 0) {
			bY += (padding + height) / 2;
		}
		for (int c = 0; c < n; c++) {
						
			buttons.add(new Button(game, ids[c], bX, bY, width, height, text[c]));
			bY += (height + padding);
		}
	}

}
