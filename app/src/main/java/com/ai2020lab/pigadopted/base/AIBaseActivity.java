/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.base;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiutils.system.NetworkUtils;
import com.ai2020lab.aiviews.popupview.PromptView;
import com.ai2020lab.aiviews.toolbar.ToolbarActivity;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.fragment.LoadingDialog;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity基类，继承自ToolbarActivity，添加网络连接监听，并且可以弹出进度条对话框
 * Created by Justin on 2015/12/10.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class AIBaseActivity extends ToolbarActivity {

	private final static String TAG = AIBaseActivity.class.getSimpleName();

	/**
	 * 加载对话框TAG
	 */
	private final static String TAG_DIALOG_LOADING = "tag_dialog_ai_loading";

	private LoadingDialog loadingDialog;

	private PromptView checkNetPromptView;

	/**
	 * 监听网络连接状态变化广播接收器
	 */
	private BroadcastReceiver netChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 网络状态改变
			onNetworkChange(NetworkUtils.getNetworkType(getActivity()));
		}
	};

	/**
	 * 程序入口
	 *
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}


	// 初始化数据和界面布局
	private void init() {
		Handler handler = new Handler();
		// 延迟500毫秒注册网络状态监听广播，否则弹出popupWindow会出错
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				registerNetReceiver(getActivity());
			}
		}, 500);
	}

	/**
	 * 显示加载对话框
	 *
	 * @param content          加载提示
	 */
	public void showLoading(String content) {
		loadingDialog = LoadingDialog.newInstance(content);
		Fragment fragment = getFragmentManager().findFragmentByTag(TAG_DIALOG_LOADING);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragment != null)
			ft.remove(fragment);
		ft.addToBackStack(null);// 加入回退栈
		loadingDialog.show(ft, TAG_DIALOG_LOADING);
	}

	/**
	 * 关闭进度条对话框
	 */
	public void dismissLoading() {
		LogUtils.i(TAG, "关闭进度条对话框");
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}

	/**
	 * 注册广播
	 */
	private void registerNetReceiver(Context context) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(netChangeReceiver, intentFilter);
	}

	/**
	 * 解除广播
	 */
	private void unRegisterNetReceiver(Context context) {
		if (netChangeReceiver == null) return;
		context.unregisterReceiver(netChangeReceiver);
	}

	/**
	 * 监听网络状态改变<p>
	 * <strong>注意：子类覆盖这个方法的时候必须调用super.onNetworkChange(),否则网络无法连接提示不会弹出</strong>
	 *
	 * @param netType NetworkUtils.NetworkType
	 */
	protected void onNetworkChange(NetworkUtils.NetworkType netType) {
		LogUtils.i(TAG, "当前网络状态->" + netType.getName());
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			LogUtils.i(TAG, "网络不可用");
			showNetPrompt();
		} else {
			LogUtils.i(TAG, "网络可用");
			dismissCheckNetPrompt();
		}
//		switch (netType) {
//			case UNKNOWN:
//			case DISCONNECTED:
//				showNetPrompt();
//				break;
//			case NET_2G:
//			case NET_3G:
//			case NET_4G:
//			case NET_WIFI:
//				dismissCheckNetPrompt();
//				break;
//		}
	}

	/**
	 * 显示网络出错提示
	 */
	private void showNetPrompt() {
		if (getToolbarVisibility()) {
			showCheckNetPrompt();
			return;
		}
		ToastUtils.getInstance().showToast(getActivity(), getString(R.string.prompt_check_network));
	}

	/**
	 * 显示网络出错提示
	 */
	private void showCheckNetPrompt() {
		View contentView = ViewUtils.makeView(getActivity(), R.layout.promptview_network_check);
		if (checkNetPromptView == null) {
			checkNetPromptView = new PromptView.Builder(contentView,
					DisplayUtils.getScreenWidth(getActivity()), 0)
					.setBackgroundDrawable(ResourcesUtils.getDrawable(R.drawable.popupview_bg))
					.create();
		}
		checkNetPromptView.showAsDropDown(getToolbar());
//		checkNetPromptView.showAtLocation();
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NetworkUtils.showWirelessSettings(getActivity());
			}
		});

	}

	/**
	 * 隐藏网络检查提示View
	 */
	private void dismissCheckNetPrompt() {
		if (checkNetPromptView != null && getToolbarVisibility()) {
			checkNetPromptView.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterNetReceiver(this);
	}


}
