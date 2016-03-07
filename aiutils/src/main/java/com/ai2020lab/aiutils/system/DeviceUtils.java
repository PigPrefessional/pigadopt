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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.ai2020lab.aiutils.common.LogUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

/**
 * 获取设备相关信息工具类
 * Created by Justin on 2015/7/14.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class DeviceUtils {

	/**
	 * 设备类型-手机
	 */
	public static final int DEVICE_TYPE_PHONE = 0;
	/**
	 * 设备类型-平板
	 */
	public static final int DEVICE_TYPE_PAD = 1;
	private static final String TAG = DeviceUtils.class.getSimpleName();

	/**
	 * 获取设备固件版本
	 *
	 * @return 返回设备固件版本
	 */
	public static String getFirmwareVersion() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取向用户展示的操作系统版本，比如：MIUI 5.0
	 *
	 * @return 返回向用户展示的操作系统版本
	 */
	public static String getSysVersion() {
		return Build.DISPLAY;
	}

	/**
	 * 获取设备型号
	 *
	 * @return 返回手机型号
	 */
	public static String getModel() {
		return Build.MODEL;
	}

	/**
	 * 获取制造商信息
	 *
	 * @return 返回制造商信息
	 */
	public static String getManufacturer() {
		return Build.MANUFACTURER;
	}

	/**
	 * 获取CPU名字
	 *
	 * @return 返回CPU的型号名
	 */
	public static String getCpuName() {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader("/proc/cpuinfo");
			br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+", 2);
			if (array.length > 1) {
				return array[1];
			}
		} catch (FileNotFoundException e) {
			LogUtils.e(TAG, "FileNotFoundException", e);
		} catch (IOException e) {
			LogUtils.e(TAG, "IOException", e);
		} finally {
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e) {
					LogUtils.e(TAG, "IOException", e);
				}
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					LogUtils.e(TAG, "IOException", e);
				}
		}
		return null;
	}

	/**
	 * 获取设备类型
	 *
	 * @param context 上下文引用
	 * @return 返回0表示电话，返回1表示平板
	 */
	public static int getDeviceType(Context context) {
		if ((context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
			return DEVICE_TYPE_PAD;
		} else {
			return DEVICE_TYPE_PHONE;
		}
	}

	/**
	 * 获取设备IMEI<br>
	 * IMEI:国际移动设备标识，设备的唯一编码
	 *
	 * @return 返回设备IMEI串号，获取失败返回null
	 */
	public static String getIMEI(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "getIMEI:上下文引用context不能为空，获取设备IMEI失败");
			return null;
		}
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm != null) {
				return tm.getDeviceId();
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "getIMEI:获取设备IMEI异常", e);
		}
		return null;
	}

	/**
	 * 获取SIM卡的ICCID<br>
	 * ICCID:integrate circuit card indentity 集成电路卡识别码，固话在手机的SIM卡中，IC卡唯一识别码
	 * 共20位数字组成
	 *
	 * @param context 上下文引用
	 * @return 返回SIM卡的ICCID，获取失败返回null
	 */
	public static String getICCID(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "getICCID:上下文引用context不能为空，获取ICCID失败");
			return null;
		}
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm != null) {
				return tm.getSimSerialNumber();
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "getICCID:获取ICCID异常", e);
		}
		return null;
	}

	/**
	 * 获取SIM卡的IMSI<br>
	 * IMSI:International Mobile Subscriber Identification Number
	 * 国际移动用户识别码，区别移动用户的标志，存储在SIM卡中，总长度不超过15位
	 *
	 * @param context 上下文引用
	 * @return 返回SIM卡的IMSI，获取失败返回null
	 */
	public static String getIMSI(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "getIMSI:上下文引用context不能为空，获取IMSI失败");
			return null;
		}
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm != null) {
				return tm.getSubscriberId();
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "getIMSI:获取IMSI异常", e);
		}
		return null;
	}

	/**
	 * 获取UUID
	 *
	 * @return 返回随机UUID
	 */
	public static String getDeviceToken() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 检查设备是否支持键盘
	 * <p/>
	 * 这个方法暂时无效
	 *
	 * @return 返回true表示支持，返回false表示不支持
	 */
	public static boolean isSupportKeyboard() {
		return KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_0);
	}

	/**
	 * 是否支持蓝牙设备
	 * <p/>
	 * 判断的是设备是否有蓝牙模块
	 *
	 * @param context 上下文引用
	 * @return 返回true-支持，false-不支持
	 */
	public static boolean isSupportedBluetooth(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "isSupportedBluetooth:上下文引用context不能为空，判断蓝牙模块失败");
			return false;
		}
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH);
	}

	/**
	 * 检查设备是否支持WIFI
	 * <p/>
	 * 判断的是设备是否有WIFI模块
	 *
	 * @param context 上下文引用
	 * @return 返回true-支持，false-不支持
	 */
	public static boolean isSupportedWIFI(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "isSupportedWIFI:上下文引用context不能为空，判断WIFI模块失败");
			return false;
		}
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_WIFI);
	}

	/**
	 * 检查设备是否有可用的摄像头
	 *
	 * @param context 上下文引用
	 * @return 返回true-有可用的摄像头，false-没有可用的摄像头
	 */
	public static boolean isSupportedCamera(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "isSupportedCamera:上下文引用context不能为空，判断Camera模块失败");
			return false;
		}
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA);
	}

	/**
	 * 检查设备是否支持GPS定位
	 * <p/>
	 * 判断的是设备是否有GPS模块
	 *
	 * @param context 上下文引用
	 * @return 返回true-支持GPS定位，false-不支持GPS定位
	 */
	public static boolean isSupportedGPS(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "isSupportedGPS:上下文引用context不能为空，判断GPS模块失败");
			return false;
		}
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_LOCATION_GPS);
	}

	/**
	 * 检查设备是否支持触屏
	 *
	 * @param context 上下文引用
	 * @return 返回true-支持触屏，false-不支持触屏
	 */
	public static boolean isSupportedTouchScreen(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "isSupportedTouchScreen:上下文引用context不能为空，判断触屏失败");
			return false;
		}
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TOUCHSCREEN);
	}

	/**
	 * 设备震动
	 *
	 * @param context  上下文引用
	 * @param duration 震动时间，单位为毫秒
	 */
	public static void vibrate(Context context, long duration) {
		if (context == null) {
			LogUtils.i(TAG, "vibrate:上下文引用context不能为空");
			return;
		}
		if(duration <= 0){
			LogUtils.i(TAG, "vibrate:duration为0");
			return;
		}
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(duration);
	}

	/**
	 * 获取剪切板内容
	 *
	 * @param context 上下文引用
	 * @return 返回剪切板内容，失败则返回null
	 */
	public static String getClipboardContent(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "getClipboardContent:上下文引用context不能为空，获取剪贴板内容失败");
			return null;
		}
		ClipData clipData = null;
		try {
			ClipboardManager cbm = (ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			if (cbm != null) {
				clipData = cbm.getPrimaryClip();
				ClipData.Item item = clipData.getItemAt(0);
				LogUtils.i(TAG, "剪切板内容-->" + item.coerceToText(context).toString());
				return item.coerceToText(context).toString();
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "getClipboardContent:获取ClipboardManager对象失败");
		}
		return null;
	}

	/**
	 * 清空剪切板数据
	 *
	 * @param context 上下文引用
	 */
	public static void clearClipboardContent(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "getClipboardContent:上下文引用context不能为空，获取剪贴板内容失败");
			return;
		}
		ClipData clipData = null;
		try {
			ClipboardManager cbm = (ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			if (cbm != null) {
				clipData = ClipData.newPlainText("", "");
				cbm.setPrimaryClip(clipData);
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "删除剪切板数据失败");
		}
	}

}