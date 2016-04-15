/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.TimeUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.ai2020lab.aiviews.wheelview.WheelView;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.CommonUtils;
import com.ai2020lab.pigadopted.common.DataManager;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.model.pig.GrowthInfo;
import com.ai2020lab.pigadopted.model.pig.PigAddRequest;
import com.ai2020lab.pigadopted.model.pig.PigAddResponse;
import com.ai2020lab.pigadopted.model.pig.PigCategory;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;
import com.ai2020lab.pigadopted.view.pickerview.DatePickerView;
import com.ai2020lab.pigadopted.view.pickerview.PigAgePickerView;
import com.ai2020lab.pigadopted.view.pickerview.PigWeightPickerView;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * 添加猪界面
 * Created by Justin Z on 2016/4/15.
 * 502953057@qq.com
 */
public class AddPigActivity extends AIBaseActivity {

	/**
	 * 日志标题
	 */
	private final static String TAG = BuyerMainActivity.class.getSimpleName();


	private TextView pigAddCategoryTv;
	private WheelView pigAddCategoryWv;
	private TextView pigAddAgeTv;
	private PigAgePickerView pigAddAgePv;
	private TextView pigAddWeightTv;
	private PigWeightPickerView pigAddWeightPv;
	private TextView pigAddTimeTv;
	private DatePickerView pigAddDatePv;

	private ArrayWheelAdapter<PigCategory> pigCategoryWvAdapter;

	private PigInfo piginfo;

	/**
	 * 入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_pig);
		setToolbar();
		assignViews();
		setTextFonts();
		setPigAddCategoryWv();
		setPigAddDatePv();
	}

	private void setToolbar() {
		supportToolbar(true);
		setToolbarTitle(getString(R.string.activity_title_add_pig));
		setToolbarLeft(R.drawable.toolbar_back_selector);
		setToolbarRight(getString(R.string.ensure));
		setOnLeftClickListener(new OnLeftClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});
		setOnRightClickListener(new OnRightClickListener() {
			@Override
			public void onClick() {
				piginfo = getSelectPigInfo();
				// 确定发起添加猪请求
				requestAddPig(piginfo);
			}
		});
	}

	private void assignViews() {
		pigAddCategoryTv = (TextView) findViewById(R.id.pig_add_category_tv);
		pigAddCategoryWv = (WheelView) findViewById(R.id.pig_add_category_wv);
		pigAddAgeTv = (TextView) findViewById(R.id.pig_add_age_tv);
		pigAddAgePv = (PigAgePickerView) findViewById(R.id.pig_add_age_pv);
		pigAddWeightTv = (TextView) findViewById(R.id.pig_add_weight_tv);
		pigAddWeightPv = (PigWeightPickerView) findViewById(R.id.pig_add_weight_pv);
		pigAddTimeTv = (TextView) findViewById(R.id.pig_add_time_tv);
		pigAddDatePv = (DatePickerView) findViewById(R.id.pig_add_date_pv);
	}

	/**
	 * 设置字体
	 */
	private void setTextFonts() {
		// 标题和按钮文字全部使用中文粗体
		pigAddCategoryTv.getPaint().setFakeBoldText(true);
		pigAddAgeTv.getPaint().setFakeBoldText(true);
		pigAddWeightTv.getPaint().setFakeBoldText(true);
		pigAddTimeTv.getPaint().setFakeBoldText(true);
	}

	/**
	 * 设置猪品种选择滚轮
	 */
	private void setPigAddCategoryWv() {
		ArrayList<PigCategory> pigCategories = DataManager.getInstance().getPigCategories();
		pigCategoryWvAdapter = new ArrayWheelAdapter<>(pigCategories, 2);
		pigAddCategoryWv.setAdapter(pigCategoryWvAdapter);
		// 设置选中第一项
		pigAddCategoryWv.setCurrentItem(0);
	}

	/**
	 * 设置时间选择为当前时间
	 */
	private void setPigAddDatePv() {
		int year = CommonUtils.getCurrentYear();
		int month = CommonUtils.getCurrentMonth();
		int day = CommonUtils.getCurrentDay();
//		LogUtils.i(TAG, "年份:" + year);
//		LogUtils.i(TAG, "月份:" + month);
//		LogUtils.i(TAG, "日期:" + day);
		pigAddDatePv.setPickerView(year, month, day);
	}

