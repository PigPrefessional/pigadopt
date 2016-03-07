package com.ai2020lab.pigadopted.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.system.NetworkUtils;

/**
 * 网络连接变化监听器
 * Created by Justin on 2016/1/14.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class NetWorkReceiver extends BroadcastReceiver {

	private final static String TAG = NetWorkReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		getNetWork(context);


	}

	private void getNetWork(Context context) {
		boolean network = NetworkUtils.isNetworkAvailable(context);
		boolean mobile = NetworkUtils.isMobileAvailable(context);
		boolean wifi = NetworkUtils.isWIFIAvailable(context);
		NetworkUtils.ISP isp = NetworkUtils.getISP(context);
		NetworkUtils.NetworkType type = NetworkUtils.getNetworkType(context);
		LogUtils.i(TAG, "网络连接是否可用->" + network);
		LogUtils.i(TAG, "移动网络连接是否可用->" + mobile);
		LogUtils.i(TAG, "wifi连接是否可用->" + wifi);
		LogUtils.i(TAG, "运营商名称->" + isp.getName());
		LogUtils.i(TAG, "当前网络类型->" + type.getName());
	}
}
