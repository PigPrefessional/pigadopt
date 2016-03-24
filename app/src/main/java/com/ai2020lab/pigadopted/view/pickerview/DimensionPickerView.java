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
import com.ai2020lab.pigadopted.common.CommonUtils;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;

/**
 * 尺寸选择自定义View
 * Created by Justin Z on 2016/3/23.
 * 502953057@qq.com
 */
public class DimensionPickerView extends LinearLayout {

	private final static String TAG = DimensionPickerView.class.getSimpleName();
	private Context context;

	private LinearLayout dimensionLl;
	private WheelView intWv;
	private NumericWheelAdapter intWvAdapter;
	private WheelView decimalWv;
	private NumericWheelAdapter decimalWvAdapter;
	private TextView unitTv;

	public DimensionPickerView(Context context) {
		super(context);
		init(context);
	}

	public DimensionPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DimensionPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		ViewUtils.makeView(context, R.layout.pickerview_dimension, this, true);
		assignViews();
		setWheelViews();
	}

	private void assignViews() {
		dimensionLl = (LinearLayout) findViewById(R.id.dimension_ll);
		intWv = (WheelView) findViewById(R.id.int_wv);
		decimalWv = (WheelView) findViewById(R.id.decimal_wv);
		unitTv = (TextView) findViewById(R.id.unit_tv);
	}

	private void setWheelViews() {
		intWvAdapter = new NumericWheelAdapter(0, 5);
		intWv.setAdapter(intWvAdapter);
		intWv.setCurrentItem(1);
		decimalWvAdapter = new NumericWheelAdapter(0, 9);
		decimalWv.setAdapter(decimalWvAdapter);
		decimalWv.setCurrentItem(0);
	}

	/**
	 * 获取选择的尺寸
	 *
	 * @return 返回选择的尺寸，float型
	 */
	public float getSelectDimension() {
		int intPart = (int) intWvAdapter.getItem(intWv.getCurrentItem());
		int decimalPart = (int) decimalWvAdapter.getItem(decimalWv.getCurrentItem());
		float result = CommonUtils.roundFloat(intPart + (float) decimalPart / 10, 1);
		LogUtils.i(TAG, "选择的尺寸:" + result);
		return result;
	}


}
