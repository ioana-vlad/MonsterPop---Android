package com.example.android.MonsterPop;

import java.util.Random;
import java.util.Stack;
import java.util.Vector;

public class Table {

	static final int height = 12, width = 6;
	byte[][] map;
	TwoItems p;
	boolean Over = false, KeyboardControlled, isInit = false;
	byte DeadLines = 0, DeadCells = 0, BreakableType = 0, DeadCellPos = 1;
	byte ReceivedLines = 0;
	Vector Breakable;
	Stack Numbered;
	byte nextCol1, nextCol2;
	int ReceivedCells = 0;
	boolean artificial = false;
	AnimatedView parent;
	boolean acceptsInput = true;

	public Table(AnimatedView parent, boolean keyControlled) {
		int i;
		KeyboardControlled = keyControlled;
		map = new byte[width + 2][height + 2];
		for (i = 0; i <= width; i++) {
			map[i][0] = -1;
			map[i][height + 1] = -1;
		}
		for (i = 0; i <= height; i++) {
			map[0][i] = -1;
			map[width + 1][i] = -1;
		}
		p = new TwoItems((byte)1,(byte)2);
		Random r = new Random();
		nextCol1 = (byte) (r.nextInt(Monster.colNo) + 1);
		nextCol2 = (byte) (r.nextInt(Monster.colNo) + 1);
		map[p.p1.x][p.p1.y] = p.p1.color;
		map[p.p2.x][p.p2.y] = p.p2.color;
		Breakable = new Vector();
		Numbered = new Stack();
		this.parent = parent;
	}

	public void setMonster(int x, int y, byte type) {
		map[x][y] = type;
	}

	public Monster getMonster(int x, int y) {
		return new Monster(x, y, map[x][y]);
	}

	private void clearMonster() {
		map[p.p1.x][p.p1.y] = map[p.p2.x][p.p2.y] = 0;
	}

	private void resetMonster() {
		map[p.p1.x][p.p1.y] = p.p1.color;
		map[p.p2.x][p.p2.y] = p.p2.color;
	}

	public void init() {
		isInit = true;
		Random r = new Random();
		if (map[width / 2][1] != 0 || map[width / 2 + 1][1] != 0) {
			Over = true;
			System.out.print("Game Over");
		} else {
			p.init(nextCol1, nextCol2);
			if (!KeyboardControlled) {
				nextCol1 = (byte) (r.nextInt(Monster.colNo) + 1);
				nextCol2 = (byte) (r.nextInt(Monster.colNo) + 1);
			}
		}
	}

	public void left() {
		if ((p.p1.x == p.p2.x && map[p.p1.x - 1][p.p1.y] == 0 && map[p.p2.x - 1][p.p2.y] == 0)
				|| (p.p1.y == p.p2.y && map[(p.p1.x < p.p2.x ? p.p1.x : p.p2.x) - 1][p.p1.y] == 0)) {
			// curat tabela
			clearMonster();
			// modific coordonatele
			p.p1.x -= 1;
			p.p2.x -= 1;
			// realoc pe tabela
			resetMonster();
		} else { // !! SOUND
		}
	}

	public void right() {
		if ((p.p1.x == p.p2.x && map[p.p1.x + 1][p.p1.y] == 0 && map[p.p2.x + 1][p.p2.y] == 0)
				|| (p.p1.y == p.p2.y && map[(p.p1.x > p.p2.x ? p.p1.x : p.p2.x) + 1][p.p1.y] == 0)) {
			// curat tabela
			clearMonster();
			// modific coordonatele
			p.p1.x += 1;
			p.p2.x += 1;
			// realoc pe tabela
			resetMonster();
		}
	}

