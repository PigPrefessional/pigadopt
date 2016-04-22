/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.pigadopted.R;

/**
 * 拖动缩放矩形框的自定义View，用于裁剪照片<p>
 * <p/>
 * Created by Justin Z on 2016/4/18.
 * 502953057@qq.com
 */
public class CropperView extends View implements View.OnTouchListener {

	private final static String TAG = CropperView.class.getSimpleName();

	/**
	 * 边缘触摸操作区域宽度
	 */
	private final static int TOUCH_EDGE_WIDTH = 20;
	/**
	 * 四个角的长度
	 */
	private final static int CORNER_EDGE_LENGTH = 15;
	/**
	 * 宽度偏移量的最小值
	 */
	private final static int OFFSET_WIDTH_MIN = 60;
	/**
	 * 宽度偏移量的最大值
	 */
	private final static int OFFSET_WIDTH_MAX = 140;
	/**
	 * 高度偏移量的最小值
	 */
	private final static int OFFSET_HEIGHT_MIN = 20;
	/**
	 * 高度偏移量的最大值
	 */
	private final static int OFFSET_HEIGHT_MAX = 180;

	/**
	 * View的触摸区域-左边
	 */
	private final static int LEFT = 0x0001;
	/**
	 * View的触摸区域-左上
	 */
	private final static int LEFT_TOP = 0x0006;
	/**
	 * View的触摸区域-右边
	 */
	private final static int RIGHT = 0x0002;
	/**
	 * View的触摸区域-右上
	 */
	private final static int RIGHT_TOP = 0x0007;
	/**
	 * View的触摸区域-顶边
	 */
	private final static int TOP = 0x0003;
	/**
	 * View的触摸区域-底边
	 */
	private final static int BOTTOM = 0x0004;
	/**
	 * View的触摸区域-中部
	 */
	private final static int CENTER = 0x0005;

	private Context context;

	private int widthMin;
	private int widthMax;
	private int heightMin;
	private int heightMax;

	/**
	 * 矩形框边的颜色
	 */
	private int strokeColor = R.color.cropper_view_rect_border;
	/**
	 * 矩形框边的宽度
	 */
	private float strokeWidth = 2f;
	/**
	 * 四个角的线条颜色
	 */
	private int cornerColor = Color.WHITE;
	/**
	 * 四个角的线条宽度
	 */
	private float cornerWidth = 4.0f;
	/**
	 * 画笔对象
	 */
	private Paint rectPaint;
	private Paint cornerPaint;
	/**
	 * 画布宽度，即整个View的宽度
	 */
	private int width;
	/**
	 * 画布高度，即整个View的高度
	 */
	private int height;

	/**
	 * 绘制矩形区域的宽度偏移量
	 */
	private int offsetWidth;
	/**
	 * 绘制矩形区域的高度偏移量
	 */
	private int offsetHeight;
	/**
	 * 触摸区域的宽度
	 */
	private int touchWidth;
	/**
	 * 四个角的长度
	 */
	private int cornerLength;
	/**
	 * 当前触摸的区域
	 */
	private int touchAreaFlag = -1;

	private int lastX;
	private int lastY;

	public CropperView(Context context) {
		super(context);
		init(context);
	}

