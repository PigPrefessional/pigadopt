/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.view.CropperView;

/**
 * Created by Justin Z on 2016/4/18.
 * 502953057@qq.com
 */
public class TestTouchScaleActivity extends Activity {

	/**
	 * 日志标题
	 */
	private final static String TAG = TestTouchScaleActivity.class.getSimpleName();

	private CropperView touchScaleView;

	/**
	 * 入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_touch_scale);
		touchScaleView = (CropperView) findViewById(R.id.test_tsv);
		touchScaleView.setStrokeColor(Color.WHITE);
		touchScaleView.setStrokeWidth(2f);
	}


}