	public void up() {
		if (p.p1.y > 1) {
			clearMonster();
			if (p.p1.y == p.p2.y) {
				if (p.p1.x < p.p2.x) {
					p.p2.y -= 1;
					p.p2.x -= 1;
				} else {
					p.p1.x -= 1;
					p.p1.y -= 1;
				}
			} else {

				if (p.p2.y < p.p1.y) {
					if (map[p.p1.x + 1][p.p1.y] == 0) {
						p.p2.x = p.p1.x;
						p.p2.y = p.p1.y;
						p.p1.x += 1;
					}
				} else if (map[p.p2.x + 1][p.p2.y] == 0) {
					p.p1.x = p.p2.x;
					p.p1.y = p.p2.y;
					p.p2.x += 1;
				}
			}
			resetMonster();
		}
	}

	public void moveMonster() {
		if (p.p1.x == p.p2.x) {
			if (map[p.p1.x][Math.max(p.p1.y, p.p2.y) + 1] != 0) {
				p.p1.color = 0;
				p.p2.color = 0;
				Breakable.addElement(p.p1);
				Breakable.addElement(p.p2);
				breakM();
				init();
			} // trebuiesc coborate
			else {
				// curat tabela
				clearMonster();
				// modific coordonatele
				p.p1.y += 1;
				p.p2.y += 1;
				// realoc pe tabela
				resetMonster();
			}
		} else {
			if (map[p.p1.x][p.p1.y + 1] == 0 && map[p.p2.x][p.p2.y + 1] == 0) {
				// curat tabela
				clearMonster();
				// modific coordonatele
				p.p1.y += 1;
				p.p2.y += 1;
				// realoc pe tabela
				resetMonster();
			} else {
				dropMonster();
			}
		}
	}

	public void dropMonster() {
		int max;
		clearMonster();
		// daca au acelasi x dar p2 este mai jos decat p1, atunci trebuie
		// aruncat prima data p2
		if (p.p1.x == p.p2.x && p.p1.y < p.p2.y) {
			for (max = height; map[p.p2.x][max] != 0; max--) {
				;
			}
			p.p2.y = max;
			map[p.p2.x][p.p2.y] = p.p2.color;

			for (max = height; map[p.p1.x][max] != 0; max--) {
				;
			}
			p.p1.y = max;
			map[p.p1.x][p.p1.y] = p.p1.color;
		} // altfel aruncam p1 prima data
		else {
			for (max = Table.height; map[p.p1.x][max] != 0; max--) {
				;
			}
			p.p1.y = max;
			map[p.p1.x][max] = p.p1.color;

			for (max = Table.height; map[p.p2.x][max] != 0; max--) {
				;
			}
			p.p2.y = max;
			map[p.p2.x][max] = p.p2.color;
		}
		p.p1.color = 0;
		p.p2.color = 0;

		Breakable.addElement(p.p1);
		Breakable.addElement(p.p2);
		parent.drawTable(parent.c, KeyboardControlled? 1 : 0);
		breakM();
		init();
	}

	public void dropLines(int noOfLines) {
		int i, max, line;
		for (line = 0; line < noOfLines; line++) {
			for (i = 1; i <= width; i++) {
				max = height;
				while (map[i][max] > 0)
					max--;
				if (max == 1)
					Over = true;
				map[i][max] = (byte) (Monster.colNo + 1);
			}
			if (Over)
				break;
		}
		ReceivedLines = 0;
	}

	public int number(int x, int y, int type) {
		if (map[x][y] == type) {
			Numbered.push(getMonster(x, y));
			map[x][y] = -2;
			return 1 + number(x - 1, y, type) + number(x + 1, y, type)
					+ number(x, y - 1, type) + number(x, y + 1, type);
		}
		return 0;
	}

