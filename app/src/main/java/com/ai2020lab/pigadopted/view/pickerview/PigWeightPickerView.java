/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.view.pickerview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.wheelview.WheelView;
import com.ai2020lab.pigadopted.R;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;

/**
 * 猪体重选择自定义View
 * Created by Justin Z on 2016/3/20.
 * 502953057@qq.com
 */
public class PigWeightPickerView extends LinearLayout {

	private final static String TAG = PigWeightPickerView.class.getSimpleName();

	private Context context;

	private LinearLayout pigWeightLl;
	private WheelView weightHundredWv;
	private NumericWheelAdapter hundredWvAdapter;
	private WheelView weightDecadeWv;
	private NumericWheelAdapter decadeWvAdapter;
	private WheelView weightUnitWv;
	private NumericWheelAdapter unitWvAdapter;
	private TextView weightUnitTv;

	public PigWeightPickerView(Context context) {
		super(context);
		init(context);
	}

	public PigWeightPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PigWeightPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		ViewUtils.makeView(context, R.layout.pickerview_pig_weight, this, true);
		assignViews();
		setWheelViews();
	}

	private void assignViews() {
		pigWeightLl = (LinearLayout) findViewById(R.id.pig_weight_ll);
		weightHundredWv = (WheelView) findViewById(R.id.weight_hundred_wv);
		weightDecadeWv = (WheelView) findViewById(R.id.weight_decade_wv);
		weightUnitWv = (WheelView) findViewById(R.id.weight_unit_wv);
		weightUnitTv = (TextView) findViewById(R.id.weight_unit_tv);
	}

	private void setWheelViews() {
		// 百位范围0-3
		hundredWvAdapter = new NumericWheelAdapter(0, 3);
		weightHundredWv.setAdapter(hundredWvAdapter);
		weightHundredWv.setCurrentItem(0);
		// 十位范围0-9
		decadeWvAdapter = new NumericWheelAdapter(0, 9);
		weightDecadeWv.setAdapter(decadeWvAdapter);
		weightDecadeWv.setCurrentItem(0);
		// 个位范围0-9
		unitWvAdapter = new NumericWheelAdapter(0, 9);
		weightUnitWv.setAdapter(unitWvAdapter);
		weightUnitWv.setCurrentItem(1);
	}

	/**
	 * 获取选择的猪体重，单位为公斤
	 *
	 * @return 返回选择的猪体重
	 */
	public int getSelectPigWeight() {
		// 百位
		int hundred = (int) hundredWvAdapter.getItem(weightHundredWv.getCurrentItem());
		// 十位
		int decade = (int) decadeWvAdapter.getItem(weightDecadeWv.getCurrentItem());
		// 个位
		int unit = (int) unitWvAdapter.getItem(weightUnitWv.getCurrentItem());
		int pigWeight = hundred * 100 + decade * 10 + unit;
		LogUtils.i(TAG, "选择的猪体重为:" + pigWeight + " 公斤");
		return pigWeight;
	}


}
