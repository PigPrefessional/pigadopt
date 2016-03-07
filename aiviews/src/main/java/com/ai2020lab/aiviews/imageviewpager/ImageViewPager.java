package com.ai2020lab.aiviews.imageviewpager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 横向滑动展示图片，并带页卡指示的ViewPager
 * Created by Justin on 2015/11/18.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ImageViewPager extends RelativeLayout {

	private final static String TAG = ImageViewPager.class.getSimpleName();
	/**
	 * 页卡指示游标显示为点的页卡总数限制
	 */
	private final static int DOT_INDICATOR_LIMIT = 8;

	private Context context;
	/**
	 * 图片展示ViewPager
	 */
	private ViewPager viewPager;
	/**
	 * ViewPager页卡指示容器
	 */
	private LinearLayout indicatorLayout;
	/**
	 * ImageViewPager页卡列表数据
	 */
	private List<ImageTab> imgTabs;
	/**
	 * 图片URL列表总数
	 */
	private int imageSize;
	/**
	 * 当前显示的页卡index
	 */
	private int currentIndex;
	/**
	 * 页卡数量是否超过最大限制，用来判断采用点状页卡指示还是采用文字页卡指示
	 */
	private boolean isExceedLimit = false;
	/**
	 * 点状页卡指示器正常状态Drawable
	 */
	private Drawable indicatorNormal;
	/**
	 * 点状页卡指示器选择状态Drawable
	 */
	private Drawable indicatorSelected;

	private boolean isFirstLoad = true;

	public ImageViewPager(Context context) {
		super(context);
		init(context);
	}

	public ImageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ImageViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	/**
	 * 初始化界面布局和资源
	 */
	@SuppressWarnings("deprecation")
	private void init(Context context) {
		this.context = context;
		// 初始化界面
		LayoutInflater.from(context).inflate(R.layout.image_viewpager, this, true);
		viewPager = (ViewPager) findViewById(R.id.image_viewpager);
		indicatorLayout = (LinearLayout) findViewById(R.id.indicator_layout);
		Resources r = context.getResources();
		indicatorNormal = r.getDrawable(R.mipmap.indicator_normal);
		indicatorSelected = r.getDrawable(R.mipmap.indicator_selected);
	}

	/**
	 * 设置图片URL链接地址列表
	 *
	 * @param imageUrls 图片URL链接地址列表
	 */
	public void setImageUrls(List<String> imageUrls) {
		if (imageUrls == null || imageUrls.size() == 0) {
			LogUtils.i(TAG, "图片url数据为空");
			return;
		}
//		if(!isFirstLoad){
//			LogUtils.i(TAG, "不是第一次加载，不做数据初始化");
//			return;
//		}
		this.imageSize = imageUrls.size();
//		LogUtils.i(TAG, "图片链接的长度-->" + imageSize);
		imgTabs = new ArrayList<>();
		ImageTab imgTab;
		for (int i = 0; i < imageSize; i++) {
			imgTab = new ImageTab();
			imgTab.imgUrl = imageUrls.get(i);
			LogUtils.i(TAG, "图片链接地址-->" + imgTab.imgUrl);
			imgTab.imageView = (ImageView) ViewUtils.makeView(context, R.layout.image);
			imgTabs.add(imgTab);
		}
		initViewPager();
		initIndicator();
//		isFirstLoad = false;
	}

	// 初始化ViewPager
	private void initViewPager() {
		ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(context, imgTabs);
		viewPager.setAdapter(adapter);
		LogUtils.i(TAG, "当前页码->" + currentIndex);
		viewPager.setCurrentItem(0);
		viewPager.addOnPageChangeListener(new ImageOnPageChangeListener());
	}

	// 初始化页卡指示器
	private void initIndicator() {
		if (indicatorLayout.getChildCount() > 0) indicatorLayout.removeAllViews();
		if (imageSize > DOT_INDICATOR_LIMIT) {
			isExceedLimit = true;
		}
		// 只有不超过最大页卡数才显示点状indicator
		if (!isExceedLimit) {
			// 页卡指示器View
			ImageView indicatorDot = null;
			for (int i = 0; i < imageSize; i++) {
				indicatorDot = (ImageView) ViewUtils.makeView(context, R.layout.indicator_dot);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.MATCH_PARENT);
				lp.setMargins(10, 0, 10, 0);
				indicatorDot.setLayoutParams(lp);
				indicatorLayout.addView(indicatorDot);
				if (i == currentIndex) {
					indicatorDot.setImageDrawable(indicatorSelected);
				} else {
					indicatorDot.setImageDrawable(indicatorNormal);
				}
			}
		} else {
			TextView indicatorText = (TextView) ViewUtils.makeView(context, R.layout.indicator_text);
			indicatorText.setText(String.format("%d/%d", currentIndex + 1, imageSize));
			indicatorLayout.addView(indicatorText);
		}
	}

	/**
	 * 页卡切换监听
	 */
	private class ImageOnPageChangeListener implements ViewPager.OnPageChangeListener {

		/**
		 * 选中页卡回调
		 */
		@Override
		public void onPageSelected(int arg0) {
			currentIndex = arg0;
			if (!isExceedLimit) {
				for (int i = 0; i < imageSize; i++) {
					ImageView indicatorDot = (ImageView) indicatorLayout.getChildAt(i);
					if (indicatorDot == null) {
						LogUtils.i(TAG, "点状页卡指示为空");
						return;
					}
					// 如果为当前选中页卡就让指示器选中
					if (i == currentIndex) {
						indicatorDot.setImageDrawable(indicatorSelected);
					} else {
						indicatorDot.setImageDrawable(indicatorNormal);
					}
				}
			}
			//
			else {
				TextView indicatorText = (TextView) indicatorLayout.getChildAt(0);
				if (indicatorText == null) {
					LogUtils.i(TAG, "文字页卡指示为空");
					return;
				}
				indicatorText.setText(String.format("%d/%d", currentIndex + 1, imageSize));
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}


}