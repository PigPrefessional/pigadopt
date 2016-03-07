package com.ai2020lab.pigadopted.common;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.storage.FileUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.pigadopted.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * Created by Justin on 2016/1/11.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ImageLoaderManager {

	public final static String IMAGE_CACHE_PATH = "analysis_image";
	private final static String TAG = ImageLoaderManager.class.getSimpleName();

	/**
	 * 获取ImageLoaderConfiguration的配置
	 *
	 * @param context 上下文引用
	 */
	public static void initConfig(Context context) {
		File file = FileUtils.getDiskCacheDir(context, IMAGE_CACHE_PATH);
		LogUtils.i(TAG, "imageLoader缓存图片的磁盘路径->" + file.getPath());
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
		// 设置内存缓存中图片的最大宽高
		builder.memoryCacheExtraOptions(DisplayUtils.getScreenWidth(context),
				DisplayUtils.getScreenHeight(context));
		builder.threadPoolSize(5).threadPriority(Thread.NORM_PRIORITY - 2);
		builder.tasksProcessingOrder(QueueProcessingType.FIFO);
		// 不缓存图片的多种尺寸在内存中
		builder.denyCacheImageMultipleSizesInMemory();
		builder.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024));
		// 设置图片缓存在磁盘上的路径,会在SD卡上创建一个图片缓存的路径
		builder.diskCache(new UnlimitedDiskCache(file));
		// 使用MD5文件名方式缓存图片
		builder.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		ImageLoader.getInstance().init(builder.build());
	}

	/**
	 * 获取图片加载配置
	 *
	 * @param context 上下文引用
	 * @return DisplayImageOptions
	 */
	public static DisplayImageOptions getImageOptions(Context context) {
		Drawable defaultDrawable = ResourcesUtils.getDrawable(R.mipmap.image_default);
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.showImageOnLoading(defaultDrawable);
		builder.showImageForEmptyUri(defaultDrawable);
		builder.showImageOnFail(defaultDrawable);
		// 设置是否缓存在内存中
		builder.cacheInMemory(true);
		// 设置是否缓存在磁盘中
		builder.cacheOnDisk(true);

		return builder.build();
	}


}
