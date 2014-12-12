package com.example.android.MonsterPop;

import java.io.IOException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimatedView extends ImageView {
	private MonsterActivity mContext;
	float x = -1, x1 = -1;
	float y = -1, y1 = -1;
	private Handler h;
	private final int FRAME_RATE = 50;
	private final int nrPlayers = 2;
	final Table[] Deck;
	boolean requestRedraw = false;
	int step = 0;
	private int posOffset = 270;
	static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3, MOVE = 4, OVER = 5,
			MY_DECK = 0, OPP_DECK = 1;
	int zipNr = 6;
	Integer move = -1;
	Canvas c = null;
	boolean over = false;

	public AnimatedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = (MonsterActivity) context;
		h = new Handler();
		Deck = new Table[nrPlayers];
		//for (int i = 0; i < nrPlayers; i++)
		Deck[0] = new Table(this, false);
		Deck[1] = new Table(this, true);
		moveOpponent();
	}

	private Runnable r = null;

	
	void moveOne(int dir, int pos) {
		switch (dir) {
		case LEFT:
			Deck[pos].left();
			break;
		case UP:
			Deck[pos].up();
			break;
		case DOWN:
			Deck[pos].dropMonster();
			break;
		case RIGHT:
			Deck[pos].right();
			break;
		case MOVE:
			Deck[pos].moveMonster();
			break;
		case OVER:
			break;
		}
		if (Deck[pos].DeadCells > 0) {

		}
	}

	void moveOneSend(int dir) {
		if (Deck[MY_DECK].acceptsInput) {
			moveOne(dir, MY_DECK);
			int msg = dir + 6 * Deck[MY_DECK].nextCol1 + 36 * Deck[MY_DECK].nextCol2;
			try {
				MyApp.Out.write(msg);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void moveOpponent() {
		// opponent thread
		new Thread(new Runnable() {
			public void run() {
				int msg;
				while (!Deck[MY_DECK].Over &&
						!Deck[OPP_DECK].Over) {
					try {
						msg = MyApp.In.read();
						
						moveOne(msg%6, OPP_DECK);
						msg = msg / 6;
						Deck[OPP_DECK].nextCol1 = (byte) (msg % 6);
						msg = msg / 6;
						Deck[OPP_DECK].nextCol2 = (byte) (msg % 6);

						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		// my thread
		new Thread(new Runnable() {
			public void run() {
				while (!Deck[MY_DECK].Over &&
						!Deck[OPP_DECK].Over) {
				
						moveOneSend(MOVE);
					try {
						Thread.sleep(1200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				over = true;
			    
			}
		}).start();
		
		

		r = new Runnable() {
			@Override
			public void run() {
				invalidate();
			}
		};
		h.postDelayed(r, FRAME_RATE);

	}

	public void drawTable(Canvas c, int pos) {
		this.c = c;
		int offset = 30;
		int left_down = offset + posOffset * pos;
		int item_size = 25;
		int width = item_size * Table.width;
		int height = item_size * Table.height;
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.BLACK);
		c.drawRect(5, left_down, height, width + left_down, paint);
		for (int i = 0; i < Table.width; i++)
			for (int j = 0; j < Table.height; j++) {
				int color = Deck[pos].map[i + 1][j + 1];
				if (color > 0) {
					int resId = mContext.getResources().getIdentifier(
							"i" + color, "drawable",
							"com.example.android.MonsterBluetooth");
					BitmapDrawable item = (BitmapDrawable) mContext
							.getResources().getDrawable(resId);
					c.drawBitmap(item.getBitmap(), j * item_size + 1, left_down
							+ i * item_size, null);
				}
			}
	}
	
	private void drawScore(Canvas c){
		if (Deck[MY_DECK].Over){
			int resId = mContext.getResources().getIdentifier("lose", "drawable","com.example.android.MonsterBluetooth");
			BitmapDrawable item = (BitmapDrawable) mContext.getResources().getDrawable(resId);
			c.drawBitmap(item.getBitmap(), 40, 80 , null);
		}
		else{
			int resId = mContext.getResources().getIdentifier("win", "drawable","com.example.android.MonsterBluetooth");
			BitmapDrawable item = (BitmapDrawable) mContext.getResources().getDrawable(resId);
			c.drawBitmap(item.getBitmap(), 40, 80 , null);
		}
	}

	protected void onDraw(Canvas c) {

		if (!over) {
			drawTable(c, 0);
			drawTable(c, 1);
		}
		else{
			drawScore(c);
		}
		
		if (r != null && !over) {
			h.postDelayed(r, FRAME_RATE);	
		}
		
	}
}