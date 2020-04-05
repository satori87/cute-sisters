package com.bbg.sisters.scenes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.bbg.sisters.AssetLoader;
import com.bbg.sisters.Game;
import com.bbg.sisters.Map;
import com.bbg.sisters.iface.Button;
import com.bbg.sisters.iface.Frame;
import com.bbg.sisters.iface.Label;
import com.bbg.sisters.iface.Scene;
import com.bbg.sisters.iface.TextBox;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class EditScene extends Scene {

	List<Button> layerBtns = new LinkedList<Button>();
	List<Button> visBtns = new LinkedList<Button>();
	int curSet = 0;
	int curSetCat = 0;
	int curTile = 0;
	int curTileSet = 0;
	int curWall = 0;
	int layer = 0;
	boolean[] vis = new boolean[12];
	int curX = 0;
	int curY = 0;
	public Frame mapFrame;
	Label set;
	Label cat;

	Label lblCurTile;

	TextBox cur;

	Button save;
	Button load;

	Frame frmDoor;
	Label frmDoorX;
	Label frmDoorY;
	Label frmDoorMap;
	int frmDoorXv = 0;
	int frmDoorYv = 0;
	int frmDoorMapv = 0;

	Frame frmLock;
	Label lblLock;
	int lock = 0;

	Frame frmBlock;
	Label lblBlock;
	Label lblBlock2;
	Label lblBlock3;
	Label lblBlock4;
	int block = 0;
	int block2 = 0;
	int block3 = 0;
	int block4 = 0;

	Label lblHover;

	public EditScene(Game game) {
		this.game = game;

	}

	public void start(long tick) {
		Button b;
		super.start(tick);

		lblHover = new Label(game, 1020, 34, 1f, "hh", Color.WHITE, true);
		lblCurTile = new Label(game, 1000, 10, 1f, "", Color.WHITE, true);
		labels.add(lblCurTile);
		labels.add(lblHover);

		frmLock = new Frame(game, 779, 48, 266, 522, true, false);
		lblLock = new Label(game, 900, 80, 1f, "X: ", Color.WHITE, true);
		frmLock.labels.add(lblLock);

		frmLock.buttons.add(new Button(game, 400, 840, 80, 44, 24, "-"));
		frmLock.buttons.add(new Button(game, 401, 960, 80, 44, 24, "+"));
		frames.add(frmLock);
		frmLock.visible = false;

		frmBlock = new Frame(game, 779, 48, 266, 522, true, false);
		lblBlock = new Label(game, 900, 80, 1f, "X: ", Color.WHITE, true);
		lblBlock2 = new Label(game, 900, 110, 1f, "X: ", Color.WHITE, true);
		lblBlock3 = new Label(game, 900, 140, 1f, "X: ", Color.WHITE, true);
		lblBlock4 = new Label(game, 900, 170, 1f, "X: ", Color.WHITE, true);
		frmBlock.labels.add(lblBlock);
		frmBlock.labels.add(lblBlock2);
		frmBlock.labels.add(lblBlock3);
		frmBlock.labels.add(lblBlock4);
		b = new Button(game, 300, 840, 80, 44, 24, "-");
		frmBlock.buttons.add(b);
		b = new Button(game, 301, 960, 80, 44, 24, "+");
		frmBlock.buttons.add(b);
		b = new Button(game, 302, 840, 110, 44, 24, "-");
		frmBlock.buttons.add(b);
		b = new Button(game, 303, 960, 110, 44, 24, "+");
		frmBlock.buttons.add(b);
		b = new Button(game, 304, 840, 140, 44, 24, "-");
		frmBlock.buttons.add(b);
		b = new Button(game, 305, 960, 140, 44, 24, "+");
		frmBlock.buttons.add(b);
		b = new Button(game, 306, 840, 170, 44, 24, "-");
		frmBlock.buttons.add(b);
		b = new Button(game, 307, 960, 170, 44, 24, "+");
		frmBlock.buttons.add(b);
		frames.add(frmBlock);
		frmBlock.visible = false;

		frmDoor = new Frame(game, 779, 48, 266, 522, true, false);
		frmDoorX = new Label(game, 900, 80, 1f, "X: ", Color.WHITE, true);
		frmDoorY = new Label(game, 900, 110, 1f, "Y: ", Color.WHITE, true);
		frmDoorMap = new Label(game, 900, 140, 1f, "Map: ", Color.WHITE, true);
		frmDoor.labels.add(frmDoorX);
		frmDoor.labels.add(frmDoorY);
		frmDoor.labels.add(frmDoorMap);

		b = new Button(game, 200, 840, 80, 44, 24, "-");
		frmDoor.buttons.add(b);
		b = new Button(game, 201, 960, 80, 44, 24, "+");
		frmDoor.buttons.add(b);
		b = new Button(game, 202, 840, 110, 44, 24, "-");
		frmDoor.buttons.add(b);
		b = new Button(game, 203, 960, 110, 44, 24, "+");
		frmDoor.buttons.add(b);
		b = new Button(game, 204, 840, 140, 44, 24, "-");
		frmDoor.buttons.add(b);
		b = new Button(game, 205, 960, 140, 44, 24, "+");
		frmDoor.buttons.add(b);

		game.input.mouseDown[0] = false;
		curTile = 0;
		addButtons(true, true, 1, 820, 24, 64, 28, 0, new String[] { "Prev" }, new int[] { 0 });
		addButtons(true, true, 1, 960, 24, 64, 28, 0, new String[] { "Next" }, new int[] { 1 });
		// addButtons(true, true, 1, 1040, 24, 64, 28, 0, new String[] { "Prev" }, new
		// int[] { 46 });
		// addButtons(true, true, 1, 1180, 24, 64, 28, 0, new String[] { "Next" }, new
		// int[] { 47 });

		set = new Label(game, 890, 24, 1f, "Set: 0", Color.WHITE, true);
		cat = new Label(game, 1115, 24, 1f, "Terrain", Color.WHITE, true);
		labels.add(set);
		labels.add(cat);
		frames.add(new Frame(game, 779, 48, 266, 522, false, false));
		frames.add(new Frame(game, 779, 582, 42, 42, false, false));
		frames.add(new Frame(game, 779 + 64, 582, 170, 42, false, false));
		layerBtns = addButtons(false, false, 2, 780, 646, 44, 24, 4, new String[] { "BG1", "BG2", "FG", "ATT", "UNU" },
				new int[] { 100, 101, 102, 103, 104 });
		// layerBtns.get(layer).toggle = true;
		save = new Button(game, 44, 800, 675, 44, 24, "Save");
		load = new Button(game, 45, 800, 706, 44, 24, "Load");

		buttons.add(save);
		buttons.add(load);
		cur = new TextBox(game, 1, 3, true, 880, 644, 100);
		textboxes.add(cur);

		frames.add(frmDoor);
		frmDoor.visible = false;
		// mapFrame = new Frame(game, 512, 384, 600, 384, true, true);
	}

	void save(int a) {
		FileOutputStream f;
		try {
			f = new FileOutputStream(new File("map" + a));
			OutputStream outputStream = new DeflaterOutputStream(f);
			Output output = new Output(outputStream);

			game.kryo.writeObject(output, game.map());
			game.maps[a] = game.map();
			output.close();
			f.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void load(int a) {
		FileInputStream f;
		try {

			f = new FileInputStream(new File("map" + a));
			InputStream inputStream = new InflaterInputStream(f);
			Input input = new Input(inputStream);
			game.maps[a] = game.kryo.readObject(input, Map.class);
			input.close();
			f.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
	}

	public void buttonPressed(int id) {
		switch (id) {
		case 0:
			curSet--;
			if (curSet < 0) {
				curSet = 0;
			}
			break;
		case 1:
			curSet++;
			if (curSet > 6) {
				curSet = 6;
			}
			break;
		case 44:
			int i = 0;
			if (cur.text.length() > 0) {
				i = Integer.parseInt(cur.text);
			} else {

			}

			if (i >= 0 && i < 100) {
				// game.curMap = Integer.parseInt(cur.text);
				// for (int j = 0; j < 50; j++) {
				save(i);
				// }
			}

			break;
		case 45:
			game.curMap = Integer.parseInt(cur.text);
			load(game.curMap);
			break;
		case 100: // bg1
			layer = 0;
			break;
		case 101: // bg2
			layer = 1;
			break;
		case 102: // fg
			layer = 2;
			break;
		case 103: // att
			layer = 3;
			frmDoor.visible = false;
			frmBlock.visible = false;
			frmLock.visible = false;
			curAttMode = 0;
			break;
		case 104: // water
			frmDoor.visible = false;
			frmBlock.visible = false;
			frmLock.visible = false;
			layer = 4;
			break;
		case 109:
			game.changeScene(game.gameScene);
			game.changeMap(game.curMap, 96, 96);
			break;
		case 200:
			frmDoorXv--;
			if (frmDoorXv < 0) {
				frmDoorXv = 0;
			}
			break;
		case 201:
			frmDoorXv++;
			if (frmDoorXv > 99) {
				frmDoorXv = 99;
			}
			break;
		case 202:
			frmDoorYv--;
			if (frmDoorYv < 0) {
				frmDoorYv = 0;
			}
			break;
		case 203:
			frmDoorYv++;
			if (frmDoorYv > 99) {
				frmDoorYv = 99;
			}
			break;
		case 204:
			frmDoorMapv--;
			if (frmDoorMapv < 0) {
				frmDoorMapv = 0;
			}
			break;
		case 205:
			frmDoorMapv++;
			if (frmDoorMapv > 99) {
				frmDoorMapv = 99;
			}
			break;
		case 300:
			block--;
			if (block < 0) {
				block = 0;
			}
			break;
		case 301:
			block++;
			if (block > 2) {
				block = 2;
			}
			break;
		case 302:
			block2--;
			if (block2 < 0) {
				block2 = 0;
			}
			break;
		case 303:
			block2++;
			if (block2 > 7) {
				block2 = 7;
			}
			break;
		case 304:
			block3--;
			if (block3 < 0) {
				block3 = 0;
			}
			break;
		case 305:
			block3++;
			if (block3 > 3) {
				block3 = 3;
			}
			break;
		case 306:
			block4--;
			if (block4 < 0) {
				block4 = 0;
			}
			break;
		case 307:
			block4++;
			if (block4 > 99) {
				block4 = 99;
			}
			break;
		case 400:
			lock--;
			if (lock < 0) {
				lock = 0;
			}
			break;
		case 401:
			lock++;
			if (lock > 5) {
				lock = 5;
			}
			break;

		}
	}

	public void buttonReleased(int id) {

	}

	long moveStamp = 0;

	public int curAtt[] = new int[6];

	public void update(long tick) {

		game.moveCameraTo((int) (game.width / 2f), (int) (game.height / 2f));
		for (Integer ii : game.input.keyPress) {
			if (ii == 247) {
				game.changeScene(game.gameScene);
				int m = game.curMap;
				game.curMap = -1;
				game.changeMap(m, 96, 96);
			}
		}

		if (tick > moveStamp) {
			moveStamp = tick + 50;

			lblCurTile.text = (curTile - 1) + "";
			if (game.input.keyDown[19]) { // up
				curY++;
				if (curY > 99) {
					curY = 99;
				}
			} else if (game.input.keyDown[20]) { // down
				curY--;
				if (curY < 0) {
					curY = 0;
				}
			}
			if (game.input.keyDown[21]) { // left
				curX--;
				if (curX < 0) {
					curX = 0;
				}
			} else if (game.input.keyDown[22]) { // right
				curX++;
				if (curX > 99) {
					curX = 99;
				}
			}
		}
		int mx = game.input.mouseX;
		int my = game.input.mouseY;
		if (layer != 3) {
			frmDoor.visible = false;
			frmBlock.visible = false;
			frmLock.visible = false;
			curAttMode = 0;
		}
		frmDoorX.text = "X: " + frmDoorXv;
		frmDoorY.text = "Y: " + frmDoorYv;
		frmDoorMap.text = "Map: " + frmDoorMapv;
		lblBlock.text = "type: " + block;
		lblBlock2.text = "Color:" + block2;
		lblBlock3.text = "Type:" + block3;
		lblBlock4.text = "Item:" + block4;
		lblLock.text = "Key:" + lock;
		if (curAttMode == 2) {
			curAtt[0] = 2;
			curAtt[1] = frmDoorXv;
			curAtt[2] = frmDoorYv;
			curAtt[3] = frmDoorMapv;
		} else if (curAttMode == 3) {
			curAtt[0] = 3;
			curAtt[1] = block;
			curAtt[2] = block2;
			curAtt[3] = block3;
			curAtt[4] = block4;
		} else if (curAttMode == 4) {
			curAtt[0] = 4;
			curAtt[1] = lock;
		}
		if (Button.inBox(mx, my, 0, 767, 0, 719)) {
			int hx = mx / 32 + curX;
			int hy = (int) (game.height - my) / 32 + curY;
			lblHover.text = hx + "," + hy;
		}
		if (game.input.mouseDown[0]) {
			if (Button.inBox(mx, my, 784, 784 + 255, 48, 48 + 511)) {
				// clicked tiles
				int tx = mx - 784;
				int ty = my - 48;
				if (layer < 3) {
					curTile = ((tx / 32) + (ty / 32) * 8) + 1;
				} else if (layer == 3 && curAttMode == 0) {
					int t = ((tx / 32) + (ty / 32) * 8) + 1;
					switch (t) {
					case 1: // wall
						break;
					case 2: // door
						frmDoor.visible = true;
						curAttMode = 2;
						game.input.wasMouseJustClicked = false;
						game.input.mouseDown[0] = false;
						break;
					case 3: // block
						frmBlock.visible = true;
						curAttMode = 3;
						game.input.wasMouseJustClicked = false;
						game.input.mouseDown[0] = false;
						break;
					case 4: // lock
						frmLock.visible = true;
						curAttMode = 4;
						game.input.wasMouseJustClicked = false;
						game.input.mouseDown[0] = false;
						break;
					}
				} else if (layer == 4) {
					curTile = ((tx / 32) + (ty / 32) * 8) + 1;
				}
			}
		}
		if (Button.inBox(mx, my, 0, 767, 0, 719)) {
			// clicked map
			int tx = (mx / 32) + curX;
			int ty = ((720 - my) / 32) + curY;
			if (tx >= 0 && tx < 100) {
				if (ty >= 0 && ty < 100) {
					if (game.input.mouseDown[0]) {
						if (layer < 3) {
							if (curTile > 0) {
								game.map().tile[tx][ty][layer] = curTile;
								game.map().set[tx][ty][layer] = curSet;
							}
						} else if (layer == 3) {
							if (curAtt[0] > 0) {
								for (int a = 0; a < 6; a++) {
									game.map().att[tx][ty][a] = curAtt[a];
								}

							}
						} else if (layer == 4) {
							if (curSet == 5 && curTile > 0) {
								game.map().tile[tx][ty][3] = curTile;
								game.map().set[tx][ty][3] = 5;
							}
						}
					} else if (game.input.mouseDown[1]) {
						if (layer < 3) {
							game.map().tile[tx][ty][layer] = 0;
							game.map().set[tx][ty][layer] = 0;
						} else if (layer == 3) {
							game.map().att[tx][ty][0] = 0;
						} else if (layer == 4) {
							game.map().tile[tx][ty][3] = 0;
							game.map().set[tx][ty][3] = 0;
						}
					}
				}
			}
		}

		super.update(tick);
		game.input.keyPress.clear();
	}

	public int curAttMode = 0;

	public void render() {
		super.render();
		int x = 0;
		int y = 0;
		game.batcher.flush();
		Rectangle scissors = new Rectangle();
		Rectangle clipBounds = new Rectangle(0, 0, 768, 720);
		ScissorStack.calculateScissors(game.cam, game.batcher.getTransformMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(scissors);
		for (int mx = 0; mx < 24; mx++) {
			for (int my = 0; my < 22; my++) {
				x = mx + curX;
				y = my + curY;
				float dx = mx * 32;
				float dy = game.height - (my * 32) - 32;
				if (x >= 0 && x < 100 && y >= 0 && y < 100) {
					int a = 4;
					int t = game.map().tile[x][y][3];
					if (t > 0) {
						t -= 1;
						game.draw(AssetLoader.tiles[5], dx, dy, (t % 8) * 32, (t / 8) * 32, 32, 32, false, true);
					}
					for (a = 0; a < 3; a++) {
						t = game.map().tile[x][y][a];
						if (t > 0) {
							t -= 1;
							game.draw(AssetLoader.tiles[game.map().set[x][y][a]], dx, dy, (t % 8) * 32, (t / 8) * 32,
									32, 32, false, true);
						}
					}
					t = game.map().att[x][y][0];
					if (t > 0) {
						t -= 1;
						game.draw(AssetLoader.att, dx, dy, (t % 8) * 32, (t / 8) * 32, 32, 32, false, true);
					}
				}
			}
		}
		int tx = (curTile - 1) % 8;
		int ty = ((curTile - 1) / 8);

		game.batcher.flush();
		ScissorStack.popScissors();
		if (layer < 3) {
			game.draw(AssetLoader.tiles[curSet], 784, 53, 0, 0, 256, 512, false, true);
			game.draw(AssetLoader.tiles[curSet], 784, 587, tx * 32, ty * 32, 32, 32, false, true);
		} else if (layer == 3) {
			if (curAttMode == 0) {
				game.draw(AssetLoader.att, 784, 53, 0, 0, 256, 512, false, true);
				game.draw(AssetLoader.att, 784, 587, tx * 32, ty * 32, 32, 32, false, true);
			}
		} else if (layer == 4) {
			game.draw(AssetLoader.tiles[curSet], 784, 53, 0, 0, 256, 512, false, true);
			game.draw(AssetLoader.tiles[curSet], 784, 587, tx * 32, ty * 32, 32, 32, false, true);
		}
	}
}
