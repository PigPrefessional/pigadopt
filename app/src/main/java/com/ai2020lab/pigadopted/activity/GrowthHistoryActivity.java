/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.BounceInterpolator;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.adapter.GrowthHistoryRvAdapter;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.model.pig.GrowthInfo;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

/**
 * 成长历程Activity
 * Created by Justin Z on 2016/3/28.
 * 502953057@qq.com
 */
public class GrowthHistoryActivity extends AIBaseActivity {

	private final static String TAG = GrowthHistoryActivity.class.getSimpleName();
	/**
	 * 成长历程RecyclerView
	 */
	private RecyclerView growthHistoryRv;

	private GrowthHistoryRvAdapter growthHistoryRvAdapter;

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

	private void setToolbar() {
		supportToolbar(true);
		setToolbarTitle(getString(R.string.activity_title_growth_history));
		setToolbarLeft(R.drawable.toolbar_back_selector);
		setToolbarRight(R.drawable.growth_history_camera_selector);
		// 返回按钮监听
		setOnLeftClickListener(new OnLeftClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});
		// 拍照按钮监听
		setOnRightClickListener(new OnRightClickListener() {
			@Override
			public void onClick() {
				skipToDistanceCameraActivity();
			}
		});
	}

	private void setGrowthHistoryRv() {
		growthHistoryRvAdapter = new GrowthHistoryRvAdapter(this);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		growthHistoryRv.setLayoutManager(layoutManager);
		// 加入分组头部
		final StickyRecyclerHeadersDecoration headersDecor = new
				StickyRecyclerHeadersDecoration(growthHistoryRvAdapter);
		growthHistoryRv.addItemDecoration(headersDecor);
		growthHistoryRv.setAdapter(growthHistoryRvAdapter);
		// 加入item动画效果
//		LandingAnimator animator = new LandingAnimator();
		SlideInDownAnimator animator = new SlideInDownAnimator();
		animator.setAddDuration(500);
		animator.setInterpolator(new BounceInterpolator());
		growthHistoryRv.setItemAnimator(animator);
	}

	/**
	 * 猪成长记录拍照
	 */
	private void skipToDistanceCameraActivity() {
		Intent intent = new Intent(this, DistanceCameraActivity.class);
		startActivity(intent);
	}

	private void loadData() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				loadTestListData();

			}
		}, 1000);
	}

	// TODO:加载测试数据
	private void loadTestListData() {
		List<GrowthInfo> growthInfos = getTestListData();
		sortList(growthInfos);
		int size = growthInfos.size();
		for (int i = 0; i < size; i++) {
			GrowthInfo growthInfo = growthInfos.get(i);
			// 采用逐项添加的方式才能触发动画
			this.growthHistoryRvAdapter.add(i, growthInfo);
		}
	}

	private List<GrowthInfo> getTestListData() {
		List<GrowthInfo> growthInfos = new ArrayList<>();
		GrowthInfo growthInfo;

		growthInfo = new GrowthInfo();
		growthInfo.increasedWeight = 10;
		growthInfo.pigWeight = 30;
		growthInfo.collectedTime = 1445508052553l;
		growthInfo.pigPhoto = "http://a4.att.hudong.com/13/53/01300000167984122578538815071.jpg";
		growthInfos.add(growthInfo);
		//////////////////////////////////////////
		growthInfo = new GrowthInfo();
		growthInfo.increasedWeight = 20;
		growthInfo.pigWeight = 40;
		growthInfo.collectedTime = 1446372052553l;
		growthInfo.pigPhoto = "http://pic17.nipic.com/20110821/8203958_145711321167_2.jpg";
		growthInfos.add(growthInfo);
		//////////////////////////////////////////
		growthInfo = new GrowthInfo();
		growthInfo.increasedWeight = 30;
		growthInfo.pigWeight = 80;
		growthInfo.collectedTime = 1451642452553l;
		growthInfo.pigPhoto = "http://images.quanjing.com/sps032/high/sps255-30779.jpg";
		growthInfos.add(growthInfo);
		//////////////////////////////////////////
		growthInfo = new GrowthInfo();
		growthInfo.increasedWeight = 10;
		growthInfo.pigWeight = 90;
		growthInfo.collectedTime = 1445508052553l;
		growthInfo.pigPhoto = "http://file.youboy.com/a/93/86/55/5/10297165.jpg";
		growthInfos.add(growthInfo);
		//////////////////////////////////////////
		growthInfo = new GrowthInfo();
		growthInfo.increasedWeight = 10;
		growthInfo.pigWeight = 110;
		growthInfo.collectedTime = 1459155690552l;
		growthInfo.pigPhoto = "http://a3.att.hudong.com/21/63/20300001248577133465636942467.jpg";
		growthInfos.add(growthInfo);

		return growthInfos;
	}

	// 倒序排列
	private void sortList(List<GrowthInfo> growthInfos) {
		if (growthInfos != null) {
			Collections.sort(growthInfos, new Comparator<GrowthInfo>() {
				@Override
				public int compare(GrowthInfo info1, GrowthInfo info2) {
					if (info1.collectedTime < info2.collectedTime) {
						return 1;
					} else if (info1.collectedTime > info2.collectedTime) {
						return -1;
					}
					return 0;
				}
			});
		}
	}


}
