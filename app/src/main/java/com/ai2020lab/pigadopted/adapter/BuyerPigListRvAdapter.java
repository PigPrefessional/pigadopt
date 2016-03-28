/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiviews.textview.RotateTextView;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.BaseApplication;
import com.ai2020lab.pigadopted.common.CommonUtils;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;
import com.ai2020lab.pigadopted.model.pig.PigStatus;

import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * 卖家猪列表RecyclerView适配器
 * Created by Justin Z on 2016/3/21.
 * 502953057@qq.com
 */
public class BuyerPigListRvAdapter extends BuyerPigListAdapter<BuyerPigListRvAdapter.ItemViewHolder> {
	private final static String TAG = BuyerPigListRvAdapter.class.getSimpleName();

	private LayoutInflater inflater;

	private Context context;

	private Drawable pigWalkingDrawable;
	private Drawable pigSleepingDrawable;
	private Drawable pigEatingDrawable;

	/**
	 * 构造方法
	 *
	 * @param context Context
	 */
	public BuyerPigListRvAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		initResources();
	}

	private void initResources() {
		pigWalkingDrawable = ResourcesUtils.getDrawable(R.mipmap.pig_status_walking_big);
		pigSleepingDrawable = ResourcesUtils.getDrawable(R.mipmap.pig_status_sleeping_big);
		pigEatingDrawable = ResourcesUtils.getDrawable(R.mipmap.pig_status_eating_big);
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LogUtils.i(TAG, "--onCreateViewHolder--");
		View view = inflater.inflate(R.layout.listitem_buyer_pig_list, parent, false);
		return new ItemViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ItemViewHolder holder, int position) {
		LogUtils.i(TAG, "--onBindViewHolder--");
		final PigDetailInfoAndOrder pigInfo = getItem(position);
		// 根据猪状态不同设置猪图片
		setBuyerPigIv(pigInfo, holder.buyerPigIv);
		// 设置体重
		setBuyerPigWeightTv(pigInfo, holder.buyerPigWeightTv);
		// 设置年龄
		setBuyerPigAgeTv(pigInfo, holder.buyerPigAgeTv);
		// 设置猪卖家名字
		setSellerNameTv(pigInfo, holder.sellerNameTv);
		// 设置绳子的隐藏状态
		setLineVisible(position, holder.lineLeftUpIv, holder.lineLeftDownIv,
				holder.lineRightUpIv, holder.lineRightDownIv);
	}

	/**
	 * 设置猪图片
	 */
	private void setBuyerPigIv(PigDetailInfoAndOrder pigInfo, ImageView buyerPigIv) {
		if (pigInfo.healthInfo == null) {
			LogUtils.i(TAG, "猪健康信息为空，设置默认图片为走路");
			buyerPigIv.setImageDrawable(pigWalkingDrawable);
			return;
		}
		int status = pigInfo.healthInfo.status;
		switch (status) {
			case PigStatus.WALKING:
				buyerPigIv.setImageDrawable(pigWalkingDrawable);
				break;
			case PigStatus.SLEEPING:
				buyerPigIv.setImageDrawable(pigSleepingDrawable);
				break;
			case PigStatus.EATING:
				buyerPigIv.setImageDrawable(pigEatingDrawable);
				break;
			default:
				buyerPigIv.setImageDrawable(pigWalkingDrawable);
				break;
		}
	}

	/**
	 * 设置猪当前猪龄
	 */
	private void setBuyerPigAgeTv(PigDetailInfoAndOrder pigInfo,
	                              TextView buyerPigAgeTv) {
		String ageMonth = context.getString(R.string.display_none1);
		String ageDay = context.getString(R.string.display_none1);
		if (pigInfo.pigInfo != null) {
			String ageArr[] = CommonUtils.getPigAgeMonthDay(CommonUtils
					.getPigAge(pigInfo.pigInfo.attendedTime, pigInfo.pigInfo.attendedAge));
			ageMonth = ageArr[0];
			ageDay = ageArr[1];
		}
		String ageStr = String.format(context.getString(R.string.pig_age),
				ageMonth, ageDay);
		SpannableString str = new SpannableString(ageStr);
		str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal_Light), 0, 2,
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		// 要重新设置一下字体样式，否则会使用系统默认字体
		CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(
				TypefaceUtils.load(context.getAssets(), BaseApplication.FONT_PATH));
		str.setSpan(typefaceSpan, 0, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		buyerPigAgeTv.setText(str);
	}

	/**
	 * 设置猪体重数据
	 */
	private void setBuyerPigWeightTv(PigDetailInfoAndOrder pigInfo,
	                                 TextView buyerPigWeightTv) {
		String weight = context.getString(R.string.display_none);
		if (pigInfo.growthInfo != null) {
			weight = pigInfo.growthInfo.pigWeight + "";
		}
		String weightStr = String.format(context.getString(R.string.pig_weight),
				weight);
		SpannableString str = new SpannableString(weightStr);
		str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal_Light), 0, 2,
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(
				TypefaceUtils.load(context.getAssets(), BaseApplication.FONT_PATH));
		str.setSpan(typefaceSpan, 0, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		buyerPigWeightTv.setText(str);
	}

	/**
	 * 设置猪卖家名字
	 */
	private void setSellerNameTv(PigDetailInfoAndOrder pigInfo,
	                             RotateTextView sellerNameTv) {
		String name = context.getString(R.string.display_none);
		if (pigInfo.pigInfo != null && pigInfo.pigInfo.hogpenInfo != null &&
				pigInfo.pigInfo.hogpenInfo.userInfo != null) {
			name = pigInfo.pigInfo.hogpenInfo.userInfo.userName;
		}
		sellerNameTv.setText(name);
		sellerNameTv.getPaint().setFakeBoldText(true);
	}

	private void setLineVisible(int position, ImageView lineLeftUpIv,
	                            ImageView lineLeftDownIv, ImageView lineRightUpIv,
	                            ImageView lineRightDownIv) {
		// 第一条数据，隐藏左上和右上的绳子
		if (position == 0) {
			lineLeftUpIv.setVisibility(View.VISIBLE);
			lineRightUpIv.setVisibility(View.VISIBLE);
			lineLeftDownIv.setVisibility(View.VISIBLE);
			lineRightDownIv.setVisibility(View.VISIBLE);
		}
		// 最后一条数据，隐藏左下和右下的绳子
		else if (position == getItemCount() - 1) {
			lineLeftUpIv.setVisibility(View.VISIBLE);
			lineRightUpIv.setVisibility(View.VISIBLE);
			lineLeftDownIv.setVisibility(View.GONE);
			lineRightDownIv.setVisibility(View.GONE);
		} else {
			lineLeftUpIv.setVisibility(View.VISIBLE);
			lineRightUpIv.setVisibility(View.VISIBLE);
			lineLeftDownIv.setVisibility(View.VISIBLE);
			lineRightDownIv.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * ViewHolder内部类
	 */
	public static class ItemViewHolder extends RecyclerView.ViewHolder {
		ImageView buyerPigIv;
		LinearLayout buyerPigInfoRl;
		TextView buyerPigAgeTv;
		TextView buyerPigWeightTv;
		RotateTextView sellerNameTv;
		ImageView lineLeftUpIv;
		ImageView lineLeftDownIv;
		ImageView lineRightUpIv;
		ImageView lineRightDownIv;

		ItemViewHolder(View view) {
			super(view);
			buyerPigIv = (ImageView) view.findViewById(R.id.buyer_pig_iv);
			buyerPigInfoRl = (LinearLayout) view.findViewById(R.id.buyer_pig_info_rl);
			buyerPigAgeTv = (TextView) view.findViewById(R.id.buyer_pig_age_tv);
			buyerPigWeightTv = (TextView) view.findViewById(R.id.buyer_pig_weight_tv);
			sellerNameTv = (RotateTextView) view.findViewById(R.id.seller_name_tv);
			lineLeftUpIv = (ImageView) view.findViewById(R.id.line_left_up_iv);
			lineLeftDownIv = (ImageView) view.findViewById(R.id.line_left_down_iv);
			lineRightUpIv = (ImageView) view.findViewById(R.id.line_right_up_iv);
			lineRightDownIv = (ImageView) view.findViewById(R.id.line_right_down_iv);
		}
	}


}
