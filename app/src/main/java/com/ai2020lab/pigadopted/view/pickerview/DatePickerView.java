/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.view.pickerview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.wheelview.WheelView;
import com.ai2020lab.pigadopted.R;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Justin Z on 2016/3/20.
 * 502953057@qq.com
 */
public class DatePickerView extends LinearLayout {

	private final static String TAG = DatePickerView.class.getSimpleName();

	public static final int DEFAULT_START_YEAR = 2016;
	public static final int DEFAULT_END_YEAR = 2100;

	private Context context;

	private LinearLayout dateLl;
	private WheelView yearWv;
	private TextView yearTv;
	private WheelView monthWv;
	private TextView monthTv;
	private WheelView dayWv;
	private TextView dayTv;

	private int startYear = DEFAULT_START_YEAR;
	private int endYear = DEFAULT_END_YEAR;

	private List<String> list_big;
	private List<String> list_little;


	public DatePickerView(Context context) {
		super(context);
		init(context);
	}

	public DatePickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		setMonthList();
		ViewUtils.makeView(context, R.layout.pickerview_date, this, true);
		assignViews();
	}

	/**
	 * 分配各个View
	 */
	private void assignViews() {
		dateLl = (LinearLayout) findViewById(R.id.date_ll);
		yearWv = (WheelView) findViewById(R.id.year_wv);
		yearTv = (TextView) findViewById(R.id.year_tv);
		monthWv = (WheelView) findViewById(R.id.month_wv);
		monthTv = (TextView) findViewById(R.id.month_tv);
		dayWv = (WheelView) findViewById(R.id.day_wv);
		dayTv = (TextView) findViewById(R.id.day_tv);
	}

	// 添加大小月月份并将其转换为list,方便之后的判断
	private void setMonthList() {
		String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
		String[] months_little = {"4", "6", "9", "11"};
		list_big = Arrays.asList(months_big);
		list_little = Arrays.asList(months_little);
	}

	/**
	 * 设置选择器初始显示
	 *
	 * @param year  初始显示的年
	 * @param month 初始显示的月
	 * @param day   初始显示的日
	 */
	public void setPickerView(int year, int month, int day) {
		// 年
		yearWv.setAdapter(new NumericWheelAdapter(startYear, endYear));
		yearWv.setCurrentItem(year - startYear);
		// 月
		monthWv.setAdapter(new NumericWheelAdapter(1, 12));
		monthWv.setCurrentItem(month - 1);
		// 日
		// 判断大小月及是否闰年,用来确定"日"的数据
		NumericWheelAdapter dayWvAdapter;
		if (list_big.contains(String.valueOf(month + 1))) {
			dayWvAdapter = new NumericWheelAdapter(1, 31);
		} else if (list_little.contains(String.valueOf(month + 1))) {
			dayWvAdapter = new NumericWheelAdapter(1, 30);
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				dayWvAdapter = new NumericWheelAdapter(1, 29);
			else
				dayWvAdapter = new NumericWheelAdapter(1, 28);
		}
		dayWv.setAdapter(dayWvAdapter);
		dayWv.setCurrentItem(day - 1);
		// 绑定选择监听
		yearWv.setOnItemSelectedListener(yearWvSelectedListener);
		monthWv.setOnItemSelectedListener(monthWvSelectedListener);
	}

	/**
	 * 获取选择的时间
	 * @return
	 */
	public String getSelectTime() {
		StringBuffer sb = new StringBuffer();
		sb.append((yearWv.getCurrentItem() + startYear)).append("-")
				.append((monthWv.getCurrentItem() + 1)).append("-")
				.append((dayWv.getCurrentItem() + 1));
		return sb.toString();
	}

	// 年监听，不同的年来判断日的显示范围
	private OnItemSelectedListener yearWvSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(int index) {
			int year_num = index + startYear;
			// 判断大小月及是否闰年,用来确定"日"的数据
			int maxItem = 30;
			if (list_big
					.contains(String.valueOf(monthWv.getCurrentItem() + 1))) {
				dayWv.setAdapter(new NumericWheelAdapter(1, 31));
				maxItem = 31;
			} else if (list_little.contains(String.valueOf(monthWv
					.getCurrentItem() + 1))) {
				dayWv.setAdapter(new NumericWheelAdapter(1, 30));
				maxItem = 30;
			} else {
				if ((year_num % 4 == 0 && year_num % 100 != 0)
						|| year_num % 400 == 0) {
					dayWv.setAdapter(new NumericWheelAdapter(1, 29));
					maxItem = 29;
				} else {
					dayWv.setAdapter(new NumericWheelAdapter(1, 28));
					maxItem = 28;
				}
			}
			if (dayWv.getCurrentItem() > maxItem - 1) {
				dayWv.setCurrentItem(maxItem - 1);
			}
		}
	};

	// 月监听
	private OnItemSelectedListener monthWvSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(int index) {
			int month_num = index + 1;
			int maxItem = 30;
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big.contains(String.valueOf(month_num))) {
				dayWv.setAdapter(new NumericWheelAdapter(1, 31));
				maxItem = 31;
			} else if (list_little.contains(String.valueOf(month_num))) {
				dayWv.setAdapter(new NumericWheelAdapter(1, 30));
				maxItem = 30;
			} else {
				if (((yearWv.getCurrentItem() + startYear) % 4 == 0 && (yearWv
						.getCurrentItem() + startYear) % 100 != 0)
						|| (yearWv.getCurrentItem() + startYear) % 400 == 0) {
					dayWv.setAdapter(new NumericWheelAdapter(1, 29));
					maxItem = 29;
				} else {
					dayWv.setAdapter(new NumericWheelAdapter(1, 28));
					maxItem = 28;
				}
			}
			if (dayWv.getCurrentItem() > maxItem - 1) {
				dayWv.setCurrentItem(maxItem - 1);
			}

		}
	};


}