	public CropperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CropperView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		touchWidth = DisplayUtils.dpToPxInt(context, TOUCH_EDGE_WIDTH);
		LogUtils.i(TAG, "触摸操作区域宽度 touchWidth-->" + touchWidth);
		cornerLength = DisplayUtils.dpToPxInt(context, CORNER_EDGE_LENGTH);
		LogUtils.i(TAG, "四个边角线条长度 cornerLength-->" + cornerLength);
		widthMin = DisplayUtils.dpToPxInt(context, OFFSET_WIDTH_MIN);
		widthMax = DisplayUtils.dpToPxInt(context, OFFSET_WIDTH_MAX);
		heightMin = DisplayUtils.dpToPxInt(context, OFFSET_HEIGHT_MIN);
		heightMax = DisplayUtils.dpToPxInt(context, OFFSET_HEIGHT_MAX);
		LogUtils.i(TAG, "widthMin-->" + widthMin);
		LogUtils.i(TAG, "widthMax-->" + widthMax);
		LogUtils.i(TAG, "heightMin-->" + heightMin);
		LogUtils.i(TAG, "heightMax-->" + heightMax);
		offsetWidth = widthMax;
		LogUtils.i(TAG, "宽度偏移量 offsetWidth-->" + offsetWidth);
		offsetHeight = heightMax;
		LogUtils.i(TAG, "高度偏移量 offsetHeight-->" + offsetHeight);
		initPaint();
		// 注册触摸监听
		setOnTouchListener(this);
	}

	private void initPaint() {
		rectPaint = new Paint();
		rectPaint.setColor(ResourcesUtils.getColor(strokeColor));
		rectPaint.setStyle(Paint.Style.STROKE);
		strokeWidth = DisplayUtils.dpToPx(context, strokeWidth);
		LogUtils.i(TAG, "strokeWidth-->" + strokeWidth);
		rectPaint.setStrokeWidth(strokeWidth);
		rectPaint.setAntiAlias(true);
		cornerPaint = new Paint();
		cornerPaint.setColor(cornerColor);
		cornerPaint.setStyle(Paint.Style.FILL);
		cornerWidth = DisplayUtils.dpToPx(context, cornerWidth);
		LogUtils.i(TAG, "cornerWidth-->" + cornerWidth);
		cornerPaint.setStrokeWidth(cornerWidth);
		cornerPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取整个画布的宽度和高度
		width = canvas.getWidth();
		height = canvas.getHeight();
		LogUtils.i(TAG, "width-->" + width);
		LogUtils.i(TAG, "height-->" + height);
		// 矩形必须在整个画布的中心绘制，并且底边位于画布的中心线上
		// 约束：最大宽度和最小宽度,最大高度和最小高度
		// width - offset * 2 <= 200 && width - offset * 2 >=100
		canvas.drawRect(offsetWidth, offsetHeight, width - offsetWidth, height / 2, rectPaint);
		///////////////////////////////////////////////////////////////////////////////////////
		float offset = cornerWidth / 2;
		// 画左上角的水平方向
		canvas.drawLine(offsetWidth - cornerWidth, offsetHeight - offset,
				offsetWidth + cornerLength, offsetHeight - offset, cornerPaint);
		// 画左上角的垂直方向
		canvas.drawLine(offsetWidth - offset, offsetHeight - cornerWidth,
				offsetWidth - offset, offsetHeight + cornerLength, cornerPaint);
		// 画左下角的水平方向
		canvas.drawLine(offsetWidth - cornerWidth, height / 2 + offset,
				offsetWidth + cornerLength, height / 2 + offset, cornerPaint);
		// 画左下角的垂直方向
		canvas.drawLine(offsetWidth - offset, height / 2 - cornerLength,
				offsetWidth - offset, height / 2 + cornerWidth, cornerPaint);
		// 画右上角的水平方向
		canvas.drawLine(width - offsetWidth - cornerLength, offsetHeight - offset,
				width - offsetWidth + cornerWidth, offsetHeight - offset, cornerPaint);
		// 画右上角的垂直方向
		canvas.drawLine(width - offsetWidth + offset, offsetHeight - cornerWidth,
				width - offsetWidth + offset, offsetHeight + cornerLength, cornerPaint);
		// 画右下角水平方向
		canvas.drawLine(width - offsetWidth - cornerLength, height / 2 + offset,
				width - offsetWidth + cornerWidth, height / 2 + offset, cornerPaint);
		// 画右下角垂直方向
		canvas.drawLine(width - offsetWidth + offset, height / 2 - cornerLength,
				width - offsetWidth + offset, height / 2 + cornerWidth, cornerPaint);
	}

	/**
	 * 是否处理拖动改变矩形框大小的标志位
	 */
	private boolean handleDragFlag = false;

	// onTouch比onTouchEvent先执行
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		LogUtils.i(TAG, "--触摸事件处理--");
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				LogUtils.i(TAG, "--触摸按下处理--");
				lastX = (int) event.getX();
				lastY = (int) event.getY();
				LogUtils.i(TAG, "按下的 lastX-->" + lastX);
				LogUtils.i(TAG, "按下的 lastY-->" + lastY);
				// 获取触摸的区域
				touchAreaFlag = getTouchAreaFlag(lastX, lastY);
				if (touchAreaFlag != -1) {
					LogUtils.i(TAG, "--触摸了操作区域--");
					handleDragFlag = true;
				}
				LogUtils.i(TAG, "ACTION_DOWN-->" + handleDragFlag);
				return handleDragFlag;
			case MotionEvent.ACTION_MOVE:
				LogUtils.i(TAG, "--触摸移动处理--");
				if (handleDragFlag) {
					// 移动的过程中实时获取x,y坐标位置
					int offsetX = (int) event.getX() - lastX;
					int offsetY = (int) event.getY() - lastY;
					LogUtils.i(TAG, "x方向移动 offsetX-->" + offsetX);
					LogUtils.i(TAG, "y方向移动 offsetY-->" + offsetY);
					// 处理拖动事件并重绘矩形区域
					handleDrag(offsetX, offsetY, touchAreaFlag);
					lastX = (int) event.getX();
					lastY = (int) event.getY();
				}
				LogUtils.i(TAG, "ACTION_MOVE-->" + handleDragFlag);
				return handleDragFlag;
			case MotionEvent.ACTION_UP:
				LogUtils.i(TAG, "--触摸弹起处理--");
				touchAreaFlag = -1;
				if (handleDragFlag) {
					handleDragFlag = false;
					return true;
				}
				return false;
		}
		LogUtils.i(TAG, "--事件处理完成返回--");
		return false;
	}

	/**
	 * 拖动处理,改变矩形框的大小，并通知重绘
	 * offsetX为正数表明矩形框宽度变大，offsetX为负数表明矩形框宽度变小
	 * offsetY为正数表明矩形框高度变小，offsetY为负数表明矩形框高度变大
	 */
	private void handleDrag(int offsetX, int offsetY, int touchAreaFlag) {
		switch (touchAreaFlag) {
			case LEFT:
				left(offsetX);
				break;
			case RIGHT:
				right(offsetX);
				break;
			case TOP:
				top(offsetY);
				break;
			case LEFT_TOP:
				leftTop(offsetX, offsetY);
				break;
			case RIGHT_TOP:
				rightTop(offsetX, offsetY);
				break;

		}
		invalidate();
	}

	/**
	 * 左边区域拖动<p>
	 *
	 * @param offsetX x方向的移动距离
	 */
	private void left(int offsetX) {
		offsetWidth += offsetX;
		setMaxMinWidth();
	}

	/**
	 * 右边区域拖动<p>
	 *
	 * @param offsetX x方向的移动距离
	 */
	private void right(int offsetX) {
		offsetWidth -= offsetX;
		setMaxMinWidth();
	}

	/**
	 * 顶部区域拖动<p>
	 *
	 * @param offsetY y方向移动距离
	 */
	private void top(int offsetY) {
		offsetHeight += offsetY;
		setMaxMinHeight();
	}

	private void leftTop(int offsetX, int offsetY) {
		offsetWidth += offsetX;
		offsetHeight += offsetY;
		setMaxMinHeight();
		setMaxMinWidth();
	}

	private void rightTop(int offsetX, int offsetY) {
		offsetWidth -= offsetX;
		offsetHeight += offsetY;
		setMaxMinHeight();
		setMaxMinWidth();
	}

	/**
	 * 矩形操作区域的宽度不能太小或者太大
	 */
	private void setMaxMinWidth() {
		if (offsetWidth >= widthMax) {
			offsetWidth = widthMax;
		} else if (offsetWidth <= widthMin) {
			offsetWidth = widthMin;
		}
	}

	/**
	 * 矩形操作区域的高度不能太小或者太大
	 */
	private void setMaxMinHeight() {
		if (offsetHeight >= heightMax) {
			offsetHeight = heightMax;
		} else if (offsetHeight <= heightMin) {
			offsetHeight = heightMin;
		}
	}

	/**
	 * 判断触摸区域
	 */
	private int getTouchAreaFlag(int x, int y) {
		if (x >= offsetWidth - touchWidth && x <= offsetWidth + touchWidth) {
			if (y > offsetHeight + touchWidth && y < height / 2 - touchWidth) {
				LogUtils.i(TAG, "--触摸了左边--");
				return LEFT;
			}
			if (y <= offsetHeight + touchWidth && y >= offsetHeight - touchWidth) {
				LogUtils.i(TAG, "--触摸了左边顶部--");
				return LEFT_TOP;
			}
		}
		if (x <= width - offsetWidth + touchWidth && x >= width - offsetWidth - touchWidth) {
			if (y > offsetHeight + touchWidth && y < height / 2 - touchWidth) {
				LogUtils.i(TAG, "--触摸了右边--");
				return RIGHT;
			}
			if (y <= offsetHeight + touchWidth && y >= offsetHeight - touchWidth) {
				LogUtils.i(TAG, "--触摸了右边顶部--");
				return RIGHT_TOP;
			}
		}
		if (y <= offsetHeight + touchWidth && y >= offsetHeight - touchWidth) {
			if (x > offsetWidth + touchWidth && x < width - offsetWidth - touchWidth) {
				LogUtils.i(TAG, "--触摸了顶边--");
				return TOP;
			}
		}
		return -1;
	}

	/**
	 * 裁剪框的边框颜色
	 *
	 * @param color 裁剪框边框颜色
	 */
	public void setStrokeColor(int color) {
		this.strokeColor = color;
		invalidate();
	}

	/**
	 * 设置裁剪框的边框宽度，单位是dp
	 *
	 * @param strokeWidth 裁剪框的边框宽度
	 */
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
		invalidate();
	}

	/**
	 * 返回取景框的矩形坐标
	 *
	 * @return Rect
	 */
	public Rect getCropperRect() {
		return new Rect(offsetWidth, offsetHeight, width - offsetWidth, height / 2);
	}


}