	/**
	 * 返回添加的猪信息对象
	 */
	private PigInfo getSelectPigInfo() {
		PigInfo pigInfo = new PigInfo();
		// 猪品种
		pigInfo.pigCategory = (PigCategory) pigCategoryWvAdapter
				.getItem(pigAddCategoryWv.getCurrentItem());
		// 入栏猪龄
		pigInfo.attendedAge = pigAddAgePv.getSelectPigAge();
		// 入栏体重
		pigInfo.attendedWeight = pigAddWeightPv.getSelectPigWeight();
		// 入栏时间
		pigInfo.attendedDate = pigAddDatePv.getSelectTime();
		pigInfo.attendedTime = TimeUtils.dateToTimeStamp(pigInfo.attendedDate,
				TimeUtils.Template.YMD);
		LogUtils.i(TAG, "入栏品种:" + pigInfo.pigCategory.toString());
		LogUtils.i(TAG, "入栏猪龄:" + pigInfo.attendedAge);
		LogUtils.i(TAG, "入栏体重:" + pigInfo.attendedWeight);
		LogUtils.i(TAG, "入栏时间:" + pigInfo.attendedDate);
		LogUtils.i(TAG, "入栏时间戳:" + pigInfo.attendedTime);
		LogUtils.i(TAG, "转换回来:" + TimeUtils.formatTimeStamp(pigInfo.attendedTime,
				TimeUtils.Template.YMD));
		return pigInfo;
	}

	/**
	 * 发起添加猪请求
	 */
	private void requestAddPig(final PigInfo pigInfo) {
		LogUtils.i(TAG, "--发起添加猪请求--");
		// 弹出提示
		showLoading(getString(R.string.prompt_add_loading));
		PigAddRequest data = new PigAddRequest();
		data.categoryID = pigInfo.pigCategory.categoryID;
		data.hogpenID = getIntent().getIntExtra(IntentExtra.HOGPEN_ID, -1);
		// 时间戳除以1000，服务端长度溢出
		data.attendedTime = pigInfo.attendedTime / 1000l;
		data.attendedWeight = pigInfo.attendedWeight;
		data.attendedAge = pigInfo.attendedAge;
		HttpManager.postJson(this, UrlName.ADD_PIG.getUrl(), data,
				new JsonHttpResponseHandler<PigAddResponse>(this) {
					/**
					 * 成功回调
					 *
					 * @param statusCode 状态码
					 * @param headers    Header
					 * @param jsonObj    服务端返回的对象
					 */
					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            final PigAddResponse jsonObj) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								finishActivity(pigInfo);
							}
						}, 1000);

					}

					@Override
					public void onCancel() {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								// 没有网络的情况会终止请求
								ToastUtils.getInstance().showToast(getActivity(),
										R.string.prompt_add_pig_failure);
							}
						}, 1000);
					}

					@Override
					public void onHandleFailure(String errorMsg) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								ToastUtils.getInstance().showToast(getActivity(),
										R.string.prompt_add_pig_failure);
							}
						}, 1000);
					}
				});
	}

	/**
	 * 返回添加猪测试数据
	 */
	private PigDetailInfoAndOrder getAddPigInfoAndOrder(PigInfo pigInfo) {
		PigDetailInfoAndOrder pigInfoAndOrder = new PigDetailInfoAndOrder();
//		pigInfoAndOrder.orderInfo = new OrderInfoForSeller();
//		pigInfoAndOrder.orderInfo.buyerNumber = 0;
//		pigInfoAndOrder.healthInfo = new HealthInfo();
//		pigInfoAndOrder.healthInfo.temperature = 36.5f;
//		pigInfoAndOrder.healthInfo.status = PigStatus.WALKING;
		pigInfoAndOrder.growthInfo = new GrowthInfo();
		pigInfoAndOrder.growthInfo.pigWeight = pigInfo.attendedWeight;
		pigInfoAndOrder.pigInfo = pigInfo;
		return pigInfoAndOrder;
	}

	/**
	 * 将添加的猪数据返回给卖家主页
	 */
	private void finishActivity(PigInfo pigInfo){
		PigDetailInfoAndOrder pigInfoAndOrder = getAddPigInfoAndOrder(pigInfo);
		Intent intent = new Intent();
		intent.putExtra(IntentExtra.PIG_INFO_AND_ORDER, pigInfoAndOrder);
		setResult(RESULT_OK, intent);
		finish();
	}


}
