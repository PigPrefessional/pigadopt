/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aiviews.textview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiviews.R;

/**
 * 自定义描边的TextView<p>
 * 实现原理为两个TextView叠加，只有描边的TextView为底，实体TextView叠加在上面<br>
 * 看上去文字就有个不同颜色的边框了
 * Created by Justin Z on 2016/3/17.
 * 502953057@qq.com
 */
public class StrokeTextView extends TextView {
	private final static String TAG = StrokeTextView.class.getSimpleName();
	/**
	 * 用于描边的TextView
	 */
	private TextView borderText = null;
	/**
	 * 默认使用白色描边
	 */
	private int borderColor = R.color.aiview_text_border;

	public StrokeTextView(Context context) {
		super(context);
		borderText = new TextView(context);
		init();
	}

	public StrokeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		borderText = new TextView(context, attrs);
		init();
	}

	public StrokeTextView(Context context, AttributeSet attrs,
	                      int defStyle) {
		super(context, attrs, defStyle);
		borderText = new TextView(context, attrs, defStyle);
		init();
	}

	@TargetApi(Build.VERSION_CODES.M)
	public void init() {
		TextPaint tp1 = borderText.getPaint();
		//设置描边宽度
		tp1.setStrokeWidth(4);
		//对文字只描边
		tp1.setStyle(Paint.Style.STROKE);
		borderText.setGravity(getGravity());
//		borderText.setTextAppearance(R.style.TextAppearance_FontPath);
	}

	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params) {
		super.setLayoutParams(params);
		borderText.setLayoutParams(params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		CharSequence tt = borderText.getText();
		//两个TextView上的文字必须一致
		if (tt == null || !tt.equals(this.getText())) {
			LogUtils.i(TAG, "将描边的文字设置为一样");
			borderText.setText(getText());
			this.postInvalidate();
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		borderText.measure(widthMeasureSpec, heightMeasureSpec);
	}

	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		borderText.layout(left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		LogUtils.i(TAG, "-onDraw-");
		borderText.setTextColor(ResourcesUtils.getColor(borderColor));  //设置描边颜色
		borderText.draw(canvas);
		super.onDraw(canvas);
	}

	/**
	 * 设置描边的颜色
	 *
	 * @param colorResID 描边的颜色
	 */
	public void setBorderColor(int colorResID) {
		LogUtils.i(TAG, "设置边框颜色");
		this.borderColor = colorResID;
	}


}
