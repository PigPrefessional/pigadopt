/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.view.pickerview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.StringUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.wheelview.WheelView;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.common.CommonUtils;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;

/**
 * 猪龄选择自定义View
 * Created by Justin Z on 2016/3/19.
 * 502953057@qq.com
 */
public class PigAgePickerView extends LinearLayout {

	private final static String TAG = PigAgePickerView.class.getSimpleName();
	/**
	 * 天换算成月的因子-天数按一个月按多少天来换算成月数
	 */
	private final static int CONVERSION_FACTOR = 29;
	/**
	 * 上下文引用
	 */
	private Context context;

	private LinearLayout pigAgeLayout;
	private WheelView monthWv;
	private NumericWheelAdapter monthWvAdapter;
	private TextView monthTv;
	private WheelView dayWv;
	private TextView dayTv;
	private NumericWheelAdapter dayWvAdapter;

	public PigAgePickerView(Context context) {
		super(context);
		init(context);
	}

	public PigAgePickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PigAgePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		ViewUtils.makeView(context, R.layout.pickerview_pig_age, this, true);
		assignViews();
		setWheelViews();
	}

	/**
	 * 分配各个View
	 */
	private void assignViews() {
		pigAgeLayout = (LinearLayout) findViewById(R.id.pig_age_ll);
		monthWv = (WheelView) findViewById(R.id.month_wv);
		monthTv = (TextView) findViewById(R.id.month_tv);
		dayWv = (WheelView) findViewById(R.id.day_wv);
		dayTv = (TextView) findViewById(R.id.day_tv);
	}

	private void setWheelViews() {
		// 月数范围从0-23个月
		monthWvAdapter = new NumericWheelAdapter(0, 23);
		monthWv.setAdapter(monthWvAdapter);
		monthWv.setCurrentItem(0);
		// 天数范围从1-29天
		dayWvAdapter = new NumericWheelAdapter(1, 29);
		dayWv.setAdapter(dayWvAdapter);
		dayWv.setCurrentItem(0);
	}

	/**
	 * 获取滚轮选中的猪龄，天数按一月29天换算成月数
	 *
	 * @return 返回月为单位的猪龄月数
	 */
	public float getSelectPigAge() {
		int month = (int) monthWvAdapter.getItem(monthWv.getCurrentItem());
		int day = (int) dayWvAdapter.getItem(dayWv.getCurrentItem());
		LogUtils.i(TAG, "当前选中：" + month + " 月");
		LogUtils.i(TAG, "当前选中：" + day + " 天");
		float dayMonth = CommonUtils.getMonth(day);
		float selectMonth = month + dayMonth;
		LogUtils.i(TAG, "换算成的猪龄为：" + selectMonth + " 月");
		return selectMonth;
	}

	// TODO:还没有做完换算
	public void setSelectPigAge(float month) {
		String [] ageArr = CommonUtils.getPigAgeMonthDay(month);
		int ageMonth = StringUtils.parseInt(ageArr[0]);
		int ageDay = StringUtils.parseInt(ageArr[1]);


	}


}
