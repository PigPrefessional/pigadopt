/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.image.ImageUtils;
import com.ai2020lab.pigadopted.R;

/**
 * Created by Justin Z on 2016/4/10.
 * 502953057@qq.com
 */
public class GSensitiveView extends ImageView {

	private final static String TAG = GSensitiveView.class.getSimpleName();
	private Bitmap image;
	private double rotation;
	private Paint paint;

	public GSensitiveView(Context context) {
		super(context);
//		init();
	}

	public GSensitiveView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		init();
	}

	public GSensitiveView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//		init();
	}

	private void init() {
		Drawable drawable = ResourcesUtils.getDrawable(R.mipmap.gradienter);
		image = ImageUtils.drawable2Bitmap(drawable);
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
////		int degrees = (int) (180 * rotation / Math.PI);
////		canvas.rotate(degrees, getWidth() / 2, getHeight() / 2);
//		LogUtils.i(TAG, "是否为空-->" + (image != null));
//		double w = image.getWidth();
//		double h = image.getHeight();
//
//		@SuppressLint("DrawAllocation") Rect rect = new Rect();
//		getDrawingRect(rect);
//
//		int degrees = (int) (180 * rotation / Math.PI);
//		canvas.rotate(degrees, rect.width() / 2, rect.height() / 2);
//		canvas.drawBitmap(image, //
//				(float) ((rect.width() - w) / 2),//
//				(float) ((rect.height() - h) / 2),//
//				paint);
		canvas.save();
		int degrees = (int) (180 * rotation / Math.PI);
		// 围绕中心点旋转
		canvas.rotate(degrees, this.getWidth() / 2f, this.getHeight() / 2f);
		super.onDraw(canvas);
		canvas.restore();

	}

	public void setRotation(double rad) {
		rotation = rad;
		invalidate();
	}
}
