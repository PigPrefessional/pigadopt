/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.support.v7.widget.RecyclerView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 买家猪列表RecyclerView适配器
 * Created by Justin Z on 2016/3/21.
 * 502953057@qq.com
 */
public abstract class BuyerPigListAdapter<VH extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<VH> {

	private final static String TAG = BuyerPigListAdapter.class.getSimpleName();

	private List<PigDetailInfoAndOrder> items = new ArrayList<>();

	/**
	 * 构造方法
	 */
	public BuyerPigListAdapter() {
		setHasStableIds(true);
	}

	public void add(PigDetailInfoAndOrder object) {
		items.add(object);
		notifyDataSetChanged();
	}

	public void add(int index, PigDetailInfoAndOrder object) {
		items.add(index, object);
		notifyItemInserted(index);
		notifyDataSetChanged();
	}

	public void addAll(Collection<? extends PigDetailInfoAndOrder> collection) {
		if (collection != null) {
			items.addAll(collection);
			notifyDataSetChanged();
		}
	}

	public void addAll(PigDetailInfoAndOrder... items) {
		addAll(Arrays.asList(items));
	}

	public void clear() {
		LogUtils.i(TAG, "数据清空");
		items.clear();
		notifyDataSetChanged();
	}

	public void remove(PigDetailInfoAndOrder object) {
		items.remove(object);
		notifyDataSetChanged();
	}

	public void remove(int position) {
		items.remove(position);
		notifyItemRemoved(position);
	}

	public PigDetailInfoAndOrder getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public int getItemCount() {
		return items.size();
	}


}
