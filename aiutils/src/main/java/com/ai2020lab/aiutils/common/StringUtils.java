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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 字符窜处理工具类
 * Created by Justin on 2016/1/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class StringUtils {

	private final static String TAG = StringUtils.class.getSimpleName();

	/**
	 * 将字符窜进行MD5编码
	 * @param key 要进行MD5编码的字符窜
	 * @return 返回MD5编码的字符窜
	 */
	public static String encodeToMD5(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte aByte : bytes) {
			String hex = Integer.toHexString(0xFF & aByte);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static int parseInt(String s){
		int num = 0;
		try{
			num = Integer.parseInt(s);
		} catch(Exception e){
			LogUtils.e(TAG, "Exception", e);
		}
		return num;
	}

	public static long parseLong(String s){
		long num = 0l;
		try{
			num = Long.parseLong(s);
		} catch(Exception e){
			LogUtils.e(TAG, "Exception", e);
		}
		return num;
	}

	public static double parseDouble(String s){
		double num = 0;
		try{
			num = Double.parseDouble(s);
		} catch(Exception e){
			LogUtils.e(TAG, "Exception", e);
		}
		return num;
	}

	public static float parseFloat(String s){
		float num = 0;
		try{
			num = Float.parseFloat(s);
		} catch(Exception e){
			LogUtils.e(TAG, "Exception", e);
		}
		return num;
	}


}
