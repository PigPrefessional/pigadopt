/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.model.hogpen.SellerHogpenInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义猪圈ViewPager
 * Created by Justin Z on 2016/3/13.
 * 502953057@qq.com
 */
public class HogpenViewPager extends LinearLayout {
	/**
	 * 日志标题
	 */
	private final static String TAG = HogpenViewPager.class.getSimpleName();
	/**
	 * 猪圈个数最大限制
	 */
	public final static int HOGPEN_LIMIT = 3;
	/**
	 * 上下文引用
	 */
	private Context context;
	/**
	 * 猪圈ViewPager容器
	 */
	private ViewPager hogpenViewPager;
	/**
	 * 猪圈ViewPager适配器
	 */
	private HogpenViewPagerAdapter hogpenAdapter;
	/**
	 * 当前选中的猪圈tab的index
	 */
	private int currentIndex;
	/**
	 * 猪圈tab总数
	 */
	private int hogpenTabSize;
	/**
	 * 猪圈Tab列表
	 */
	private List<HogpenTab> hogpenTabs = new ArrayList<>();
	/**
	 * 猪圈数据列表
	 */
	private List<SellerHogpenInfo> hogpenInfos = new ArrayList<>();
	/**
	 * 猪圈切换监听
	 */
	private OnHogpenChangeListener onHogpenChangeListener;

	public HogpenViewPager(Context context) {
		super(context);
		init(context);
	}

	public HogpenViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HogpenViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	/**
	 * 初始化方法
	 */
	private void init(Context context) {
		this.context = context;
		ViewUtils.makeView(context, R.layout.hogpen_viewpager_container, this, true);
		initHogpenViewPager();
	}

	/**
	 * 初始化猪圈ViewPager
	 */
	private void initHogpenViewPager() {
		hogpenViewPager = (ViewPager) findViewById(R.id.hogpen_vp);
		hogpenAdapter = new HogpenViewPagerAdapter(context, hogpenTabs, hogpenInfos);
		hogpenViewPager.setAdapter(hogpenAdapter);
		// TODO:这里绑定页卡切换监听
		hogpenViewPager.addOnPageChangeListener(new OnPageChangeListener());
	}

	/**
	 * 绑定猪圈切换监听
	 *
	 * @param onHogpenChangeListener OnHogpenChangeListener
	 */
	public void setOnHogpenChangeListener(OnHogpenChangeListener onHogpenChangeListener) {
		this.onHogpenChangeListener = onHogpenChangeListener;
	}

	/**
	 * 设置猪圈的页卡列表
	 * 调用这个方法会对猪圈数据进行整体刷新
	 *
	 * @param hogpenInfos List<SellerHogpenInfo>
	 */
	public void setHogpenTabs(List<SellerHogpenInfo> hogpenInfos) {
		if (hogpenInfos == null || hogpenInfos.size() == 0) {
			LogUtils.i(TAG, "没有猪圈列表数据，不刷新界面");
			return;
		}
		this.hogpenInfos.clear();
		this.hogpenInfos.addAll(hogpenInfos);
		hogpenTabSize = hogpenInfos.size();
		hogpenTabs.clear();
		HogpenTab hogpenTab;
		for (int i = 0; i < hogpenTabSize; i++) {
			hogpenTab = new HogpenTab();
			hogpenTab.hogpenContainer = (LinearLayout) ViewUtils
					.makeView(context, R.layout.hogpen_container);
			hogpenTabs.add(hogpenTab);
		}
		// 刷新界面数据
		hogpenAdapter.notifyDataSetChanged();
	}

	/**
	 * 添加猪圈
	 * 添加猪圈请求成功之后，重新拉取猪圈数据，并刷新数据
	 */
	public void addHogpen(SellerHogpenInfo sellerHogpenInfo) {
		if (hogpenAdapter == null) {
			LogUtils.i(TAG, "猪圈页卡数据还没有初始化");
			return;
		}
		if (sellerHogpenInfo == null) {
			LogUtils.i(TAG, "要添加的猪圈数据不能为空");
			return;
		}
		if (hogpenTabSize >= HOGPEN_LIMIT) {
			LogUtils.i(TAG, "猪圈总数超过最大数量限制:" + HOGPEN_LIMIT + ", 不能添加！");
			return;
		}
		// 将添加数据加入到猪圈数据列表的末尾
		this.hogpenInfos.add(sellerHogpenInfo);
		HogpenTab hogpenTab = new HogpenTab();
		hogpenTab.hogpenContainer = (LinearLayout) ViewUtils
				.makeView(context, R.layout.hogpen_container);
		hogpenTabs.add(hogpenTab);
		hogpenTabSize = hogpenTabs.size();
		// 刷新界面数据
		hogpenAdapter.notifyDataSetChanged();
	}


	/**
	 * 设置当前选中的猪圈页卡
	 *
	 * @param index 需要选中的猪圈页卡的下标
	 */
	public void setCurrentIndex(int index) {
		if (index < 0 || index > hogpenTabSize - 1) {
			LogUtils.i(TAG, "要选中的猪圈页卡下标越界，当前猪圈个数为:" + hogpenTabSize);
			return;
		}
		hogpenViewPager.setCurrentItem(index, true);
	}

	/**
	 * 页卡切换监听
	 */
	private class OnPageChangeListener implements ViewPager.OnPageChangeListener {

		/**
		 * 选中页卡回调
		 */
		@Override
		public void onPageSelected(int arg0) {
			currentIndex = arg0;
			if (onHogpenChangeListener != null) {
				onHogpenChangeListener.onHogpenSelected(arg0);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public interface OnHogpenChangeListener {
		void onHogpenSelected(int index);
	}


}
