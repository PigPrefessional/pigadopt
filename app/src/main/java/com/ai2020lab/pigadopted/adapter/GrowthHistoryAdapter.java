/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.adapter;

import android.support.v7.widget.RecyclerView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.pigadopted.model.pig.GrowthInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 成长历程列表Adapter
 * Created by Justin Z on 2016/3/28.
 * 502953057@qq.com
 */
public abstract class GrowthHistoryAdapter<VH extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<VH> {

	private final static String TAG = BuyerPigListAdapter.class.getSimpleName();

	private List<GrowthInfo> items = new ArrayList<>();

	/**
	 * 构造方法
	 */
	public GrowthHistoryAdapter() {
		setHasStableIds(true);
	}

	public void add(GrowthInfo object) {
		items.add(object);
		notifyDataSetChanged();
	}

	public void add(int index, GrowthInfo object) {
		items.add(index, object);
		// 只有调用这个方法动画才能生效
		notifyItemInserted(index);
//		notifyDataSetChanged();
	}

	public void addAll(Collection<? extends GrowthInfo> collection) {
		if (collection != null) {
			items.addAll(collection);
			notifyDataSetChanged();
		}
	}

	public void addAll(GrowthInfo... items) {
		addAll(Arrays.asList(items));
	}

	public void clear() {
		LogUtils.i(TAG, "数据清空");
		items.clear();
		notifyDataSetChanged();
	}

	public void remove(GrowthInfo object) {
		items.remove(object);
		notifyDataSetChanged();
	}

	public void remove(int position) {
		items.remove(position);
		notifyItemRemoved(position);
	}

	public GrowthInfo getItem(int position) {
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
