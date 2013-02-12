package com.genymobile.sommeil.customWidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LevelBarView extends View {
	float level = 0; // 0 to 1
	int color = Color.GRAY;
	Paint painter;
	RectF rect;
	float roundCornerRadius = 10.0f;
	float delta = 0;

	public LevelBarView(Context context) {
		super(context);
		init();
	}

	public LevelBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LevelBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		painter = new Paint();
		painter.setColor(color);
		painter.setStyle(Paint.Style.FILL);
		painter.setShadowLayer(42f, roundCornerRadius, delta, Color.CYAN);
		rect = new RectF(100, 0, 0, 0);
	}

	public void setLevel(int lev, int levMax) {
		level = ((float)lev / (float)levMax);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int h = getHeight();
		int w = getWidth();

		rect.set(0, 0, (int) (w * level), h - delta);
		canvas.drawRoundRect(rect, roundCornerRadius, roundCornerRadius, painter);
	}
}
