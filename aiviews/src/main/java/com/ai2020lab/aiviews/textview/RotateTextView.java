/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aiviews.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 可以让字旋转角度显示的TextView
 * Created by Justin Z on 2016/3/17.
 * 502953057@qq.com
 */
public class RotateTextView extends TextView {

	private final static String TAG = RotateTextView.class.getSimpleName();

	private final static int DEFAULT_DEGREES = -30;

	private int mDegrees = DEFAULT_DEGREES;

	public RotateTextView(Context context) {
		super(context);
	}

	public RotateTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RotateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	private void init(Context context, AttributeSet attrs) {
		setGravity(Gravity.CENTER);
//		TypedArray a = context.obtainStyledAttributes(attrs,
//				R.styleable.RotateTextView);
//		mDegrees = a.getDimensionPixelSize(R.styleable.RotateTextView_degree,
//				DEFAULT_DEGREES);
//		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
		// 围绕中心点旋转
		canvas.rotate(mDegrees, this.getWidth() / 2f, this.getHeight() / 2f);
		super.onDraw(canvas);
		canvas.restore();
	}

	/**
	 * 设置旋转角度
	 *
	 * @param degrees 旋转角度
	 */
	public void setDegrees(int degrees) {
		mDegrees = degrees;
	}


}
