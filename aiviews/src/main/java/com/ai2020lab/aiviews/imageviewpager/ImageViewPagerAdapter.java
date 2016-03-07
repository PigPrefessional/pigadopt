package com.ai2020lab.aiviews.imageviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ai2020lab.aiutils.common.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Justin on 2015/11/18.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ImageViewPagerAdapter extends PagerAdapter {

	private final static String TAG = ImageViewPagerAdapter.class.getSimpleName();

	/**
	 * ImageViewPager页卡列表数据
	 */
	private List<ImageTab> imageTabs;

	private ImageLoader imageLoader;

	/**
	 * 构造器
	 *
	 * @param context   上下文引用
	 * @param imageTabs 图片链接列表
	 */
	public ImageViewPagerAdapter(Context context, List<ImageTab> imageTabs) {
		this.imageTabs = imageTabs;
		this.imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return imageTabs == null || imageTabs.size() == 0 ? 0 : imageTabs.size();
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

		final ImageTab imageTab = imageTabs.get(position);
		container.addView(imageTab.imageView);
		String imgUrl = imageTab.imgUrl;
		LogUtils.i(TAG, "图片地址-->" + imgUrl);
		// 加载图片
		imageLoader.displayImage(imgUrl, imageTab.imageView);
		return imageTab.imageView;
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
		container.removeView(imageTabs.get(position).imageView);
	}


}
