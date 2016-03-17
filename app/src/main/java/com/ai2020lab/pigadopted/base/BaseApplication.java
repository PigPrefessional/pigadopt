package com.ai2020lab.pigadopted.base;

import android.app.Application;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.pigadopted.common.ImageLoaderManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ai2020lab.pigadopted.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Justin on 2015/11/17.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class BaseApplication extends Application {

	private final static String TAG = BaseApplication.class.getSimpleName();

	/**
	 * 自定义Application对象的引用
	 */
	private static BaseApplication instance = null;

	/**
	 * 得到应用程序的实例
	 *
	 * @return 返回应用程序实例
	 */
	public static BaseApplication getInstance() {
		return instance;
	}

	/**
	 * 在应用程序启动的时候回调 在这里启动全局的系统服务
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.d(TAG, "自定义BaseApplication启动");
		instance = this;
		// 初始化ImageLoader的配置
		ImageLoaderManager.initConfig(this);
		// 初始化资源工具类
		ResourcesUtils.initContext(this);

		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
						.setDefaultFontPath("fonts/HYQingKongTiJ.ttf")
						.setFontAttrId(R.attr.fontPath)
						.build()
		);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		LogUtils.d(TAG, "自定义BaseApplication结束");
	}

}
