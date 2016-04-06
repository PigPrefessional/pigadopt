package com.ai2020lab.aiviews.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * 自定义Dialog
 * Created by Justin on 2016/1/20.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class BaseDialog extends Dialog {

	private BaseDialog(Context context) {
		super(context);
	}

	private BaseDialog(Context context, boolean cancelable,
	                   OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	private BaseDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * 内部建造器类
	 *
	 * @author Justin Z
	 */
	public final static class Builder {
		/**
		 * 上下文引用
		 */
		private Context context;
		/**
		 * 内容view
		 */
		private View contentView;
		/**
		 * 动画资源id
		 */
		private int anim;
		/**
		 * 主题资源id
		 */
		private int style;
		/**
		 * 对话框宽度
		 */
		private int dialogWidth = -3;
		/**
		 * 对话框高度
		 */
		private int dialogHeight = -3;
		/**
		 * 重力
		 */
		private int gravity = -1;
		/**
		 * 是否按back键可取消
		 */
		private boolean cancelable = true;
		/**
		 * 点击对话框外的屏幕部分是否取消对话框
		 */
		private boolean canceledOnTouchOutSide = false;
		/**
		 * 对话框取消事件监听器接口
		 */
		private OnCancelListener onCancelListener;


		/**
		 * 建造器构造方法
		 *
		 * @param context     上下文引用
		 * @param layoutResID 内容View的布局文件资源ID
		 */
		public Builder(Context context, int layoutResID) {
			this.context = context;
			this.contentView = LayoutInflater.from(context).inflate(layoutResID, null);
		}

		/**
		 * 建造器构造方法
		 *
		 * @param context     上下文引用
		 * @param contentView 内容View
		 */
		public Builder(Context context, View contentView) {
			this.context = context;
			this.contentView = contentView;
		}

		/**
		 * 设置对话框的宽度
		 *
		 * @param dialogWidth 对话框的宽度
		 * @return Builder
		 */
		public Builder setWidth(int dialogWidth) {
			this.dialogWidth = dialogWidth;
			return this;
		}

		/**
		 * 设置对话框的高度
		 *
		 * @param dialogHeight 对话框的高度
		 * @return Builder
		 */
		public Builder setHeight(int dialogHeight) {
			this.dialogHeight = dialogHeight;
			return this;
		}

		/**
		 * 设置窗口的Gravity
		 *
		 * @param gravity gravity
		 * @return Builder
		 */
		public Builder setGravity(int gravity) {
			this.gravity = gravity;
			return this;
		}

		/**
		 * 设置对话框是否可取消
		 *
		 * @param cancelable 是否可取消
		 * @return Builder
		 */
		public Builder setCancelable(boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}

		/**
		 * 设置对话框主题
		 *
		 * @param style 主题资源id
		 * @return Builder
		 */
		public Builder setStyle(int style) {
			this.style = style;
			return this;
		}

		/**
		 * 设置对话框动画
		 * @param anim 动画资源id
		 * @return Builder
		 */
		public Builder setAnimStyle(int anim){
			this.anim = anim;
			return this;
		}

		/**
		 * 设置点击对话框以外的区域是否关闭对话框
		 *
		 * @param canceledOnTouchOutSide true or false
		 * @return Builder
		 */
		public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutSide) {
			this.canceledOnTouchOutSide = canceledOnTouchOutSide;
			return this;
		}

		/**
		 * 设置取消事件监听
		 *
		 * @param onCancelListener OnCancelListener
		 * @return Builder
		 */
		public Builder setOnCancelListener(OnCancelListener onCancelListener) {
			this.onCancelListener = onCancelListener;
			return this;
		}

		/**
		 * 创建对话框对象方法
		 *
		 * @return 返回对话框对象
		 */
		public final BaseDialog create() {
			// 参数检查
			if (this.context == null) {
				throw new IllegalArgumentException("上下文引用不能为空");
			}
			BaseDialog dialog;
			if (style == -1)
				dialog = new BaseDialog(context);
			else
				dialog = new BaseDialog(context, style);
			// 设置内容View
			if (contentView != null) {
				dialog.setContentView(contentView);
			}
			// 设置对话框按下back键的可取消状态
			dialog.setCancelable(cancelable);
			// 设置点击对话框外部屏幕区域对话框是否消失
			dialog.setCanceledOnTouchOutside(canceledOnTouchOutSide);
			// 绑定取消事件监听事件
			if (onCancelListener != null) {
				dialog.setOnCancelListener(onCancelListener);
			}
			measureLayoutParams(dialog, dialogWidth, dialogHeight, gravity, anim);
			return dialog;
		}

		private void measureLayoutParams(Dialog dialog, int width, int height, int gravity, int anim) {
			Window dialogWindow = dialog.getWindow();
			if (gravity > 0)
				dialogWindow.setGravity(gravity);
			WindowManager.LayoutParams wlp = dialogWindow.getAttributes();
			if (width > -3)
				wlp.width = width;
			else
				wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
			if (height > -3)
				wlp.height = height;
			else
				wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			// 设置窗口动画
			if (anim != 0)
				dialogWindow.setWindowAnimations(anim);
			dialogWindow.setAttributes(wlp);
		}


	}
}
