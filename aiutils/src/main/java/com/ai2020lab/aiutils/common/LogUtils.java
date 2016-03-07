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
package com.ai2020lab.aiutils.common;

import android.util.Log;

/**
 * 日志工具类
 * Created by Justin on 2015/7/13.
 * Email:502953057@qq.com
 */
public final class LogUtils {

	private static boolean isDebug = true;

	/**
	 * 返回当前日志状态
	 *
	 * @return true-打印日志，false-不打印日志
	 */
	public static boolean getDebugFlag() {
		return isDebug;
	}

	/**
	 * 调用这个方法打开日志或关闭日志
	 *
	 * @param debug true-打开日志，false-关闭日志
	 */
	public static void setDebugFlag(boolean debug) {
		isDebug = debug;
	}

	/**
	 * 对应Log.d
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void d(String tag, String msg) {
		if (isDebug) {
			Log.d(tag, msg);
		}
	}

	/**
	 * 对应Log.e
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void e(String tag, String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}

	/**
	 * 对应Log.e
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 * @param e   异常信息
	 */
	public static void e(String tag, String msg, Exception e) {
		if (isDebug) {
			Log.e(tag, msg, e);
		}
	}

	/**
	 * 对应Log.i
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void i(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	/**
	 * 对应Log.w
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void w(String tag, String msg) {
		if (isDebug) {
			Log.w(tag, msg);
		}
	}

	/**
	 * 对应Log.w
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 * @param e   异常信息
	 */
	public static void w(String tag, String msg, Exception e) {
		if (isDebug) {
			Log.w(tag, msg, e);
		}
	}

	/**
	 * 对应Log.w
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 * @param tr  异常信息
	 */
	public static void w(String tag, String msg, Throwable tr) {
		if (isDebug) {
			Log.w(tag, msg, tr);
		}
	}

	/**
	 * 对应Log.w
	 *
	 * @param tag 日志标题
	 * @param e   异常信息
	 */
	public static void w(String tag, Exception e) {
		if (isDebug) {
			Log.w(tag, e);
		}
	}

	/**
	 * 对应Log.v
	 *
	 * @param tag 日志标题
	 * @param msg 日志内容
	 */
	public static void v(String tag, String msg) {
		if (isDebug) {
			Log.v(tag, msg);
		}
	}

}
