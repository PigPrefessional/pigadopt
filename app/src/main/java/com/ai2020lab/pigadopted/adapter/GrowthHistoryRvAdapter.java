/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.TimeUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.anim.AnimationImageLoadingListener;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.BaseApplication;
import com.ai2020lab.pigadopted.common.CommonUtils;
import com.ai2020lab.pigadopted.common.ImageLoaderManager;
import com.ai2020lab.pigadopted.model.pig.GrowthInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * 成长记录适配器
 * Created by Justin Z on 2016/3/28.
 * 502953057@qq.com
 */
public class GrowthHistoryRvAdapter extends
		GrowthHistoryAdapter<GrowthHistoryRvAdapter.ItemViewHolder>
		implements StickyRecyclerHeadersAdapter<GrowthHistoryRvAdapter.HeaderViewHolder> {

	private final static String TAG = GrowthHistoryRvAdapter.class.getSimpleName();

	private Context context;

	/**
	 * 构造方法
	 *
	 * @param context Context
	 */
	public GrowthHistoryRvAdapter(Context context) {
		this.context = context;
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LogUtils.i(TAG, "--onCreateViewHolder--");
		View view = ViewUtils.makeView(context, R.layout.listitem_growth_history, parent, false);
		return new ItemViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ItemViewHolder holder, int position) {
		LogUtils.i(TAG, "--onBindViewHolder--");
		GrowthInfo growthInfo = getItem(position);
		// 加载网络图片
		ImageLoader.getInstance().displayImage(growthInfo.pigPhoto, holder.pigPhotoRiv,
				ImageLoaderManager.getImageOptions(context),
				new AnimationImageLoadingListener());
		// 设置时间
		holder.dateTv.setText(getUploadDateStr(growthInfo));
		// 设置体重
		holder.weightTv.setText(getWeightStr(growthInfo));
		// 设置增长体重
		holder.weightIncreaseTv.setText(getIncreasedStr(growthInfo));
	}

	/**
	 * 照片上传时间显示字符窜
	 */
	private SpannableString getUploadDateStr(GrowthInfo growthInfo) {
		String date = context.getString(R.string.display_none);
		String tempDate = null;
		if (growthInfo != null && growthInfo.collectedTime > 0) {
			tempDate = TimeUtils.formatTimeStamp(growthInfo.collectedTime, TimeUtils.Template.YMD);
		}
		date = TextUtils.isEmpty(tempDate) ? date : tempDate;
		String weightStr = String.format(context.getString(R.string.pig_photo_date),
				date);
		SpannableString str = new SpannableString(weightStr);
		str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal_Light), 0, 2,
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		// 要重新设置一下字体样式，否则会使用系统默认字体
		CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(
				TypefaceUtils.load(context.getAssets(), BaseApplication.FONT_PATH));
		str.setSpan(typefaceSpan, 0, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}


	/**
	 * 猪体重显示字符窜
	 */
	private SpannableString getWeightStr(GrowthInfo growthInfo) {
		String weight = context.getString(R.string.display_none);
		if (growthInfo != null && growthInfo.pigWeight > -1000) {
			weight = CommonUtils.roundFloat(growthInfo.pigWeight, 2) + "";
		}
		String weightStr = String.format(context.getString(R.string.pig_weight),
				weight);
		SpannableString str = new SpannableString(weightStr);
		str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal_Light), 0, 2,
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		// 要重新设置一下字体样式，否则会使用系统默认字体
		CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(
				TypefaceUtils.load(context.getAssets(), BaseApplication.FONT_PATH));
		str.setSpan(typefaceSpan, 0, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	/**
	 * 猪增长体重显示字符窜
	 */
	private SpannableString getIncreasedStr(GrowthInfo growthInfo) {
		String increased = context.getString(R.string.display_none);
		if (growthInfo != null && growthInfo.increasedWeight > -1000) {
			increased = CommonUtils.roundFloat(growthInfo.increasedWeight, 2) + "";
		}
		String weightStr = String.format(context.getString(R.string.pig_increase_weight),
				increased);
		SpannableString str = new SpannableString(weightStr);
		str.setSpan(new TextAppearanceSpan(context, R.style.TextNormal_Light), 0, 2,
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		// 要重新设置一下字体样式，否则会使用系统默认字体
		CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(
				TypefaceUtils.load(context.getAssets(), BaseApplication.FONT_PATH));
		str.setSpan(typefaceSpan, 0, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	// 根据时间分组
	@Override
	public long getHeaderId(int position) {
		String YMD = TimeUtils.formatTimeStamp(getItem(position).collectedTime,
				TimeUtils.Template.YMD);
		LogUtils.i(TAG, "转换为年月日-->" + YMD);
		long timeStampYMD = TimeUtils.dateToTimeStamp(YMD, TimeUtils.Template.YMD);
		LogUtils.i(TAG, "分组时间戳-->" + timeStampYMD);
		return timeStampYMD;
	}

	@Override
	public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
		LogUtils.i(TAG, "--onCreateHeaderViewHolder--");
		View view = ViewUtils.makeView(context, R.layout.listitem_growth_history_header,
				parent, false);
		return new HeaderViewHolder(view);
	}

	@Override
	public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
		LogUtils.i(TAG, "--onBindHeaderViewHolder--");
		GrowthInfo growthInfo = getItem(position);
		holder.headerTimeView.setText(getYMD(context, growthInfo.collectedTime));
		holder.headerTimeView.getPaint().setFakeBoldText(true);
	}

	/**
	 * 获取分组时间显示
	 */
	private String getYMD(Context context, long timeStamp) {
		String YMD = TimeUtils.formatTimeStamp(timeStamp, TimeUtils.Template.YMD);
		if (!TextUtils.isEmpty(YMD)) {
			String year = YMD.substring(0, 4);
			String month = YMD.substring(5, 7);
			String day = YMD.substring(8, 10);
			return String.format(context.getString(R.string.year_month_day),
					year, month, day);
		}
		return null;
	}

	/**
	 * Item的ViewHolder类
	 */
	public static class ItemViewHolder extends RecyclerView.ViewHolder {
		ImageView pigPhotoRiv;
		TextView dateTv;
		TextView weightTv;
		TextView weightIncreaseTv;

		ItemViewHolder(View view) {
			super(view);
			pigPhotoRiv = (ImageView) view.findViewById(R.id.pig_photo_riv);
			dateTv = (TextView) view.findViewById(R.id.date_tv);
			weightTv = (TextView) view.findViewById(R.id.weight_tv);
			weightIncreaseTv = (TextView) view.findViewById(R.id.weight_increase_tv);
		}
	}

	/**
	 * Header的ViewHolder类
	 */
	public static class HeaderViewHolder extends RecyclerView.ViewHolder {
		TextView headerTimeView;

		HeaderViewHolder(View itemView) {
			super(itemView);
			headerTimeView = (TextView) itemView.findViewById(R.id.growth_history_time_tv);
		}


	}

}
