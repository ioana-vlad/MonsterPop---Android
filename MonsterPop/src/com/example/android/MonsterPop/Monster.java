package com.example.android.MonsterPop;

public class Monster {
	int x,y;
	byte color;
	public static int radius = 8, colNo = 4;
	
	public Monster(int x1, int y1, byte c){
		x = x1;
		y = y1;
		color = c;
	}
	
	public boolean equal(Monster p){
		return p.color == color ? true:false;
	}
}
	