package com.bbg.sisters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {

	public static Texture texture;

	public static Texture coins;
	public static Texture items;
	public static Texture keys;
	public static Texture locks;

	public static Texture[] plant;

	public static Texture debris;
	public static Texture att;
	public static Texture[] tiles;
	public static Texture[][][] sprites;
	public static TextureRegion[] frame;
	public static TextureRegion[][] button;
	public static TextureRegion[][] field; // lit, section
	public static TextureRegion[][] font; // type, ascii code
	public static int[] fontWidth; // type, ascii code
	public static int[] fontX;

	public static boolean loaded = false;

	public static void load() {
		loadFrame();
		loadFonts();

		plant = new Texture[4];
		for (int i = 0; i < 4; i++) {
			loadTexture("p" + i);
			plant[i] = texture;
		}

		tiles = new Texture[10];
		for (int i = 0; i < 7; i++) {
			loadTexture("t" + i);
			tiles[i] = texture;
		}
		sprites = new Texture[2][20][2];
		loadTexture("stand");
		sprites[0][0][0] = texture;
		loadTexture("walk");
		sprites[0][1][0] = texture;
		loadTexture("jump");
		sprites[0][2][0] = texture;	
		loadTexture("fall");
		sprites[0][4][0] = texture;
		loadTexture("crouch");
		sprites[0][6][0] = texture;
		loadTexture("crouchwalk");
		sprites[0][7][0] = texture;
		loadTexture("dash");
		sprites[0][8][0] = texture;
		loadTexture("brake");
		sprites[0][9][0] = texture;
		loadTexture("climb");
		sprites[0][10][0] = texture;
		loadTexture("swimidle");
		sprites[0][11][0] = texture;
		loadTexture("swimfeet");
		sprites[0][12][0] = texture;
		loadTexture("swimhands");
		sprites[0][13][0] = texture;
		loadTexture("swimboth");
		sprites[0][14][0] = texture;


		loadTexture("standa");
		sprites[0][0][1] = texture;
		loadTexture("walka");
		sprites[0][1][1] = texture;
		loadTexture("walka");
		sprites[0][9][1] = texture;
		loadTexture("jumpa");
		sprites[0][2][1] = texture;
		loadTexture("jumpa");
		sprites[0][4][1] = texture;
		loadTexture("croucha");
		sprites[0][6][1] = texture;
		loadTexture("crouchwalka");
		sprites[0][7][1] = texture;
		loadTexture("dasha");
		sprites[0][8][1] = texture;
		loadTexture("climba");
		sprites[0][10][1] = texture;
		loadTexture("swima");
		sprites[0][11][1] = texture;
		loadTexture("swima");
		sprites[0][12][1] = texture;
		loadTexture("swima");
		sprites[0][13][1] = texture;
		loadTexture("swima");
		sprites[0][14][1] = texture;


		loadTexture("debris");
		debris = texture;

		loadTexture("att");
		att = texture;

		loadTexture("coins");
		coins = texture;
		loadTexture("keys");
		keys = texture;
		loadTexture("locks");
		locks = texture;
		loadTexture("items");
		items = texture;

		loaded = true;
	}

	public static TextureRegion newTR(Texture tex, int x, int y, int w, int h) {
		TextureRegion t = new TextureRegion(tex, x, y, w, h);
		fix(t, false, true);
		return t;
	}

	public static void loadFrame() {
		int i = 0;

		loadTexture("frame");

		frame = new TextureRegion[19];
		for (i = 0; i < 8; i++) {
			frame[i] = newTR(texture, i * 32, 0, 32, 32);
		}
		frame[8] = newTR(texture, 0, 56, 8, 8);
		frame[9] = newTR(texture, 200, 42, 2, 22);
		frame[10] = newTR(texture, 214, 42, 2, 22);
		frame[11] = newTR(texture, 62 + 16, 56, 16, 16);
		frame[12] = newTR(texture, 62 + 16, 56 + 16, 16, 16);
		frame[13] = newTR(texture, 62, 56, 16, 16);
		frame[14] = newTR(texture, 62, 56 + 16, 16, 16);
		frame[15] = newTR(texture, 94, 56 + 16, 12, 16);
		frame[16] = newTR(texture, 94, 56, 12, 16);
		frame[17] = newTR(texture, 106, 56, 13, 13);
		frame[18] = newTR(texture, 106, 56 + 13, 13, 13);

		button = new TextureRegion[2][9];

		for (int b = 0; b < 2; b++) {
			for (i = 0; i < 8; i++) {
				button[b][i] = new TextureRegion(texture, 119 + i * 8, 56 + b * 8, 8, 8);
				fix(button[b][i], false, true);
			}
			button[b][8] = new TextureRegion(texture, 119 + 64, 56 + b * 8, 8, 8);
			fix(button[b][8], false, true);
		}
		field = new TextureRegion[2][3];
		for (int b = 0; b < 2; b++) {
			field[b][0] = new TextureRegion(texture, 0, 88 + b * 26, 42, 26);
			fix(field[b][0], false, true);
			field[b][1] = new TextureRegion(texture, 42, 88 + b * 26, 32, 26);
			fix(field[b][1], false, true);
			field[b][2] = new TextureRegion(texture, 74, 88 + b * 26, 41, 26);
			fix(field[b][2], false, true);

		}

		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	}

	public static void loadFonts() {

		int i = 0;
		loadTexture("font");
		font = new TextureRegion[2][256];
		fontWidth = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 7, 5, 6, 9, 9, 10, 10, 3, 5, 5, 9, 7, 3, 8, 3, 7, 7, 5, 7, 7, 8, 7, 7, 7, 7, 7, 3, 3, 8, 6, 8,
				7, 9, 7, 7, 7, 8, 7, 7, 7, 7, 5, 8, 7, 7, 9, 8, 7, 7, 8, 8, 7, 7, 7, 7, 9, 8, 7, 7, 5, 7, 5, 10, 7, 5,
				7, 7, 7, 7, 7, 6, 7, 7, 5, 5, 7, 4, 9, 7, 7, 7, 8, 7, 7, 7, 7, 7, 9, 7, 7, 7, 6, 3, 6, 8, 9, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		fontX = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 1, 10, 17, 25, 36, 47, 59, 71, 76, 83, 90, 101, 110, 115, 125, 130, 139, 148, 155, 164, 173, 183,
				192, 201, 210, 219, 228, 233, 238, 248, 256, 266, 275, 286, 295, 304, 313, 323, 332, 341, 350, 359, 366,
				376, 385, 394, 405, 415, 424, 433, 443, 453, 462, 471, 480, 489, 500, 510, 519, 528, 535, 544, 551, 563,
				572, 579, 588, 597, 606, 615, 624, 632, 641, 650, 657, 664, 673, 679, 690, 699, 708, 717, 727, 736, 745,
				754, 763, 772, 783, 792, 801, 810, 818, 823, 831, 841, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0 };

		for (i = 0; i < 256; i++) {
			if (fontWidth[i] > 0) {
				fontWidth[i] += 1;
				for (int t = 0; t < 2; t++) {
					font[t][i] = new TextureRegion(texture, fontX[i], t * 16, fontWidth[i], 16);
					fix(font[t][i], false, true);
					font[t][i].getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
				}
			}
		}
	}

	public static void loadTexture(String name) {
		texture = new Texture(Gdx.files.internal("assets/" + name + ".png"));
		texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
	}

	public static void fix(TextureRegion tex, boolean flipX, boolean flipY) {
		// fixBleeding(tex);
		tex.flip(flipX, flipY);
		tex.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		// tex.getTexture().setFilter(TextureFilter.Linear,TextureFilter.Linear);
	}

	public static void fixBleeding(TextureRegion region) {
		float fix = 0.01f;
		float x = region.getRegionX();
		float y = region.getRegionY();
		float width = region.getRegionWidth();
		float height = region.getRegionHeight();
		float invTexWidth = 1f / region.getTexture().getWidth();
		float invTexHeight = 1f / region.getTexture().getHeight();
		region.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth,
				(y + height - fix) * invTexHeight); // Trims Region
	}

	public void dispose() {
		if (texture != null) {
			texture.dispose();
		}
		// if (frameTex != null) {
		// frameTex.dispose();
		// }
		texture = null;
		// frameTex = null;
		button = null;
		field = null;
		font = null;
		frame = null;

	}

	public static Color getRandomColor() {
		return new Color((float) (Math.random()), (float) (Math.random()), (float) (Math.random()), 1);
	}

	public static float getStringWidth(String s, float scale, float padding, float spacing) {
		float total = 0;
		for (char c : s.toCharArray()) {
			int ascii = (int) c;
			total += fontWidth[ascii] * scale + padding * 2 + spacing;
		}
		return total;
	}

	public static int rndInt(int m) {
		return ((int) (Math.random() * m));
	}

}