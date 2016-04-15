/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.animation.BounceInterpolator;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.adapter.GrowthHistoryRvAdapter;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.model.base.PageSplitInfo;
import com.ai2020lab.pigadopted.model.pig.GrowthInfo;
import com.ai2020lab.pigadopted.model.pig.GrowthInfoRequest;
import com.ai2020lab.pigadopted.model.pig.GrowthInfoResponse;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
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

	private PigInfo pigInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pigInfo = (PigInfo) getIntent().getSerializableExtra(IntentExtra.PIG_INFO);
		setContentView(R.layout.activity_growth_history);
		setToolbar();
		assignViews();
		setGrowthHistoryRv();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "--onResume--");
		queryCustomerPigList();
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

	/**
	 * 设置生长记录RecyclerView
	 */
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
	 * 跳转到猪成长记录拍照上传界面
	 */
	private void skipToDistanceCameraActivity() {
		Intent intent = new Intent(this, DistanceCameraActivity.class);
		intent.putExtra(IntentExtra.PIG_INFO, pigInfo);
		startActivity(intent);
	}

	/**
	 * 请求生长记录列表
	 */
	// TODO:还要加上下拉刷新和分页加载效果
	private void queryCustomerPigList() {
		LogUtils.i(TAG, "--查询买家猪列表--");
		// 弹出提示
		showLoading(getString(R.string.prompt_loading));
		GrowthInfoRequest data = new GrowthInfoRequest();
		data.pigID = pigInfo.pigID;
		data.pageSplitInfo = new PageSplitInfo();
		data.pageSplitInfo.startIndex = 1;
		data.pageSplitInfo.dataNum = 20;
		HttpManager.postJson(this, UrlName.GROWTH_INFO_LIST.getUrl(), data,
				new JsonHttpResponseHandler<GrowthInfoResponse>(this) {

					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            final GrowthInfoResponse jsonObj) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
//								growthHistoryRv.smoothScrollToPosition(0);
								growthHistoryRvAdapter.clear();
								List<GrowthInfo> growthInfos = jsonObj.data.growthInfos;
								int size = growthInfos.size();
								if (size > 0) {
									// 排序
									sortList(growthInfos);
									for (int i = 0; i < size; i++) {
										GrowthInfo growthInfo = growthInfos.get(i);
										growthHistoryRvAdapter.add(i, growthInfo);
									}
								} else {
									ToastUtils.getInstance().showToast(getActivity(),
											R.string.prompt_none_growth_infos);
								}
							}
						}, 1000);
					}

					@Override
					public void onCancel() {
						dismissLoading();
						// 没有网络的情况会终止请求
						ToastUtils.getInstance().showToast(getActivity(),
								R.string.prompt_loading_failure);
					}

					@Override
					public void onHandleFailure(String errorMsg) {
						dismissLoading();
						ToastUtils.getInstance().showToast(getActivity(),
								R.string.prompt_loading_failure);
					}

				});

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
