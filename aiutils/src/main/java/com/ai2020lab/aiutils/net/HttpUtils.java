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

package com.ai2020lab.aiutils.net;

import android.content.Context;
import android.text.TextUtils;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.system.NetworkUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;

/**
 * HTTP工具类
 * Created by Justin on 2015/7/20.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class HttpUtils {

	private final static String TAG = HttpUtils.class.getSimpleName();

	/**
	 * post请求
	 *
	 * @param context      上下文引用
	 * @param url          请求URL地址
	 * @param timeOut      请求超时时间
	 * @param headerParams 请求头参数HashMap对象
	 * @param entity       HttpEntity对象的引用
	 * @param contentType  上传文件contentType类型
	 * @param response     ResponseHandlerInterface接口的引用
	 */
	public static void post(Context context, String url,
	                        int timeOut, HashMap<String, String> headerParams,
	                        HttpEntity entity, String contentType,
	                        ResponseHandlerInterface response) {
		if (context == null) {
			LogUtils.i(TAG, "上下文引用context为空");
			return;
		}
		if (TextUtils.isEmpty(url)) {
			LogUtils.i(TAG, "请求地址url为空");
			return;
		}
		LogUtils.i(TAG, "请求地址->" + url);
		AsyncHttpClient client = new AsyncHttpClient();
		// 设置请求超时时间
		if (timeOut >= 0)
			client.setTimeout(timeOut);
		// 设置HTTP请求头
		for (Map.Entry<String, String> entry : headerParams.entrySet()) {
			client.addHeader(entry.getKey(), entry.getValue());
		}
		LogUtils.i(TAG, "--发起HTTP post请求--");
		client.post(context, url, entity, contentType, response);
		// 监测网络状态，没有连接网络则取消请求
		boolean isNetAvailable = NetworkUtils.isNetworkAvailable(context);
		if (!isNetAvailable) {
			LogUtils.i(TAG, "没有网络连接，将取消请求");
			// 取消请求要放在发送请求的后面才有效果
			client.cancelRequests(context, true);
		}

	}

	/**
	 * post请求
	 *
	 * @param context       上下文引用
	 * @param url           请求URL地址
	 * @param timeOut       请求超时时间
	 * @param headerParams  请求头参数HashMap对象
	 * @param requestParams RequestParams
	 * @param response      ResponseHandlerInterface接口的引用
	 */
	public static void post(Context context, String url,
	                        int timeOut, HashMap<String, String> headerParams,
	                        RequestParams requestParams,
	                        ResponseHandlerInterface response) {
		if (context == null) {
			LogUtils.i(TAG, "上下文引用context为空");
			return;
		}
		if (TextUtils.isEmpty(url)) {
			LogUtils.i(TAG, "请求地址url为空");
			return;
		}
		LogUtils.i(TAG, "请求地址->" + url);
		AsyncHttpClient client = new AsyncHttpClient();
		// 设置请求超时时间
		if (timeOut >= 0)
			client.setTimeout(timeOut);
		// 设置HTTP请求头
		for (Map.Entry<String, String> entry : headerParams.entrySet()) {
			client.addHeader(entry.getKey(), entry.getValue());
		}

		LogUtils.i(TAG, "--发起HTTP post请求--");
		client.post(url, requestParams, response);
		// 监测网络状态，没有连接网络则取消请求
		boolean isNetAvailable = NetworkUtils.isNetworkAvailable(context);
		if (!isNetAvailable) {
			LogUtils.i(TAG, "没有网络连接，将取消请求");
			// 取消请求要放在发送请求的后面才有效果
			client.cancelRequests(context, true);
		}
	}


}
