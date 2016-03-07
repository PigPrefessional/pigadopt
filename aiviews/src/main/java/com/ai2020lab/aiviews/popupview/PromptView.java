package com.ai2020lab.aiviews.popupview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.ai2020lab.aiutils.common.LogUtils;

/**
 * PopupWindow的封装
 * Created by Justin on 2016/1/8.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PromptView extends PopupWindow {

	private final static String TAG = PromptView.class.getSimpleName();


	private PromptView(View view, int width, int height) {
		super(view, width, height);
	}

	/**
	 * 建造器类
	 */
	public static class Builder {

		private View contentView;

		private int width;

		private int height;

		private Drawable backgroundDrawable;

		private int animationStyle = -1;

		private boolean focusable = false;

		/**
		 * 构造器
		 *
		 * @param contentView 内容View
		 * @param width       窗口宽度
		 * @param height      窗口高度
		 */
		public Builder(View contentView, int width, int height) {
			this.contentView = contentView;
			this.height = height;
			this.width = width;
		}

		/**
		 * 构造器
		 *
		 * @param context     上下文引用
		 * @param layoutResID 内容View的布局资源文件ID
		 * @param width       窗口宽度
		 * @param height      窗口高度
		 */
		public Builder(Context context, int layoutResID, int width, int height) {
			this.contentView = LayoutInflater.from(context).inflate(layoutResID, null);
			this.height = height;
			this.width = width;
		}

		/**
		 * 设置背景Drawable
		 *
		 * @param backgroudDrawable 背景Drawable
		 * @return Builder
		 */
		public Builder setBackgroundDrawable(Drawable backgroudDrawable) {
			this.backgroundDrawable = backgroudDrawable;
			return this;
		}

		/**
		 * 设置动画类型
		 *
		 * @param animationStyle 动画类型
		 * @return Builder
		 */
		public Builder setAnimationStyle(int animationStyle) {
			this.animationStyle = animationStyle;
			return this;
		}

		/**
		 * 设置是否可获得焦点，设置为true,点击popupWindow外部会取消popupWindow
		 *
		 * @param focusable true or false
		 * @return Builder
		 */
		public Builder setFocusable(boolean focusable) {
			this.focusable = focusable;
			return this;
		}

		public PromptView create() {
			if (contentView == null) {
				LogUtils.i(TAG, "contentView不能为空");
				return null;
			}
			if (width == 0) {
				width = ViewGroup.LayoutParams.WRAP_CONTENT;
			}
			if (height == 0) {
				height = ViewGroup.LayoutParams.WRAP_CONTENT;
			}
			PromptView promptView = new PromptView(contentView, width, height);
			if (backgroundDrawable != null)
				promptView.setBackgroundDrawable(backgroundDrawable);
			if (animationStyle != -1)
				promptView.setAnimationStyle(animationStyle);
			// 可以获取焦点,设置了true可能会影响Activity窗口焦点的变化
			promptView.setFocusable(focusable);
			return promptView;
		}


	}


}
