package com.example.android.MonsterPop;

import com.example.android.MonsterBluetooth.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class MonsterActivity extends Activity {

	AnimatedView anim;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.game);
		anim = (AnimatedView) findViewById(R.id.anim_view);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

	SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float xmove = e1.getX() - e2.getX(), ymove = e1.getY() - e2.getY();
			float sensi = 10, sensiPercent = 1.5f;
			synchronized (anim.move) {
				if (Math.abs(xmove) > sensi
						&& Math.abs(xmove) > Math.abs(ymove) * sensiPercent) {
					if (xmove > 0)
						anim.moveOneSend(AnimatedView.UP);
					else
						anim.moveOneSend(AnimatedView.DOWN);
				} else if (Math.abs(ymove) > sensi
						&& Math.abs(ymove) > Math.abs(xmove) * sensiPercent) {
					if (ymove > 0)
						anim.moveOneSend(AnimatedView.LEFT);
					else
						anim.moveOneSend(AnimatedView.RIGHT);
				}
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	};

	GestureDetector gestureDetector = new GestureDetector(
			simpleOnGestureListener);
}