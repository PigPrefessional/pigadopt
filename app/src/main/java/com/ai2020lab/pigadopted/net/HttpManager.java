package com.ai2020lab.pigadopted.net;

import android.content.Context;

import com.ai2020lab.aiutils.common.JsonUtils;
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.net.HttpUtils;
import com.ai2020lab.pigadopted.model.base.RequestData;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by Justin on 2015/11/11.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class HttpManager {

	public static final String RSA = "rsa";
	private static final int TIME_OUT = 5 * 1000;
	private final static String TAG = HttpManager.class.getSimpleName();
	public static String HTTP_HEADER_HTTP_QUERY = "HttpQuery";
	public static String USER_ID = "100000001872";
	public static String CONTENT_TYPE = "Content-Type";
	public static String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
	public static String CONTENT_TYPE_JPEG = "image/jpeg";
	public static String CONTENT_TYPE_MULTIPART = "multipart/form-data";


	private static <T> String getRequestJson(T requestObj) {
		RequestData data = new RequestData();
		data.data = requestObj;
		String sendJSON = JsonUtils.getInstance().serializeToJson(data);
		LogUtils.i(TAG, "请求JSON数据-->" + sendJSON);
		return sendJSON;
	}

	private static String getVerifyString(String userId) {
		return String.format("%s&0&%s&%s", String.valueOf(new Date().getTime()), userId, RSA);
	}

	/**
	 * post发送json格式字符窜
	 *
	 * @param context    上下文引用
	 * @param url        接口访问地址url
	 * @param requestObj 发送数据对象实例
	 * @param response   ResponseHandlerInterface的引用
	 */
	public static <T> void postJson(Context context, String url, T requestObj,
	                                ResponseHandlerInterface response) {
		LogUtils.i(TAG, "----POST发送JSON数据----");
		HashMap<String, String> headerParams = new HashMap<>();

		String verifyString = getVerifyString(USER_ID);
		headerParams.put(HTTP_HEADER_HTTP_QUERY, verifyString);

		// 设置HTTP请求体
		StringEntity entity = null;
		try {
			String json = getRequestJson(requestObj);
			entity = new StringEntity(json, HTTP.UTF_8);
		} catch (UnsupportedCharsetException e) {
			LogUtils.e(TAG, "UnsupportedCharsetException", e);
		}
		if (entity == null) {
			LogUtils.i(TAG, "要发送的请求数据对象为空");
			return;
		}
		HttpUtils.post(context, url, TIME_OUT, headerParams, entity, CONTENT_TYPE_JSON, response);
	}

//	/**
//	 * post发送json格式数据,同时上传文件
//	 *
//	 * @param context    上下文引用
//	 * @param url        接口访问地址url
//	 * @param requestObj 发送数据对象实例
//	 * @param filePath   上传文件路径
//	 * @param response   ResponseHandlerInterface的引用
//	 */
//	public static <T> void postFile(Context context, String url,
//	                                T requestObj, String filePath,
//	                                ResponseHandlerInterface response) {
//		LogUtils.i(TAG, "----POST发送照片文件----");
//		HashMap<String, String> headerParams = new HashMap<>();
////		headerParams.put(HTTP_HEADER_HTTP_QUERY, getVerifyString(USER_ID));
//
//		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//		//MultipartEntity有2个模式，STRICT和BROWSER_COMPATIBLE
//		//缺省为STRICT,发送Content-Type和Content-Transfer-Encoding
//		//BROWSER_COMPATIBLE不会
//		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//		builder.addTextBody("data", getRequestJson(requestObj),
//				ContentType.create("text/plain", Charset.forName("utf-8")));
//		builder.addPart("fileUp", new FileBody(new File(filePath)));
//		HttpEntity entity = builder.build();
//		if (entity == null) {
//			LogUtils.i(TAG, "要发送的请求数据对象为空");
//			return;
//		}
//		HttpUtils.post(context, url, TIME_OUT, headerParams, entity,
//				CONTENT_TYPE_MULTIPART, response);
//	}

	/**
	 * post发送json格式数据,同时上传文件
	 *
	 * @param context    上下文引用
	 * @param url        接口访问地址url
	 * @param requestObj 发送数据对象实例
	 * @param filePath   上传文件路径
	 * @param response   ResponseHandlerInterface的引用
	 */
	public static <T> void postFile(Context context, String url,
	                                T requestObj, String filePath,
	                                ResponseHandlerInterface response) {
		LogUtils.i(TAG, "----POST请求 multipart方式----");
		HashMap<String, String> headerParams = new HashMap<>();
//		headerParams.put(HTTP_HEADER_HTTP_QUERY, getVerifyString(USER_ID));

		RequestParams params = new RequestParams();
		// JSON字符窜
		params.put("data", getRequestJson(requestObj));

		try {
			params.put("fileUp", new File(filePath));
		} catch (FileNotFoundException e) {
			LogUtils.e(TAG, "FileNotFoundException", e);
			return;
		}
		HttpUtils.post(context, url, TIME_OUT, headerParams, params, response);
	}

}
