package com.example.android.MonsterPop;

import java.util.Random;


public class TwoItems {
	Monster p1,p2;
	
	Random r;
	
	public TwoItems(){
		r = new Random();
		p1 = new Monster(Table.width / 2  , 1, (byte)(r.nextInt(Monster.colNo) + 1));
		p2 = new Monster(Table.width / 2 + 1, 1, (byte)(r.nextInt(Monster.colNo) + 1));
	}
	
	public TwoItems(byte type1, byte type2){
		p1 = new Monster(Table.width / 2  , 1, type1);
		p2 = new Monster(Table.width / 2 + 1, 1, type2);
	}
	
	void init(){
		p1.y = p2.y = 1;
		p1.x = Table.width / 2 ;
		p2.x = Table.width / 2 + 1;
		p1.color = (byte)(r.nextInt(Monster.colNo) + 1);
		p2.color = (byte)(r.nextInt(Monster.colNo) + 1);
	}
	
	void init(byte type1, byte type2){
		p1.y = p2.y = 1;
		p1.x = Table.width / 2 ;
		p2.x = Table.width / 2 + 1;
		p1.color = type1;
		p2.color = type2;
	}
}