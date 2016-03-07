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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * JSON工具类，对google GSON 的二次封装
 * Created by Justin on 2015/7/14.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class JsonUtils {

	private final static String TAG = JsonUtils.class.getSimpleName();

	private static Gson gson;
	private static JsonParser jsonParser;

	private static ParserType parserType = ParserType.NORMAL;

	/**
	 * 私有化构造器
	 */
	private JsonUtils() {
	}

	private static void initGson(ParserType type) {
		if (type == ParserType.EXCLUDE) {
			gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		} else if (type == ParserType.NORMAL) {
			gson = new GsonBuilder().create();
		}
	}

	// 初始化解析器
	private static void init(ParserType type) {
		if (jsonParser == null) {
			jsonParser = new JsonParser();
		}
		if (gson == null) {
			initGson(type);
			parserType = type;
		} else {
			if (parserType != type) {
				initGson(type);
				parserType = type;
			}
		}
	}

	/**
	 * 工厂方法，返回一个本类的实例
	 *
	 * @return JsonUtils类的实例
	 */
	public static JsonUtils getInstance() {
		init(parserType);
		return SingletonHolder.instance;
	}

	/**
	 * 工厂方法，返回一个本类的实例
	 *
	 * @param type 解析类型
	 * @return JsonUtils类的实例
	 */
	public static JsonUtils getInstance(ParserType type) {
		init(type);
		return SingletonHolder.instance;
	}

	/**
	 * 判断字符串是否是有效JSON格式
	 *
	 * @param jsonStr 要判断是否为JSON格式的字符窜
	 * @return 验证通过返回true，否则返回false
	 */
	public boolean isJsonStrAvailable(String jsonStr) {
		if (jsonStr == null || jsonStr.equals("")) {
			LogUtils.i(TAG, "不是有效的JSON格式字符窜");
			return false;
		}
		try {
			jsonParser.parse(jsonStr);
			return true;
		} catch (Exception e) {
			LogUtils.e(TAG, "不是有效的JSON格式字符窜");
			return false;
		}
	}

	/**
	 * 字符串转换为JsonObject对象
	 *
	 * @param jsonStr 输入的字符串
	 * @return 返回JsonObject对象
	 */
	public JsonObject stringToJsonObj(String jsonStr) {
		// JSON字符窜为空
		if (jsonStr == null || jsonStr.equals("")) {
			throw new IllegalArgumentException("要转换成JsonObject的字符窜不能为空");
		}
		JsonElement jsonE = null;
		try {
			jsonE = jsonParser.parse(jsonStr);
		} catch (Exception e) {
			// JSON转换时异常，JSON字符窜格式不正确导致
			LogUtils.e(TAG, " 字符窜转换成JsonObject异常");
		}
		if (jsonE == null || !jsonE.isJsonObject()) {
			// 不是JsonObject格式
			LogUtils.e(TAG, "字符窜转换成JsonObject失败，转换结果不是JsonObject格式");
			return null;
		}
		return (JsonObject) jsonE;
	}

	/**
	 * 字符串转换为JsonArray对象
	 *
	 * @param jsonStr 输入的字符串
	 * @return 返回JsonArray对象
	 */
	public JsonArray stringToJsonArr(String jsonStr) {
		// JSON字符窜为空
		if (jsonStr == null || jsonStr.equals("")) {
			throw new IllegalArgumentException("要转换成JsonArray的字符窜不能为空");
		}
		JsonElement jsonE = null;
		try {
			jsonE = jsonParser.parse(jsonStr);
		} catch (Exception e) {
			// JSON转换时异常，JSON字符窜格式不正确导致
			LogUtils.e(TAG, " 字符窜转换成JsonArray异常");
		}
		if (jsonE == null || !jsonE.isJsonArray()) {
			// 不是JsonArray格式
			LogUtils.e(TAG, "字符窜转换成JsonArray失败，转换结果不是JsonArray格式");
			return null;
		}
		return (JsonArray) jsonE;
	}

	/**
	 * 获取JSONObject中指定属性名的String的value
	 *
	 * @param key 属性名
	 * @return 得到指定属性名的value
	 */
	public String getObjStringValue(String key, JsonObject obj) {
		if (key == null || key.equals("")) {
			// 指定的Key为空
			throw new IllegalArgumentException("参数key不能为空");
		}
		if (obj == null || !obj.isJsonObject()) {
			// JsonObject对象为空或者格式不是JsonObject
			throw new IllegalArgumentException("参数obj不能为空，并且必须是JsonObject格式");
		}
		if (!obj.has(key)) {
			// 没有指定Key的属性
			LogUtils.e(TAG, "obj中没有找到key为'" + key + "' 的键");
			return null;
		}
		return obj.get(key).getAsString();
	}

	/**
	 * 获取JSONObject中指定属性名的Integer的value
	 *
	 * @param key 属性名
	 * @return 得到指定属性名的value
	 */
	public Integer getObjIntValue(String key, JsonObject obj) {
		if (key == null || key.equals("")) {
			// 指定的Key为空
			throw new IllegalArgumentException("参数key不能为空");
		}
		if (obj == null || !obj.isJsonObject()) {
			// JsonObject对象为空或者格式不是JsonObject
			throw new IllegalArgumentException("参数obj不能为空，并且必须是JsonObject格式");
		}
		if (!obj.has(key)) {
			// 没有指定Key的属性
			LogUtils.e(TAG, "obj中没有找到key为'" + key + "' 的键");
			return null;
		}
		return obj.get(key).getAsInt();
	}

	/**
	 * POJO对象转换为JSON格式字符窜
	 *
	 * @param obj 传入的POJO对象的引用
	 * @return 返回JSON格式的字符窜，转换失败则返回null
	 */
	public String serializeToJson(Object obj) {
		if (obj == null) {
			LogUtils.i(TAG, "输入参数POJO对象为空");
			return null;
		}
		try {
			return gson.toJson(obj);
		} catch (Exception e) {
			// 序列化异常
			LogUtils.e(TAG, "POJO对象序列化JSON异常");
		}
		return null;
	}

	/**
	 * JSON格式字符窜转换为POJO对象
	 *
	 * @param jsonStr JSON字符窜
	 * @param cls     反序列化目标POJO类的Class对象
	 * @return 返回POJO对象，转换失败则返回null
	 */
	public <T> T deserializeToObj(String jsonStr, Class<T> cls) {
		if (jsonStr == null || jsonStr.equals("")) {
			LogUtils.i(TAG, "deserializeToObj:参数jsonStr不能为空");
			return null;
		}
		if (cls == null) {
			LogUtils.i(TAG, "deserializeToObj:参数cls不能为空");
			return null;
		}
		try {
			return gson.fromJson(jsonStr, cls);
		} catch (Exception e) {
			// 反序列化失败异常
			LogUtils.e(TAG, "JSON反序列化POJO对象异常", e);
		}
		return null;
	}

	public enum ParserType {
		NORMAL, EXCLUDE
	}

	private static class SingletonHolder {
		private static final JsonUtils instance = new JsonUtils();
	}
}
