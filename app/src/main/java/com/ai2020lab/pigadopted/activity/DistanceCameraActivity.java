/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;

/**
 * 定距离拍照界面
 * Created by Justin Z on 2016/3/28.
 * 502953057@qq.com
 */
public class DistanceCameraActivity extends AIBaseActivity {
	private final static String TAG = DistanceCameraActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		userInfo = (UserInfo) getIntent().getExtras().get(IntentExtra.USER_INFO);
		setContentView(R.layout.activity_distance_camera);
		setToolbar();
//		assignViews();
//		setGrowthHistoryRv();
//		loadData();
	}

	private void setToolbar(){
		supportToolbar(true);
		setToolbarTitle(getString(R.string.activity_distance_camera));
		setToolbarLeft(R.drawable.toolbar_back_selector);
		// 返回按钮监听
		setOnLeftClickListener(new OnLeftClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});

	}
}
