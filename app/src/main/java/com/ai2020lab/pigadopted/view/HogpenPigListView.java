/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.anim.AnimSimpleListener;
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
// TODO:需要将View和数据分离？？？定义Adapter
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
	/**
	 * 添加猪动画完成事件监听接口
	 */
	private OnAddPigAnimListener onAddPigAnimListener;


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
	 * 绑定添加猪动画事件监听
	 *
	 * @param onAddPigAnimListener OnAddPigAnimListener
	 */
	public void setOnAddPigAnimListener(OnAddPigAnimListener onAddPigAnimListener) {
		this.onAddPigAnimListener = onAddPigAnimListener;
	}

	/**
	 * 初始化设置猪列表数据<p>
	 * 调用这个方法初始化数据
	 */
	public void setPigs(List<PigDetailInfoForSeller> pigInfos, boolean isLoadAnim) {
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
			addPig(pigInfos.get(i), isLoadAnim);
		}
	}

	/**
	 * 添加猪<p>
	 * 调用这个方法添加猪
	 */
	public void addPig(PigDetailInfoForSeller pigInfo, boolean isLoadAnim) {
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
		// 加入猪列表数据
		pigInfos.add(pigInfo);
		// 加入界面元素
		hogpenContainer.addView(pigInfoView);
		// 加载猪飞入动画
		// TODO:需要增加标志位，PigList不在屏幕上显示的时候不加载动画
		if (isLoadAnim) {
			loadPigInAnim(pigInfoView, size > 0 ? size : 0);
		} else {
			pigInfoView.findViewById(R.id.pig_info_rl).setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 根据添加猪的位置下标，返回猪信息布局View
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
	 * 根据添加猪的位置下标载入猪的飞入动画
	 */
	private void loadPigInAnim(View view, int index) {
		final RelativeLayout pigInfoLayout = (RelativeLayout) view.findViewById(R.id.pig_info_rl);
		if (index % 2 != 0) {
			LogUtils.i(TAG, "猪从左边飞入的动画");
			Animation anim = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
			view.startAnimation(anim);
			anim.setAnimationListener(new AnimSimpleListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					loadPigInfoInAnim(pigInfoLayout, true);
				}
			});
		} else {
			LogUtils.i(TAG, "猪从右边飞入的动画");
			Animation anim = AnimationUtils.loadAnimation(context, R.anim.push_right_in);
			view.startAnimation(anim);
			anim.setAnimationListener(new AnimSimpleListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					loadPigInfoInAnim(pigInfoLayout, false);
				}
			});
		}
	}

	// 猪信息进入属性动画
	private void loadPigInfoInAnim(View view, boolean isLeft) {
		if (view == null) {
			LogUtils.i(TAG, "没有找到执行缩放动画的猪信息布局");
			return;
		}
		Animation anim;
		if (isLeft) {
			anim = AnimationUtils.loadAnimation(context, R.anim.scale_left_down_in);
		} else {
			anim = AnimationUtils.loadAnimation(context, R.anim.scale_right_down_in);
		}
		anim.setInterpolator(new AnticipateOvershootInterpolator());
		view.setVisibility(View.VISIBLE);
		view.startAnimation(anim);
		anim.setAnimationListener(new AnimSimpleListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				//TODO：猪添加动画完成事件监听
				if (onAddPigAnimListener != null)
					onAddPigAnimListener.onEnd();
			}
		});
	}

	/**
	 * 设置猪信息
	 */
	private void setPigInfo(View pigInfoView, final PigDetailInfoForSeller pigInfo) {
		LogUtils.i(TAG, "设置添加的猪信息");
		ImageView pigIv = (ImageView) pigInfoView.findViewById(R.id.pig_iv);
		TextView pigWeightTv = (TextView) pigInfoView.findViewById(R.id.pig_weight_tv);
		TextView pigTemperatureTv = (TextView) pigInfoView.findViewById(R.id.pig_temperature_tv);
		TextView pigBuyersTv = (TextView) pigInfoView.findViewById(R.id.pig_buyers_tv);
		// 设置猪的图片样式，可能是吃饭，散步，睡觉
		pigIv.setBackgroundDrawable(getPigDrawable(pigInfo));
		// 设置体重
		pigWeightTv.setText(getWeightStr(pigInfo));
		// 设置体温
		pigTemperatureTv.setText(getTemperatureStr(pigInfo));
		// 设置买家数量
		pigBuyersTv.setText(getBuyerNumStr(pigInfo));
		// 绑定点击Item事件
		pigInfoView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.i(TAG, "点击了猪列表item");
				if (onClickPigItemListener != null) {
					LogUtils.i(TAG, "猪项点击事件");
					onClickPigItemListener.onClickPigItem(pigInfo);
				}
			}
		});

	}

	/**
	 * 猪体重
	 */
	private SpannableString getWeightStr(PigDetailInfoForSeller pigInfo) {
		String weight = "--";
		if (pigInfo.growthInfo != null && pigInfo.growthInfo.pigWeight > 0) {
			weight = pigInfo.growthInfo.pigWeight + "";
		}
		String weightStr = String.format(context.getString(R.string.seller_main_pig_weight),
				weight);
		SpannableString str = new SpannableString(weightStr);
		str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal_Light), 0, 2,
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	/**
	 * 猪体温
	 */
	private SpannableString getTemperatureStr(PigDetailInfoForSeller pigInfo) {
		String tem = "--";
		if (pigInfo.healthInfo != null && pigInfo.healthInfo.temperature > 0) {
			tem = pigInfo.healthInfo.temperature + "";
		}
		String temStr = String.format(context.getString(R.string.seller_main_pig_temperature),
				tem);
		SpannableString str = new SpannableString(temStr);
		str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal_Light), 0, 2,
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	/**
	 * 买家数量
	 */
	private SpannableString getBuyerNumStr(PigDetailInfoForSeller pigInfo) {
		int buyerNum = 0;
		if (pigInfo.orderInfo != null) {
			buyerNum = pigInfo.orderInfo.buyerNumber;
		}
		SpannableString str = new SpannableString(String.format(context
				.getString(R.string.seller_main_pig_buyers_number), buyerNum));
		if (buyerNum < 10) {
			LogUtils.i(TAG, "使用购买者数小于10的策略");
			str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal), 2, 3,
					Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		} else if (buyerNum >= 10 && buyerNum < 100) {
			LogUtils.i(TAG, "使用购买者数大于10小于100的策略");
			str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal), 2, 4,
					Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		// TODO:不考虑单个猪的买家超过1000个人的情况
		else if (buyerNum >= 100 && buyerNum < 1000) {
			LogUtils.i(TAG, "使用购买者数大于100小于1000的策略");
			str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal), 2, 5,
					Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		return str;
	}

	/**
	 * 根据猪状态获取猪的Drawable资源
	 *
	 * @param pigInfo PigDetailInfoForSeller
	 */
	private Drawable getPigDrawable(PigDetailInfoForSeller pigInfo) {
		int status = pigInfo.healthInfo != null ? pigInfo.healthInfo.status : PigStatus.WALKING;
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

	/**
	 * 添加猪动画执行完成事件监听接口
	 */
	public interface OnAddPigAnimListener {
		void onEnd();
	}


}
