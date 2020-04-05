package com.bbg.sisters.iface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.bbg.sisters.AssetLoader;
import com.bbg.sisters.Game;

public class Dialog {

	Game game;

	public int width = 0;
	public int height = 0;
	
	public int next = -1;
	public boolean lead = false;
	public int nextTime = 0;
	public int nextType = 0;

	public int y = 360;
	public int x = 540;
	
	public boolean timed = false;
	public long timeStamp = 0;
	public int time = 0;
	
	public boolean done = false;
	public boolean active = false;

	public List<Button> choices;
	public Label msg;
	public Frame frame;

	public int id = 0;
	public int pic = 0;
	
	public int xO = 0;
	public int yO = 0;
	
	public String text;
	
	public String[] choicetext;
	public int[] choiceid;
	public Dialog(Game game, int id, int nextType, int next, int nextTime, int pic, int width, String text, String[] choicetext,
			int[] choiceid) {
		this.game = game;
		this.id = id;
		this.width = width;
		this.pic = pic;
		this.next = next;
		this.nextType = nextType;
		this.nextTime = nextTime;
		this.choiceid = choiceid;
		this.choicetext = choicetext;
		this.text = text;	
		if(nextType < 0) {
			lead = false;
		} else {
			lead = true;
		}
	}

	public Dialog(Game game, int id, int nextType, int next, int nextTime, int pic, int width, String text) {
		this(game, id, nextType, next, nextTime, pic, width, text, new String[] { "Ok" }, new int[] { 0 });
	}

	public void setchoices(String[] choicetext, int[] choiceid) {
		this.choicetext = choicetext;
		this.choiceid = choiceid;
	}
	
	public void lead(int type, int to, int time) {
		next = to;
		nextTime = time;
		this.nextType = type;
		lead = true;
	}

	public void choose(Button b) {
		game.scene.activeDialog = null;
		active = false;
		game.scene.buttonPressed(b.id);
		done = true;
		if (lead) {
			switch(nextType) {
			case 0:
				if (next < game.scene.dialogs.size()) {
					game.scene.dialogs.get(next).timed = true;
					game.scene.dialogs.get(next).timeStamp = game.tick + nextTime;
				}
				break;
			case 1:
				//if (next < game.scene.sequences.size()) {
				//	game.scene.sequences.get(next).timed = true;
				//	game.scene.sequences.get(next).timeStamp = game.tick + nextTime;
				//}
			}
		}		
	}
	
	public void start(long tick) {
		
		game.scene.activeDialog = this;
		active = true;
		if (pic < 100) {
			xO = 96;
			yO = 32;
			text = "\"" + text + "\"";
		}
		int textHeight = (wrapText(2, width - 40 - xO, text).size() * 30) + 32 + yO;
		if (textHeight < 176) {
			textHeight = 176;
		}
		height = textHeight + choicetext.length * 60;
		frame = new Frame(game, x, y, width, height - 2, true, true);
		msg = new Label(game, x - (width / 2) + 8 + xO, y - (height / 2) + 8 + 8+yO, 2, text, Color.WHITE, false);
		msg.wrapw = width - 32 - xO;
		msg.wrap = true;
		choices = new LinkedList<Button>();
		Button b;
		for (int i = 0; i < choicetext.length; i++) {
			b = new Button(game, choiceid[i], x, y - height / 2 + textHeight + 24, width - 16, 48, choicetext[i]);
			b.dialog = true;
			choices.add(b);
		}
		if (pic < 100) {
			//crew = (Crew)game.ship.crew[pic].clone();
			
		}
	}

	public void update(long tick) {	
		
		for (Button b : choices) {
			b.update(tick);
		}
		if (pic >= 0 && pic < 100) {
			//game.crew[pic].update(tick);
		}
	}

	public static List<String> wrapText(float scale, int width, String text) {
		List<String> lines = new ArrayList<String>();
		String line = "";
		String word = "";
		for (int c = 0; c < text.length(); c++) { // read string one byte at a time, and check width at every char
			String p = text.substring(c, c + 1); // single letter
			if (p.equals(" ")) { // finished a word, try to add it on
				if (line.length() > 0) {
					if (AssetLoader.getStringWidth(line + " " + word, scale, 0, 1) > width) { // wont fit, start new
																								// line
						lines.add(line);
						line = word;
						word = "";
					} else { // this word fits no problem
						line += " " + word;
						word = "";
					}
				} else { // we're on first word of line, is it too wide?
					if (AssetLoader.getStringWidth(word, scale, 0, 1) > width) {
						line = word + " ";
						word = "";
					} else {
						line = word;
						word = "";
					}
				}
			} else {
				if (line.length() == 0 && AssetLoader.getStringWidth(word + p, scale, 0, 1) > width) {
					// first word is too wide, split it. i.e. AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
					lines.add(word);
					word = "";
					word = p;
				} else if (line.length() > 0
						&& AssetLoader.getStringWidth(line + " " + word + p, scale, 0, 1) > width) {
					lines.add(line);
					line = "";
					word += p;
				} else { // keep adding to this worddddd
					word += p;
				}
			}
		}
		if (word.length() > 0) { // get last word, since loop only makes new lines when it reaches end
			line += " " + word;
		}
		if (line.length() > 0) { // get last line, same reason as above
			lines.add(line);
		}
		return lines;
	}

	public void render() {
		frame.render();
		msg.render();
		if (pic < 100 && pic >= 0) {
			//game.crew[pic].render(x - width / 2 + 8 + 32+12, y - height / 2 + 8 + 64, 3f, true, x - (width/2) + 96,  y - (height/2) + 12);
		}
		for (Button b : choices) {
			b.render();
		}
	}

}
