/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aiviews.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.image.ImageUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiviews.R;

/**
 * 圆角和圆形ImageView
 * Created by Justin Z on 2016/3/28.
 * 502953057@qq.com
 */
public class RoundImageView extends ImageView {

	private final static String TAG = RoundImageView.class.getSimpleName();
	/**
	 * 图片类型，圆形
	 */
	private static final int TYPE_CIRCLE = 0;
	/**
	 * 图片类型，圆角
	 */
	private static final int TYPE_ROUND = 1;
	/**
	 * 圆角大小的默认值
	 */
	private static final int BORDER_RADIUS_DEFAULT = 10;
	private Context context;
	/**
	 * 图片的类型，圆形or圆角
	 */
	private Type type;
	/**
	 * 圆角的大小
	 */
	private int mBorderRadius;
	/**
	 * 绘图的Paint
	 */
	private Paint mBitmapPaint;
	/**
	 * 圆角的半径
	 */
	private int mRadius;
	/**
	 * 3x3 矩阵，主要用于缩小放大
	 */
	private Matrix mMatrix;
	/**
	 * 渲染图像，使用图像为绘制图形着色
	 */
	private BitmapShader mBitmapShader;
	/**
	 * view的宽度
	 */
	private int mWidth;
	/**
	 * 圆角模式下的矩形
	 */
	private RectF mRoundRect;
	/**
	 * 圆角模式下的边框矩形
	 */
	private RectF mBorderRoundRect;

	/**
	 * 构造方法
	 */
	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mMatrix = new Matrix();
		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
		// 从attrs中获取自定义属性
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.RoundImageView);
		// 默认为10dp
		mBorderRadius = a.getDimensionPixelSize(
				R.styleable.RoundImageView_borderRadius, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
								BORDER_RADIUS_DEFAULT, getResources()
										.getDisplayMetrics()));
		// 默认为Circle
		type = getType(a.getInt(R.styleable.RoundImageView_type, TYPE_CIRCLE));
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		LogUtils.i(TAG, "--onMeasure--");
		// 如果类型是圆形，则强制改变view的宽高一致，以小值为准
		if (type == Type.CIRCLE) {
			mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
			mRadius = mWidth / 2;
			setMeasuredDimension(mWidth, mWidth);
		}
	}

	/**
	 * 初始化BitmapShader
	 */
	private void setUpShader() {
		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}
		Bitmap bmp = ImageUtils.drawable2Bitmap(drawable);
		// 将bmp作为着色器，就是在指定区域内绘制bmp
		// 以拉伸的方式初始化BitmapShader
		mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		float scale = 1.0f;
		// 圆形图片
		if (type == Type.CIRCLE) {
			// 拿到bitmap宽或高的小值
			int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
			scale = mWidth * 1.0f / bSize;

		}
		// 圆角图片
		else if (type == Type.ROUND) {
			// 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；
			// 缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
			// 类似于centerCrop
			scale = Math.max(getWidth() * 1.0f / bmp.getWidth(), getHeight()
					* 1.0f / bmp.getHeight());
		}
		// shader的变换矩阵，我们这里主要用于放大或者缩小
		mMatrix.setScale(scale, scale);
		// 设置变换矩阵
		mBitmapShader.setLocalMatrix(mMatrix);
		// 设置shader
		mBitmapPaint.setShader(mBitmapShader);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		LogUtils.i(TAG, "--onDraw--");
		if (getDrawable() == null) {
			return;
		}
		setUpShader();

		if (type == Type.ROUND) {
			canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius,
					mBitmapPaint);
		} else if (type == Type.CIRCLE) {
			canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
		}
	}

	/**
	 * 尺寸改变的时候回调
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		super.onSizeChanged(w, h, oldW, oldH);
		LogUtils.i(TAG, "--onSizeChanged--");
		// 初始化圆角图片的范围矩形
		if (type == Type.ROUND) {
			mRoundRect = new RectF(0, 0, getWidth(), getHeight());
			mBorderRoundRect = new RectF(0, 0, getWidth(), getHeight());
		}
	}

	/**
	 * 设置圆角角度
	 *
	 * @param borderRadius 圆角角度
	 */
	public void setBorderRadius(int borderRadius) {
		if (type != Type.ROUND) {
			LogUtils.i(TAG, "只有在圆角模式下才能设置圆角角度");
			return;
		}
		int pxVal = DisplayUtils.dpToPxInt(context, borderRadius);
		if (this.mBorderRadius != pxVal) {
			this.mBorderRadius = pxVal;
			invalidate();
		}
	}

	/**
	 * 改变显示方式，圆角或圆形
	 *
	 * @param type Type枚举类型
	 */
	public void setType(Type type) {
		if (this.type != type) {
			this.type = type;
			// 请求重新布局,会调用onMeasure方法
			requestLayout();
		}
	}

	/**
	 * 获取Type枚举值
	 */
	private Type getType(int type) {
		switch (type) {
			case TYPE_CIRCLE:
				return Type.CIRCLE;
			case TYPE_ROUND:
				return Type.ROUND;
		}
		return Type.CIRCLE;
	}

	/**
	 * ImageView类型
	 */
	public enum Type {
		// 圆角
		ROUND,
		// 圆形
		CIRCLE
	}


}
