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

package com.ai2020lab.aiutils.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Map;

/**
 * SharedPreferences工具类
 * Created by Justin on 2015/7/13.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PreferencesUtils {
	/**
	 * 保存boolean类型的SharedPreferences
	 *
	 * @param key   SharedPreferences的键名
	 * @param value SharedPreferences的值
	 */
	public static void setBoolean(Context context, String key, boolean value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putBoolean(key, value).commit();
	}

	/**
	 * 得到boolean类型的SharedPreferences
	 *
	 * @param key      SharedPreferences的键名
	 * @param defValue 指定默认的返回值
	 * @return 返回boolean类型的SharedPreferences
	 */
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(key, defValue);
	}

	/**
	 * 保存float类型的SharedPreferences
	 *
	 * @param key   SharedPreferences的键名
	 * @param value SharedPreferences的值
	 */
	public static void setFloat(Context context, String key, float value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putFloat(key, value).commit();
	}

	/**
	 * 得到float类型的SharedPreferences
	 *
	 * @param key      SharedPreferences的键名
	 * @param defValue 指定默认的返回值
	 * @return 返回float类型的SharedPreferences
	 */
	public static float getFloat(Context context, String key, float defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getFloat(
				key, defValue);
	}

	/**
	 * 保存long类型的SharedPreferences
	 *
	 * @param key   SharedPreferences的键名
	 * @param value SharedPreferences的值
	 */
	public static void setLong(Context context, String key, long value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putLong(key, value).commit();
	}

	/**
	 * 得到long类型的SharedPreferences
	 *
	 * @param key      SharedPreferences的键名
	 * @param defValue 指定默认的返回值
	 * @return 返回long类型的SharedPreferences
	 */
	public static long getLong(Context context, String key, long defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(
				key, defValue);
	}

	/**
	 * 保存int类型的SharedPreferences
	 *
	 * @param key   SharedPreferences的键名
	 * @param value SharedPreferences的值
	 */
	public static void setInt(Context context, String key, int value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt(key, value).commit();
	}

	/**
	 * 得到int类型的SharedPreferences
	 *
	 * @param key      SharedPreferences的键名
	 * @param defValue 指定默认的返回值
	 * @return 返回int类型的SharedPreferences
	 */
	public static int getInt(Context context, String key, int defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(
				key, defValue);
	}

	/**
	 * 保存String类型的SharedPreferences
	 *
	 * @param key   SharedPreferences的键名
	 * @param value SharedPreferences的值
	 */
	public static void setString(Context context, String key, String value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(key, value).commit();
	}

	/**
	 * 得到String类型的SharedPreferences
	 *
	 * @param key      SharedPreferences的键名
	 * @param defValue 指定默认的返回值
	 * @return 返回String类型的SharedPreferences
	 */
	public static String getString(Context context, String key, String defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(
				key, defValue);
	}

	/**
	 * SharedPreferences通用保存方法<br>
	 * 只支持String或者long,int,float,boolean的包装类型
	 *
	 * @param key SharedPreferences的键名
	 * @param t   SharedPreferences的值
	 */
	public static <T> void setSharedPref(Context context, String key, T t) {
		if (TextUtils.isEmpty(key)) {
			throw new IllegalArgumentException("SharedPreferences的键不能为空");
		}
		if (t == null) {
			throw new IllegalArgumentException("SharedPreferences的值不能为空");
		}
		if (t instanceof String) {
			setString(context, key, (String) t);
		} else if (t instanceof Long) {
			setLong(context, key, (Long) t);
		} else if (t instanceof Integer) {
			setInt(context, key, (Integer) t);
		} else if (t instanceof Float) {
			setFloat(context, key, (Float) t);
		} else if (t instanceof Boolean) {
			setBoolean(context, key, (Boolean) t);
		} else {
			throw new IllegalArgumentException(
					"传入参数t必须为String或者long,int,float,boolean的包装类型");
		}
	}

	/**
	 * SharedPreferences通用获取方法<br>
	 * 只支持String或者long,int,float,boolean的包装类型<br>
	 * 如果给出的默认值defValue和查找key返回的值类型不匹配会抛出类型不匹配异常
	 *
	 * @param key
	 * @param defValue 返回默认值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSharedPref(Context context, String key, T defValue) {
		if (key == null || key.equals("")) {
			throw new IllegalArgumentException("SharedPreferences的键不能为空");
		}
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		// 如果有指定的key，则遍历找到
		if (sharedPref.contains(key)) {
			Map<String, ?> prefMap = sharedPref.getAll();
			for (Map.Entry<String, ?> entry : prefMap.entrySet()) {
				String prefKey = entry.getKey();
				if (prefKey.equals(key)) {
					Object obj = entry.getValue();
					// 检查类型是否匹配, 如果类型不匹配则返回空
					if (defValue.getClass() == obj.getClass()) {
						return (T) obj;
					} else {
						throw new IllegalArgumentException("'" + key
								+ "' 对应的SharedPreferences的类型是'"
								+ obj.getClass() + "',和给出的默认值defValue的类型不匹配");
					}
				}
			}
		}
		// 如果没有指定的key,则按给出的defValue类型返回默认值
		else {
			return defValue;
		}
		return null;

	}


}
