/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aimedia;

import android.content.Context;

import com.ai2020lab.aimedia.model.FlashMode;
import com.ai2020lab.aimedia.model.HDRMode;
import com.ai2020lab.aimedia.model.Quality;
import com.ai2020lab.aiutils.storage.PreferencesUtils;

/**
 * 相机设置参数管理类，缓存相机设置参数
 * Created by Justin Z on 2016/4/13.
 * 502953057@qq.com
 */
public class CameraSettingPrefManager {
	private final static String TAG = CameraSettingPrefManager.class.getSimpleName();

	public static final String CAMERA_QUALITY = "camera_quality";
	public static final String CAMERA_FLASH_MODE = "camera_flash_mode";
	public static final String CAMERA_HDR_MODE = "camera_hdr_mode";

	/**
	 * 缓存画质选择
	 *
	 * @param context Context
	 * @param quality Quality
	 */
	public static void setQuality(Context context, Quality quality) {
		PreferencesUtils.setInt(context, CAMERA_QUALITY, quality.getId());
	}

	/**
	 * 获取缓存的画质选择
	 *
	 * @param context Context
	 * @return 返回当前缓存的画质选择
	 */
	public static Quality getQuality(Context context) {
		return Quality.getQualityById(PreferencesUtils.getInt(context,
				CAMERA_QUALITY, Quality.LOW.getId()));
	}

	/**
	 * 缓存HDR模式
	 *
	 * @param context Context
	 * @param hdrMode HDRMode
	 */
	public static void setHDRMode(Context context, HDRMode hdrMode) {
		PreferencesUtils.setInt(context, CAMERA_HDR_MODE, hdrMode.getId());
	}

	/**
	 * 获取缓存的HDR模式
	 *
	 * @param context Context
	 * @return 返回当前缓存的HDR模式
	 */
	public static HDRMode getHDRMode(Context context) {
		return HDRMode.getHDRModeById(PreferencesUtils.getInt(context,
				CAMERA_HDR_MODE, HDRMode.NONE.getId()));
	}

	/**
	 * 缓存闪光灯模式
	 *
	 * @param context   Context
	 * @param flashMode FlashMode
	 */
	public static void setFlashMode(Context context, FlashMode flashMode) {
		PreferencesUtils.setInt(context, CAMERA_FLASH_MODE, flashMode.getId());
	}

	/**
	 * 获取缓存的闪光灯模式
	 *
	 * @param context Context
	 * @return 返回当前缓存的闪光灯模式
	 */
	public static FlashMode getFlashMode(Context context) {
		return FlashMode.getFlashModeById(PreferencesUtils.getInt(context,
				CAMERA_FLASH_MODE, FlashMode.AUTO.getId()));
	}


}
