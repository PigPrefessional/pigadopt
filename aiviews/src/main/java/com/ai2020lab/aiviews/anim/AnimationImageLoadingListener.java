package com.ai2020lab.aiviews.anim;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 自定义图片加载事件监听器，实现加载完成后用动画的方式载入图片
 * Created by Justin on 2016/1/11.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class AnimationImageLoadingListener extends SimpleImageLoadingListener {

	private final static String TAG = AnimationImageLoadingListener.class.getSimpleName();

	private final static int ANIMATION_TIME = 1000;

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		if(loadedImage == null) {
			LogUtils.i(TAG, "loadedImage对象为空");
			return;
		}
		ImageView imageView = (ImageView) view;
		FadeInBitmapDisplayer.animate(imageView, ANIMATION_TIME);
	}
}
