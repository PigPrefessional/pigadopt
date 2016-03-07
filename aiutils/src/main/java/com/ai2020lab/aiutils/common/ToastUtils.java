package com.ai2020lab.aiutils.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类，用于弹出全局唯一的Toast
 * Created by Justin on 2015/7/17.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ToastUtils {

	private final static String TAG = ToastUtils.class.getSimpleName();
	private static ToastUtils INSTANCE = new ToastUtils();
	private Toast toast;

	private ToastUtils() {
	}

	public static ToastUtils getInstance() {
		return new ToastUtils();
	}

	/**
	 * 弹出指定文字的Toast
	 *
	 * @param context  上下文引用
	 * @param msg      指定要显示的文字
	 * @param duration Toast持续显示时间，单位为毫秒，也可以是常量Toast.LENGTH_SHORT和Toast.LENGTH_LONG
	 */
	public void showToast(Context context, CharSequence msg, int duration) {
		if (msg == null || msg.equals("")) {
			LogUtils.i(TAG, "Toast的提示信息不能为空，弹出Toast失败");
		}
		if (toast == null) {
			toast = Toast.makeText(context, msg, duration);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

	/**
	 * 弹出指定文字的Toast
	 *
	 * @param context 上下文引用
	 * @param msg     指定要显示的文字
	 */
	public void showToast(Context context, CharSequence msg) {
		showToast(context, msg, Toast.LENGTH_SHORT);
	}

	/**
	 * 弹出指定文字的Toast
	 *
	 * @param context     上下文引用
	 * @param stringResID 指定要显示的文字资源ID
	 */
	public void showToast(Context context, int stringResID) {
		showToast(context, context.getString(stringResID), Toast.LENGTH_SHORT);
	}

	/**
	 * 隐藏Toast,特别注意在退出Activity的时候要关闭已经显示的Toast
	 */
	public void dismiss() {
		if (toast != null) toast.cancel();
	}


}
