/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;

/**
 * 成长历程Activity
 * Created by Justin Z on 2016/3/28.
 * 502953057@qq.com
 */
public class GrowthHistoryActivity extends AIBaseActivity {

	private final static String TAG = GrowthHistoryActivity.class.getSimpleName();

	private RecyclerView growthHistoryRv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		userInfo = (UserInfo) getIntent().getExtras().get(IntentExtra.USER_INFO);
		setContentView(R.layout.activity_growth_history);
		setToolbar();
		assignViews();
		setGrowthHistoryRv();
		loadData();
	}

	private void assignViews() {
		growthHistoryRv = (RecyclerView) findViewById(R.id.growth_history_rv);
	}

	private void setToolbar(){
		supportToolbar(true);
		setToolbarTitle(getString(R.string.activity_title_growth_history));
		setToolbarLeft(R.drawable.toolbar_back_selector);
		setToolbarRight(R.drawable.growth_history_camera_selector);
	}

	private void setGrowthHistoryRv() {
	}

	private void loadData() {
	}


}
