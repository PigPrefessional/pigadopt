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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ai2020lab.aiutils.common.LogUtils;

/**
 * 网络工具类<p>
 * 可判断当前网络是否可用，获取当前网络类型以及运营商名称
 * Created by Justin on 2015/7/14.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class NetworkUtils {

	/**
	 * Unknown network class.
	 */
	public static final int NETWORK_CLASS_UNKNOWN = -2;
	/**
	 * disconnect network class.
	 */
	public static final int NETWORK_CLASS_DISCONNECT = -1;
	/**
	 * Class of broadly defined "WIFI" networks.
	 */
	public static final int NETWORK_CLASS_WIFI = 0;
	/**
	 * Class of broadly defined "2G" networks.
	 */
	public static final int NETWORK_CLASS_2_G = 1;
	/**
	 * Class of broadly defined "3G" networks.
	 */
	public static final int NETWORK_CLASS_3_G = 2;
	/**
	 * Class of broadly defined "4G" networks.
	 */
	public static final int NETWORK_CLASS_4_G = 3;
	/**
	 * 未知运营商
	 */
	public static final int ISP_UNKNOWN = -1;
	/**
	 * 中国移动
	 */
	public static final int ISP_CMCC = 1;
	/**
	 * 中国联通
	 */
	public static final int ISP_CUCC = 2;
	/**
	 * 中国电信
	 */
	public static final int ISP_CTCC = 3;
	private final static String TAG = NetworkUtils.class.getSimpleName();

	/**
	 * 获取当前网络类型
	 *
	 * @param context 上下文引用
	 * @return -2为未知网络，-1为网络断开，0为WIFI，1为2G网络，2为3G网络，3为4G网络
	 */
	public static int getNetworkTypeInt(Context context) {
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manager == null) {
				LogUtils.i(TAG, "ConnectivityManager对象为空");
				return NETWORK_CLASS_DISCONNECT;
			}
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (info == null || !info.isAvailable()
					|| info.getState() != NetworkInfo.State.CONNECTED) {
				LogUtils.i(TAG, "NetworkInfo对象为空或连接不可用");
				return NETWORK_CLASS_DISCONNECT;
			}
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				switch (info.getSubtype()) {
					case TelephonyManager.NETWORK_TYPE_GPRS:
					case TelephonyManager.NETWORK_TYPE_EDGE:
					case TelephonyManager.NETWORK_TYPE_1xRTT:
					case TelephonyManager.NETWORK_TYPE_IDEN:
						return NETWORK_CLASS_2_G;
					case TelephonyManager.NETWORK_TYPE_CDMA:
					case TelephonyManager.NETWORK_TYPE_UMTS:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_HSDPA:
					case TelephonyManager.NETWORK_TYPE_HSUPA:
					case TelephonyManager.NETWORK_TYPE_HSPA:
						// case TelephonyManager.NETWORK_TYPE_EVDO_B:
						// case TelephonyManager.NETWORK_TYPE_EHRPD:
						// case TelephonyManager.NETWORK_TYPE_HSPAP:
						// 低版本兼容性，只能用数字来代替常量
					case 12:
					case 14:
					case 15:
						return NETWORK_CLASS_3_G;
					// case TelephonyManager.NETWORK_TYPE_LTE:
					case 13:
						return NETWORK_CLASS_4_G;
					default:
						return NETWORK_CLASS_UNKNOWN;
				}
			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				return NETWORK_CLASS_WIFI;
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "获取当前网络类型异常", e);
		}
		return NETWORK_CLASS_UNKNOWN;
	}

	/**
	 * 获取网络类型名
	 *
	 * @param context 上下文引用
	 * @return 返回网络类型名
	 */
	public static NetworkType getNetworkType(Context context) {
		int networkType = getNetworkTypeInt(context);
		switch (networkType) {
			case NETWORK_CLASS_UNKNOWN:
				return NetworkType.UNKNOWN;
			case NETWORK_CLASS_DISCONNECT:
				return NetworkType.DISCONNECTED;
			case NETWORK_CLASS_WIFI:
				return NetworkType.NET_WIFI;
			case NETWORK_CLASS_2_G:
				return NetworkType.NET_2G;
			case NETWORK_CLASS_3_G:
				return NetworkType.NET_3G;
			case NETWORK_CLASS_4_G:
				return NetworkType.NET_4G;
			default:
				return NetworkType.UNKNOWN;
		}
	}

	/**
	 * 判断WIFI网络是否可用
	 * @param context Context
	 * @return 返回true-可用，false-不可用
	 */
	@SuppressWarnings("deprecation")
	public static boolean isWIFIAvailable(Context context) {
		if(context == null){
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm == null){
			return false;
		}
		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return info != null && info.isAvailable();
	}

	/**
	 * 判断移动网络是否可用
	 *
	 * @param context 上下文引用
	 * @return 返回true-可用，false-不可用
	 */
	@SuppressWarnings("deprecation")
	public static boolean isMobileAvailable(Context context) {
		if(context == null){
            return false;
		}
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm == null){
			return false;
		}
		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return info != null && info.isAvailable();
	}

	/**
	 * 判断是否有网络连接
	 *
	 * @param context 上下文引用
	 * @return true-网络连接可用，false-网络连接不可用
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isAvailable();
	}

	/**
	 * 获取当前的移动网络运营商类型<br>
	 * 本方法根据IMSI获取，只支持国内的三大运营商
	 *
	 * @param context 上下文引用
	 * @return -1-获取失败，1-移动，2-联通，3-电信
	 */

	public static ISP getISP(Context context) {
		String IMSI = DeviceUtils.getIMSI(context);
		if (TextUtils.isEmpty(IMSI)) {
			LogUtils.i(TAG, "getISPType:获取IMSI失败，不能判断");
			return ISP.UNKNOWN;
		}
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			return ISP.CMCC;
		} else if (IMSI.startsWith("46001")) {
			return ISP.CUCC;
		} else if (IMSI.startsWith("46003")) {
			return ISP.CTCC;
		}
		return ISP.UNKNOWN;
	}

	/**
	 * 打开网络设置界面
	 * @param context 上下文引用
	 */
	public static void showWirelessSettings(Context context){
		context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
	}

	/**
	 * 三大运营商名枚举类
	 */
	public enum ISP {
		CMCC("China Mobile"),
		CUCC("China Unicom"),
		CTCC("China Telecom"),
		UNKNOWN("unknown");
		private String name;

		ISP(String name){
			this.name = name;
		}

		/**
		 * 获取运营商的名字
		 * @return 返回运营商的名字
		 */
		public String getName(){
			return name;
		}
	}

	/**
	 * 网络类型枚举类
	 */
	public enum NetworkType {
		NET_WIFI("wifi"),
		NET_2G("2g"),
		NET_3G("3g"),
		NET_4G("4g"),
		UNKNOWN("unknown"),
		DISCONNECTED("disconnected");
		private String name;

		NetworkType(String name){
			this.name = name;
		}

		/**
		 * 获取运营商的名字
		 * @return 返回运营商的名字
		 */
		public String getName(){
			return name;
		}


	}






}
