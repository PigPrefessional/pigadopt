/*
 * Copyright 2015 Justin Z
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ai2020lab.aiutils.system;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import com.ai2020lab.aiutils.common.LogUtils;

/**
 * 屏幕显示工具类<br>
 * 提供获取屏幕分辨率，dp,px,sp之间相互转换,屏幕截图等功能
 * Created by Justin on 2015/7/14.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class DisplayUtils {

	private final static String TAG = DisplayUtils.class.getSimpleName();

	/**
	 * 获取屏幕密度
	 *
	 * @param context 上下文引用
	 * @return 返回设备屏幕的密度
	 */
	public static float getDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}

	/**
	 * 获取屏幕垂直方向的像素点个数
	 *
	 * @param context 上下文引用
	 * @return 返回屏幕垂直方向的像素点个数，即分辨率的高
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * 获取屏幕水平方向的像素点个数
	 *
	 * @param context 上下文引用
	 * @return 返回屏幕水平方向的像素点个数，即分辨率的宽
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * dp转换成px,适用于sp转换px
	 *
	 * @param context 上下文引用
	 * @param dp      单位为dp的数值
	 * @return 转换成px成功返回不为-1的浮点数，否则返回-1f
	 */
	public static float dpToPx(Context context, float dp) {
		if (context == null) {
			LogUtils.i(TAG, "dpToPx:上下文引用不能为空，dp转换成px失败");
			return -1f;
		}
		return dp * getDensity(context);
	}

	/**
	 * dp转换成px,适用于sp转换px
	 *
	 * @param context 上下文引用
	 * @param dp      单位为dp的数值
	 * @return 转换成px成功返回不为-1的整数，否则返回-1
	 */
	public static int dpToPxInt(Context context, float dp) {
		return (int) (dpToPx(context, dp) + 0.5f);
	}

	/**
	 * px转换成dp,适用于px转换sp
	 *
	 * @param context 上下文引用
	 * @param px      单位为px的数值
	 * @return 转换成dp成功返回不为-1的浮点数，否则返回-1f
	 */
	public static float pxToDp(Context context, float px) {
		if (context == null) {
			LogUtils.i(TAG, "pxToDp:上下文引用不能为空，px转换成dp失败");
			return -1f;
		}
		return px / getDensity(context);
	}

	/**
	 * px转换成dp,适用于px转换sp
	 *
	 * @param context 上下文引用
	 * @param px      单位为px的数值
	 * @return 转换成dp成功返回不为-1的整数，否则返回-1
	 */
	public static int pxToDpInt(Context context, float px) {
		return (int) (pxToDp(context, px) + 0.5f);
	}

	/**
	 * 得到设备屏幕大小，单位为英寸<br>
	 * 实际计算的是设备屏幕对角线长度
	 *
	 * @param context 上下文引用
	 * @return 返回设备屏幕大小，单位为英寸
	 */
	public static double getScreenSize(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2)
				+ Math.pow(dm.heightPixels, 2));
		return diagonalPixels / (dm.density * 160);
	}

	/**
	 * 获取指定Activity的界面去掉状态栏的截图Bitmap对象
	 *
	 * @param activity Activity的引用
	 * @param isHasStatusBar true-截屏包括状态栏，false-截屏不包括状态栏
	 * @return 返回指定的Activity的屏幕截图的Bitmap对象，失败则返回null
	 */
	public static Bitmap getScreenshot(Activity activity, boolean isHasStatusBar) {
		if(activity == null){
			LogUtils.i(TAG, "getScreenshot:Activity对象为空,获取当前页面的截图失败");
			return null;
		}
		int screenW = getScreenWidth(activity);
		int screenH = getScreenHeight(activity);
		// 需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		// 获取状态栏高度
		Rect frame = new Rect();
		// 获取屏幕显示区域，包括标题栏,如果标题栏隐藏，则只有Activity窗口显示
		view.getWindowVisibleDisplayFrame(frame);
		// Activity窗口的y坐标，就是状态栏的高度
		int statusBarHeight = frame.top;
		// 去掉手机状态栏截图,参数含义：x开始坐标，y开始坐标，截图宽度，截图高度
		Bitmap b = null;
		if(!isHasStatusBar) {
			b = Bitmap.createBitmap(bitmap, 0, statusBarHeight, screenW,
					screenH - statusBarHeight);
		}
		else {
			b = bitmap;
		}
		view.destroyDrawingCache();
		return b;
	}

	/**
	 * 获取指定View的截图
	 *
	 * @param view 需要截图的View对象
	 * @return 返回指定View截图的Bitmap对象，失败则返回null
	 */
	public static Bitmap getViewShot(View view) {
		if(view == null){
			LogUtils.i(TAG, "getViewShot:View对象为空,获取view的截图失败");
			return null;
		}
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		view.destroyDrawingCache();
		return bitmap;
	}


}
