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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ai2020lab.aiutils.system.NetworkUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;

/**
 * WebView工具类
 * Created by Justin on 2015/12/13.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class WebViewUtils {

	public final static String APP_CACHE_PATH = "webview";
	private final static String TAG = WebViewUtils.class.getSimpleName();

	/**
	 * 设置WebView的各个属性
	 *
	 * @param context       上下文引用
	 * @param webView       WebView对象的引用
	 * @param webViewClient WebViewClient对象的引用
	 */
	public static void setProperties(Context context, WebView webView, WebViewClient webViewClient) {
		initProperties(context, webView);
		webView.requestFocus(View.FOCUS_DOWN);
		webView.requestFocus(View.FOCUS_UP);
		webView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_UP:
						if (!v.hasFocus()) {
							v.requestFocus();
						}
						break;
				}
				return false;
			}
		});
		webView.setWebViewClient(webViewClient);
	}

	/**
	 * 设置WebView的各个属性
	 *
	 * @param context         上下文引用
	 * @param webView         WebView对象的引用
	 * @param webViewClient   WebViewClient对象的引用
	 * @param webChromeClient WebChromeClient对象的引用
	 */
	public static void setProperties(Context context, WebView webView, WebViewClient webViewClient,
	                                 WebChromeClient webChromeClient) {
		setProperties(context, webView, webViewClient);
		webView.setWebChromeClient(webChromeClient);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private static void initProperties(Context context, WebView webView) {
		WebSettings settings = webView.getSettings();
//		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		// 根据网络状态设置缓存策略
		if (NetworkUtils.isNetworkAvailable(context)) {
			settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			LogUtils.i(TAG, "没有网络的情况下，加载网页缓存");
			settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		// 设置编码方式
		settings.setDefaultTextEncodingName("UTF-8");
		settings.setJavaScriptEnabled(true);// 打开JS
		settings.setDatabaseEnabled(true); // 打开允许使用本地Database
		settings.setDomStorageEnabled(true);// 允许html5 localStorage
		settings.setAllowFileAccess(true);
		settings.setAppCacheEnabled(true); // 打开H5 Application Cache
		String appCachePath = context.getDir(APP_CACHE_PATH, Context.MODE_PRIVATE)
				.getPath();
		// 创建缓存目录
//		File cacheDir = FileUtils.getDiskCacheDir(context, APP_CACHE_PATH);
//		cacheDir.mkdirs();
		LogUtils.i(TAG, "WebView数据保存路径->" + appCachePath);
		settings.setAppCachePath(appCachePath);// 设置数据缓存目录
		// 设置WebView支持缩放
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);// 是否显示缩放按钮
		settings.setDisplayZoomControls(false);
		settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
//		settings.setBlockNetworkImage(true);// 设置图片最后加载
		// 设置内容自适应屏幕
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//		settings.setDefaultFontSize();// 设置字体
		// 设置默认缩放大小，必须放在这里，否则三星部分机型只能显示一半屏幕
		setZoom(context, settings);
		setNoScrollBar(webView);
	}

	// 设置默认缩放大小
	@SuppressWarnings("deprecation")
	private static void setZoom(Context context, WebSettings settings) {
		int mDensity = context.getResources().getDisplayMetrics().densityDpi;
		LogUtils.d(TAG, "屏幕密度----->" + mDensity);
		if (mDensity == 240) {
			settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		} else if (mDensity == 160) {
			settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		} else if (mDensity == 120) {
			settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
		} else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
			settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		} else if (mDensity == DisplayMetrics.DENSITY_TV) {
			settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		} else {
			settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		}
	}

	// 设置水平和垂直方向滚动条不显示
	private static void setNoScrollBar(final WebView webView) {
		webView.setVerticalScrollBarEnabled(true);
		webView.setHorizontalScrollBarEnabled(false);
	}

	/**
	 * 清除WebView缓存
	 *
	 * @param activity Activity对象的引用
	 * @param webView  WebView对象的引用
	 */
	public static void clearChache(Activity activity, final WebView webView) {
		ThreadUtils.runOnUIThread(activity, new Runnable() {
			@Override
			public void run() {
				webView.clearCache(true);
				webView.clearHistory();
			}
		});
	}

//	/**
//	 * 清除WebView的Cookies
//	 *
//	 * @param context 上下文引用
//	 */
//
//	public static void clearCookies(Context context) {
//		CookieSyncManager.createInstance(context);
//		CookieSyncManager.getInstance().startSync();
//		CookieManager.getInstance().removeAllCookie();
//	}


}
