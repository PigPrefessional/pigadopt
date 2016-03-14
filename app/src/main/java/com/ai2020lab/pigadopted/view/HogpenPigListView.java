/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoForSeller;
import com.ai2020lab.pigadopted.model.pig.PigStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 猪圈容器自定义View<p>
 * 猪按从上到下依次添加
 * Created by Justin Z on 2016/3/14.
 * 502953057@qq.com
 */
public class HogpenPigListView extends LinearLayout {

	private final static String TAG = HogpenPigListView.class.getSimpleName();
	/**
	 * 上下文引用
	 */
	private Context context;
	/**
	 * 猪容器
	 */
	private LinearLayout hogpenContainer;
	/**
	 * 卖家猪详情列表数据
	 */
	private List<PigDetailInfoForSeller> pigInfos = new ArrayList<>();
	/**
	 * 猪详情Item点击事件接口
	 */
	private onClickPigItemListener onClickPigItemListener;


	public HogpenPigListView(Context context) {
		super(context);
		init(context);
	}

	public HogpenPigListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HogpenPigListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	//初始化方法
	private void init(Context context) {
		this.context = context;
		ViewUtils.makeView(context, R.layout.hogpen_container, this, true);
		hogpenContainer = (LinearLayout) findViewById(R.id.hogpen_ll);
	}

	/**
	 * 获取猪总数
	 */
	public int getPigNumber() {
		return pigInfos.size();
	}

	/**
	 * 绑定点击猪信息Item事件监听
	 *
	 * @param onClickPigItemListener onClickPigItemListener
	 */
	public void setOnClickPigItemListener(onClickPigItemListener onClickPigItemListener) {
		this.onClickPigItemListener = onClickPigItemListener;
	}

	/**
	 * 初始化设置猪列表数据<p>
	 * 调用这个方法初始化数据
	 */
	public void setPigs(List<PigDetailInfoForSeller> pigInfos) {
		if (pigInfos == null || pigInfos.size() == 0) {
			LogUtils.i(TAG, "没有猪列表数据，不刷新界面");
			return;
		}
		if (pigInfos.size() > HogpenViewPager.PIG_LIMIT) {
			LogUtils.i(TAG, "猪个数超过最大限制，不刷新界面");
			return;
		}
		this.pigInfos.clear();
		for (int i = 0; i < pigInfos.size(); i++) {
			addPig(pigInfos.get(i));
		}
	}

	/**
	 * 添加猪<p>
	 * 调用这个方法添加猪
	 */
	public void addPig(PigDetailInfoForSeller pigInfo) {
		if (pigInfo == null) {
			LogUtils.i(TAG, "要添加的猪信息不能为空");
			return;
		}
		int size = pigInfos.size();
		if (size == HogpenViewPager.PIG_LIMIT) {
			LogUtils.i(TAG, "同一个猪圈中的猪不能超过上限:" + HogpenViewPager.PIG_LIMIT);
			return;
		}
		View pigInfoView = getPigInfoView(size > 0 ? size : 0);
		// 设置界面数据
		setPigInfo(pigInfoView, pigInfo);
		hogpenContainer.addView(pigInfoView);
		// 加入猪列表数据
		pigInfos.add(pigInfo);
		// TODO:执行添加动画
	}

	/**
	 * 根据添加的猪信息的位置，返回猪信息布局View
	 *
	 * @param index 要添加到的位置
	 * @return View
	 */
	private View getPigInfoView(int index) {
		View view;
		if (index % 2 != 0) {
			LogUtils.i(TAG, "猪在左边的布局");
			view = ViewUtils.makeView(context, R.layout.hogpen_pig_left);
		} else {
			LogUtils.i(TAG, "猪在右边的布局");
			view = ViewUtils.makeView(context, R.layout.hogpen_pig_right);
		}
		return view;
	}

	/**
	 * 设置猪信息
	 */
	private void setPigInfo(View pigInfoView, final PigDetailInfoForSeller pigInfo) {
		// 图片,可能是吃饭，散步，睡觉
		ImageView pigIv = (ImageView) pigInfoView.findViewById(R.id.pig_iv);
		// 体重
		TextView pigWeightTv = (TextView) pigInfoView.findViewById(R.id.pig_weight_tv);
		// 体温
		TextView pigTemperatureTv = (TextView) pigInfoView.findViewById(R.id.pig_temperature_tv);
		// 买家总数
		TextView pigBuyersTv = (TextView) pigInfoView.findViewById(R.id.pig_buyers_tv);
		if (pigInfo.growthInfo != null) {
			String weight = String.format(context.getString(R.string.seller_main_pig_weight),
					pigInfo.growthInfo.pigWeight);
			pigWeightTv.setText(weight);
		}
		if (pigInfo.healthInfo != null) {
			pigIv.setBackgroundDrawable(getPigDrawable(pigInfo.healthInfo.status));
			String temperature = String.format(context.getString(R.string.seller_main_pig_temperature),
					pigInfo.healthInfo.temperature);
			pigTemperatureTv.setText(temperature);
		}
		if (pigInfo.orderInfo != null) {
			String buyers = String.format(context.getString(R.string.seller_main_pig_buyers_number),
					pigInfo.orderInfo.buyerNums);
			pigBuyersTv.setText(buyers);
		}
		// 绑定点击事件
		pigInfoView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onClickPigItemListener != null)
					onClickPigItemListener.onClickPigItem(pigInfo);
			}
		});

	}

	/**
	 * 根据猪状态获取猪的Drawable资源
	 *
	 * @param status 猪状态
	 */
	private Drawable getPigDrawable(int status) {
		switch (status) {
			case PigStatus.WALKING:
				return ResourcesUtils.getDrawable(R.mipmap.pig_status_walking);
			case PigStatus.SLEEPING:
				return ResourcesUtils.getDrawable(R.mipmap.pig_status_sleeping);
			case PigStatus.EATING:
				return ResourcesUtils.getDrawable(R.mipmap.pig_status_eating);
			default:
				return ResourcesUtils.getDrawable(R.mipmap.pig_status_walking);
		}
	}

	/**
	 * 猪详情Item点击事件监听接口
	 */
	public interface onClickPigItemListener {

		void onClickPigItem(PigDetailInfoForSeller pigInfo);
	}


}
