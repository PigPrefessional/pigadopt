/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiviews.textview.RotateTextView;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoForBuyer;

/**
 * Created by Justin Z on 2016/3/21.
 * 502953057@qq.com
 */
public class BuyerPigListRvAdapter extends BuyerPigListAdapter<BuyerPigListRvAdapter.ItemViewHolder> {
	private final static String TAG = BuyerPigListRvAdapter.class.getSimpleName();

	private LayoutInflater inflater;

	private Context context;

	/**
	 * 构造方法
	 * @param context Context
	 */
	public BuyerPigListRvAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
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
		final PigDetailInfoForBuyer pigInfo = getItem(position);


	}

	/**
	 * ViewHolder内部类
	 */
	public class ItemViewHolder extends RecyclerView.ViewHolder {
		private ImageView buyerPigIv;
		private LinearLayout buyerPigInfoRl;
		private TextView buyerPigAgeTv;
		private TextView buyerPigWeightTv;
		private RotateTextView sellerNameTv;
		private ImageView lineLeftUpIv;
		private ImageView lineLeftDownIv;
		private ImageView lineRightUpIv;
		private ImageView lineRightDownIv;

		public ItemViewHolder(View view) {
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
