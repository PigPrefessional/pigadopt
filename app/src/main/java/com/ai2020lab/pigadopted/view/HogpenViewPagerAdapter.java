/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ai2020lab.pigadopted.model.hogpen.SellerHogpenInfo;

import java.util.List;

/**
 * Created by Justin Z on 2016/3/13.
 * 502953057@qq.com
 */
public class HogpenViewPagerAdapter extends PagerAdapter {

	private final static String TAG = HogpenViewPagerAdapter.class.getSimpleName();

	private List<HogpenTab> hogpenTabs;

	private List<SellerHogpenInfo> sellerHogpenInfos;

	private Context context;

	/**
	 * 构造方法
	 *
	 * @param context           Context
	 * @param hogpenTabs        List<HogpenTab>
	 * @param sellerHogpenInfos List<SellerHogpenInfo>
	 */
	public HogpenViewPagerAdapter(Context context, List<HogpenTab> hogpenTabs,
	                              List<SellerHogpenInfo> sellerHogpenInfos) {
		this.context = context;
		this.hogpenTabs = hogpenTabs;
		this.sellerHogpenInfos = sellerHogpenInfos;
	}

	@Override
	public int getCount() {
		return hogpenTabs == null || hogpenTabs.size() == 0 ? 0 : hogpenTabs.size();
	}

	public SellerHogpenInfo getItemData(int position) {
		if (sellerHogpenInfos == null || sellerHogpenInfos.size() == 0)
			return null;
		return sellerHogpenInfos.get(position);
	}

	public HogpenTab getItem(int position) {
		if (hogpenTabs == null || hogpenTabs.size() == 0)
			return null;
		return hogpenTabs.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}

	/**
	 * Create the page for the given position.  The adapter is responsible
	 * for adding the view to the container given here, although it only
	 * must ensure this is done by the time it returns from
	 * {@link #finishUpdate(ViewGroup)}.
	 *
	 * @param container The containing View in which the page will be shown.
	 * @param position  The page position to be instantiated.
	 * @return Returns an Object representing the new page.  This does not
	 * need to be a View, but can be some other container of the page.
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		final HogpenTab hogpenTab = getItem(position);
		final SellerHogpenInfo sellerHogpenInfo = getItemData(position);
		if (hogpenTab == null || sellerHogpenInfo == null) return null;
		container.addView(hogpenTab.hogpenPigListView);

		return hogpenTab.hogpenPigListView;
	}


	/**
	 * Remove a page for the given position.  The adapter is responsible
	 * for removing the view from its container, although it only must ensure
	 * this is done by the time it returns from {@link #finishUpdate(ViewGroup)}.
	 *
	 * @param container The containing View from which the page will be removed.
	 * @param position  The page position to be removed.
	 * @param object    The same object that was returned by
	 *                  {@link #instantiateItem(View, int)}.
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		final HogpenTab hogpenTab = getItem(position);
		if (hogpenTab == null) return;
		container.removeView(hogpenTab.hogpenPigListView);
	}

}
