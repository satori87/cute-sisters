package com.bbg.sisters;

public class Map {

	public int tile[][][];
	public int set[][][];
	public int att[][][];
	public String name = "Cute Forest";
	public int startX = 32;
	public int startY = 96;

	
	
	public Map() {
		this(100, 100);
	}

	public Map(int width, int height) {
		tile = new int[width][height][4];
		set = new int[width][height][4];
		att = new int[width][height][6];
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				for (int a = 0; a < 4; a++) {
					tile[x][y][a] = 0;
					set[x][y][a] = 0;
				}
				for (int a = 0; a < 6; a++) {
					att[x][y][a] = 0;
				}
			}
		}
		
		for (int x = 0; x < 100; x++) {
			//tile[x][1][0] = 2;
			//tile[x][0][0] = 10;
			
		}
		/*
		tile[7][5][0] = 37;
		tile[8][5][0] = 38;
		tile[9][5][0] = 39;
		tile[7][5][2] = 1;
		tile[8][5][2] = 1;
		tile[9][5][2] = 1;
		
		tile[11][7][0] = 5;
		tile[11][7][2] = 1;
		tile[12][7][0] = 5;
		tile[12][7][2] = 1;
		
		tile[15][9][0] = 5;
		tile[15][9][2] = 1;
		tile[16][9][0] = 5;
		tile[16][9][2] = 1;
		
		tile[19][12][0] = 5;
		tile[19][12][2] = 1;
		tile[20][12][0] = 5;
		tile[20][12][2] = 1;
		
		tile[23][15][0] = 5;
		tile[23][15][2] = 1;
		tile[24][15][0] = 5;
		tile[24][15][2] = 1;
		
		tile[27][18][0] = 5;
		tile[27][18][2] = 1;
		tile[28][18][0] = 5;
		tile[28][18][2] = 1;
		
		for(int y = 0; y < 18; y++) {
			tile[29][y][0] = 5;
			tile[29][y][2] = 1;
			
		}
		*/
		
	}
}
