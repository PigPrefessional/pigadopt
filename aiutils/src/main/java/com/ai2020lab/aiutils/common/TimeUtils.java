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

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间工具类
 * Created by Justin on 2015/7/13.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class TimeUtils {
	private final static String TAG = TimeUtils.class.getSimpleName();

	/**
	 * 验证字符窜是否是合法的日期格式
	 *
	 * @param date 日期格式的字符窜
	 * @return true-合法，false-不合法
	 */
	public static boolean checkDate(String date) {
		if (date == null || date.equals("")) {
			return false;
		}
		Pattern p = Pattern.compile(Template.CHECK_REGEXP);
		Matcher m = p.matcher(date);
		return m.matches();
	}

	/**
	 * 将Date格式化成指定格式的字符窜
	 *
	 * @param date     Date对象的引用
	 * @param template 日期格式化模板字符窜，常用的模板在常量接口{@link Template}中定义
	 * @param locale   Locale对象的引用,如果为null则使用Locale.getDefault()来生成SimpleDateFormat对象
	 * @return 返回template格式的时间字符窜, 如果date为空或者格式化失败则返回null
	 */
	public static String formatDate(Date date, String template, Locale locale) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = getDateFormatInstance(template, locale);
		if (format != null) {
			return format.format(date);
		}
		return null;
	}

	/**
	 * 将Date格式化成指定格式的字符窜
	 * <p/>
	 * 这个方法将使用Local.getDefault()来生成SimpleDateFormat对象
	 *
	 * @param date     Date对象的引用
	 * @param template 日期格式化模板字符窜，常用的模板在常量接口{@link Template}中定义
	 * @return 返回template格式的时间字符窜, 如果date为空或者格式化失败则返回null
	 */
	public static String formatDate(Date date, String template) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = getDateFormatInstance(template, null);
		if (format != null) {
			return format.format(date);
		}
		return null;
	}

	/**
	 * 时间戳转化为成指定格式的字符窜
	 * @param timeStamp 时间戳
	 * @param template 格式化模板
	 * @return 转化成功返回指定格式的时间展示字符窜，失败返回null
	 */
	public static String formatTimeStamp(long timeStamp, String template){
		SimpleDateFormat format = getDateFormatInstance(template, null);
		if (format != null) {
			return format.format(timeStamp);
		}
		return null;
	}

	/**
	 * 将指定时间戳转换为规定模板格式的Date对象
	 * @param timeStamp 要转换的时间戳
	 * @param template 格式化模板
	 * @return 转换成功返回Date对象，失败返回null
	 */
	public static Date timeStampToDate(long timeStamp, String template){
		String d = formatTimeStamp(timeStamp, template);
		if(TextUtils.isEmpty(d))
			return null;
		return paseDate(d, template);
	}

	public static Date timeStampToDate(long timeStamp){
		String d = formatTimeStamp(timeStamp, Template.YMDHMS_UNSIGNED);
		if(TextUtils.isEmpty(d))
			return null;
		return paseDate(d, Template.YMDHMS_UNSIGNED);
	}

	/**
	 * 将指定的时间字符窜转换为时间戳
	 * @param date 指定的时间格式字符窜
	 * @param template 格式化模板
	 * @param locale  Locale对象的引用,如果为null则使用Locale.getDefault()来生成SimpleDateFormat对象
	 * @return 返回转换成功的时间戳，失败返回-1l
	 */
	public static long dateToTimeStamp(String date, String template, Locale locale){
		Date d = paseDate(date, template, locale);
		if(d == null) return -1l;
		return d.getTime();
	}

	public static long dateToTimeStamp(String date, String template){
		Date d = paseDate(date, template);
		if(d == null) return -1l;
		return d.getTime();
	}

	public static long dateToTimeStamp(String date){
		Date d = paseDate(date, Template.YMDHMS_UNSIGNED);
		if(d == null) return -1l;
		LogUtils.i(TAG, "转换时间:" + d.toString());
		return d.getTime();
	}

	/**
	 * 将字符窜转化为Date对象
	 *
	 * @param date     时间格式的字符窜
	 * @param template 日期格式化模板字符窜，常用的模板在常量接口{@link Template}中定义
	 * @param locale   Locale对象的引用,如果为null则使用Locale.getDefault()来生成SimpleDateFormat对象
	 * @return 返回转化的Date对象，失败返回null
	 */
	public static Date paseDate(String date, String template, Locale locale) {
		SimpleDateFormat format = getDateFormatInstance(template, locale);
		Date d = null;
		// 验证时间字符窜的合法性
		if (!checkDate(date)) {
			return null;
		}
		if (format != null) {
			try {
				d = format.parse(date);
			} catch (ParseException e) {
				LogUtils.e(TAG, "ParseException", e);
			}
		}
		return d;
	}

	/**
	 * 将字符窜转化为Date对象
	 * <p/>
	 * 这个方法将使用Local.getDefault()来生成SimpleDateFormat对象
	 *
	 * @param date     时间格式的字符窜
	 * @param template 日期格式化模板字符窜，常用的模板在常量接口DateTemplate中定义
	 * @return 返回转化的Date对象
	 */
	public static Date paseDate(String date, String template) {
		SimpleDateFormat format = getDateFormatInstance(template, null);
		Date d = null;
//		// 验证时间字符窜的合法性
//		if (!checkDate(date)) {
//			return null;
//		}
		if (format != null) {
			try {
				d = format.parse(date);
			} catch (ParseException e) {
				LogUtils.e(TAG, "ParseException", e);
			}
		}
		return d;
	}

	/**
	 * 获得一个SimpleDateFormat对象
	 *
	 * @param template 日期格式化模板字符窜，常用的模板在常量接口DateTemplate中定义
	 * @param locale   Locale对象的引用,如果为null则使用Locale.getDefault()来生成SimpleDateFormat对象
	 * @return 返回SimpleDateFormat对象，template为空或者无效则返回null
	 */
	public static SimpleDateFormat getDateFormatInstance(String template, Locale locale) {
		if (template == null || template.equals("")) {
			return null;
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		SimpleDateFormat format = null;
		try {
			format = new SimpleDateFormat(template, locale);

		} catch (NullPointerException e) {
			LogUtils.e(TAG, "NullPointerException", e);
		} catch (IllegalArgumentException e) {
			LogUtils.e(TAG, "IllegalArgumentException", e);
		}
		return format;
	}

	/**
	 * 比较2个Date对象的时间大小
	 *
	 * @param date1 Date对象1
	 * @param date2 Date对象2
	 * @return date1比date2早, 返回-1 , date1比date2晚返回1 ,
	 * date1比date2相同返回0，如果date1或者date2为空也返回0
	 */
	public static int compare2Dates(Date date1, Date date2) {
		if (date1 == null) {
			LogUtils.i(TAG, "date1为空");
			return 0;
		}
		if (date2 == null) {
			LogUtils.i(TAG, "date2为空");
			return 0;
		}
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		if (time1 > time2) {
			return 1;
		} else if (time1 < time2) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 比较2个Date对象的时间大小
	 *
	 * @param date1  Date对象1
	 * @param date2  Date对象2
	 * @param locale Locale对象的引用,如果为null会自动使用Locale.getDefault()得到
	 * @return date1比date2早, 返回-1 , date1比date2晚返回1 ,
	 * date1比date2相同返回0，如果date1或者date2为空也返回0
	 */
	public static int compare2Dates(Date date1, Date date2, Locale locale) {
		if (date1 == null) {
			LogUtils.i(TAG, "date1为空");
			return 0;
		}
		if (date2 == null) {
			LogUtils.i(TAG, "date2为空");
			return 0;
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		Calendar c1 = Calendar.getInstance(locale);
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance(locale);
		c2.setTime(date2);
		return c1.compareTo(c2);
	}

	/**
	 *
	 */
	public interface Template {
		/**
		 * 用于验证日期格式的正则表达式
		 */
		String CHECK_REGEXP = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";

		// 常用的一些日期格式化字符窜模板
		String YMDHMS = "yyyy-MM-dd HH:mm:ss";
		String YMDAHM = "yyyy-MM-dd aa HH:mm";
		String YMDHMS_UNSIGNED = "yyyyMMddHHmmss";
		String YMD_HMS = "yyyyMMdd_HHmmss";
		String YMDHM = "yyyy-MM-dd HH:mm";
		String YMD = "yyyy-MM-dd";
		String HMS = "HH:mm:ss";
		String HM = "HH:mm";
		String YMDHMS_IMG = "'IMG'_yyyyMMdd_HHmmss";
		String MD = "MM-dd";
	}


}
