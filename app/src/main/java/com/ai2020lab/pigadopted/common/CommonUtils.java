/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.common;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.StringUtils;

import java.util.Date;

/**
 * 通用工具类
 * Created by Justin Z on 2016/3/22.
 * 502953057@qq.com
 */
public class CommonUtils {

	private final static String TAG = CommonUtils.class.getSimpleName();

	public final static int MONTH_DAY_FACTOR = 29;

	/**
	 * 根据入栏时间，当前时间和入栏猪龄计算得到当前猪龄
	 *
	 * @param attendedTime 入栏时间戳
	 * @param attendedAge  入栏猪龄，单位为月的float
	 * @return 返回当前猪龄，单位为月,保留2位小数的float
	 */
	public static float getPigAge(long attendedTime, float attendedAge) {
		// 获取当前时间戳
		long currentTime = new Date().getTime();
		// 入栏时间同当前时间的时间差
		long diff = currentTime - attendedTime;
		int day = (int) (diff / (1000 * 60 * 60 * 24));
		// 将入栏年龄月数转换成天数
		int attendedDay = getDay(attendedAge);
		float pigAge = getMonth(attendedDay + day);
//		LogUtils.i(TAG, "入栏天数:" + day);
//		LogUtils.i(TAG, "当前时间(时间戳):" + currentTime);
//		String currentTimeStr = TimeUtils.formatTimeStamp(currentTime, TimeUtils.Template.YMD);
//		LogUtils.i(TAG, "当前时间(显示):" + currentTimeStr);
//		LogUtils.i(TAG, "入栏时间(时间戳):" + attendedTime);
//		String attendedTimeStr = TimeUtils.formatTimeStamp(attendedTime, TimeUtils.Template.YMD);
//		LogUtils.i(TAG, "入栏时间(显示):" + attendedTimeStr);
		LogUtils.i(TAG, "入栏猪龄（月数）:" + attendedAge);
		LogUtils.i(TAG, "入栏猪龄（天数）:" + attendedDay);
		LogUtils.i(TAG, "当前猪龄:" + pigAge + " 月");
		return pigAge;
	}

	/**
	 * 将猪龄月数转换为显示用的猪龄
	 *
	 * @param pigAge 猪龄，单位为月，保留小数点后2位
	 * @return 返回显示用的猪龄数组，数组第一个元素为月数，第二个元素为天数，显示方式为xx月xx天
	 */
	public static String[] getPigAgeMonthDay(float pigAge) {
		int ageMonth = (int) pigAge;
		// 获取小数部分
		String pigAgeStr = pigAge + "";
		float pigAgeDecimal = StringUtils.parseFloat(pigAgeStr.substring(pigAgeStr.indexOf("."),
				pigAgeStr.length()));
		LogUtils.i(TAG, "小数部分：" + pigAgeDecimal);
		int ageDay = roundInt(pigAgeDecimal * MONTH_DAY_FACTOR);
		LogUtils.i(TAG, "猪龄月数：" + ageMonth);
		LogUtils.i(TAG, "猪龄天数：" + ageDay);
		String[] ageArr = new String[2];
		ageArr[0] = ageMonth + "";
		ageArr[1] = ageDay + "";
		return ageArr;
	}

	/**
	 * 根据月数换算天数，按一月29天计算
	 */
	public static int getDay(float month) {
		int day = roundInt(month * MONTH_DAY_FACTOR);
		LogUtils.i(TAG, "转换成天数为:" + day);
		return day;
	}

	/**
	 * 根据天数换算月数
	 */
	public static float getMonth(int day) {
		float month = roundFloat((float) day / MONTH_DAY_FACTOR, 2);
		LogUtils.i(TAG, "转换成月数为:" + month);
		return month;
	}

	/**
	 * 4舍5入并保留digits位小数
	 */
	public static float roundFloat(float num, int digits) {
		return StringUtils.parseFloat(String.format("%." + "" + digits + "f", num));
	}

	/**
	 * 4舍5入只保留整数
	 */
	public static int roundInt(float num) {
		return (int) (num + 0.5);
	}


}