	private void removeDead() {
		int i, size = Numbered.size();
		Monster Temp;
		for (i = 0; i < size; i++) {
			Temp = (Monster) Numbered.elementAt(i);
			if (map[Temp.x - 1][Temp.y] == Monster.colNo + 1) {
				map[Temp.x - 1][Temp.y] = -2;
				Numbered.push(getMonster(Temp.x - 1, Temp.y));
			}
			if (map[Temp.x + 1][Temp.y] == Monster.colNo + 1) {
				map[Temp.x + 1][Temp.y] = -2;
				Numbered.push(getMonster(Temp.x + 1, Temp.y));
			}
			if (map[Temp.x][Temp.y - 1] == Monster.colNo + 1) {
				map[Temp.x][Temp.y - 1] = -2;
				Numbered.push(getMonster(Temp.x, Temp.y - 1));
			}
			if (map[Temp.x][Temp.y + 1] == Monster.colNo + 1) {
				map[Temp.x][Temp.y + 1] = -2;
				Numbered.push(getMonster(Temp.x, Temp.y + 1));
			}
		}
	}

	private void removeZeros() {
		int j, tempy, step;
		Monster Temp;
		while (!Numbered.isEmpty()) {
			Temp = (Monster) Numbered.pop();
			if (map[Temp.x][Temp.y] == -2) {
				tempy = height;
				while (map[Temp.x][tempy] > 0) {
					tempy--;
				}
				step = 1;
				while (tempy - step > 0) {
					if (map[Temp.x][tempy - step] > 0) {
						Breakable.addElement(new Monster(Temp.x, tempy, BreakableType));
						map[Temp.x][tempy] = map[Temp.x][tempy - step];
						tempy--;
					} else {
						step++;
					}
				}
				for (j = 1; j <= step; j++) {
					map[Temp.x][j] = 0;
				}
			}
		}
	}

	public void dropCells() {
		for (int i = 0; i < ReceivedCells; i++) {
			int max = height;
			while (map[DeadCellPos][max] > 0) {
				max--;
			}
			map[DeadCellPos][max] = (byte) (Monster.colNo + 1);
			DeadCellPos = (byte) (DeadCellPos % width + 1);
			ReceivedCells = 0;
		}
	}

	private void remove(int number) {
		byte type = ((Monster) Numbered.peek()).color;
		Monster temp;
		for (int i = 0; i < number; i++) {
			temp = (Monster) Numbered.pop();
			map[temp.x][temp.y] = type;
		}
	}

	// @return 0 daca nu a spart nimic sau 1 daca a spart ceva
	private int breakSameType(byte type) {
		int ToReturn = 0, temp;
		Monster Temp = (Monster) Breakable.firstElement();
		while (Temp.color == type) {
			if (map[Temp.x][Temp.y] > 0 && map[Temp.x][Temp.y] <= Monster.colNo) {
				if ((temp = number(Temp.x, Temp.y, map[Temp.x][Temp.y])) < 4) {
					remove(temp);
				} else {
					ToReturn = 1;
				}
			}
			Breakable.removeElementAt(0);
			if (Breakable.size() == 0) {
				break;
			} else {
				Temp = (Monster) Breakable.firstElement();
			}
		}
		return ToReturn;
	}

	public void breakM() {
		acceptsInput = false;
		dropLines(ReceivedLines);
		dropCells();
		BreakableType = 0;
		DeadLines = 0;
		DeadCells = 0;
		int br = 0;
		while (Breakable.size() > 0) {
			
			BreakableType++;
			br = breakSameType(((Monster) Breakable.firstElement()).color);
			DeadLines += br;
			DeadCells += Math.max(0, Numbered.size() - 4);

			removeDead();
			removeZeros();
			if (br > 0)
				try {
					parent.drawTable(parent.c, KeyboardControlled? 1 : 0);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
		}
		if (DeadLines > 0)
			DeadLines--;
		if (!KeyboardControlled) {
			parent.Deck[1].ReceivedLines += DeadLines;
			parent.Deck[1].ReceivedCells += DeadCells;
		} else {
			parent.Deck[0].ReceivedLines += DeadLines;
			parent.Deck[0].ReceivedCells += DeadCells;
		}
		acceptsInput = true;
	}
}