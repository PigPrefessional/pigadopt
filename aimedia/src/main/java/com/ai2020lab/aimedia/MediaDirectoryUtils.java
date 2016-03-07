/*
 * Copyright 2016 Justin Z
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ai2020lab.aimedia;

import android.content.Context;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.storage.FileUtils;
import com.ai2020lab.aiutils.storage.StorageUtils;
import com.ai2020lab.aiutils.system.PackageUtils;

import java.io.File;

/**
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class MediaDirectoryUtils {

	private final static String TAG = MediaDirectoryUtils.class.getSimpleName();

	private final static String MEDIA = "MEDIA";

	private final static String AUDIO = "audio";

	private final static String VIDEO = "video";

	private final static String IMAGE = "image";

	/**
	 * 获取多媒体文件存储路径
	 *
	 * @param context Context
	 * @return 返回多媒体文件存储路径
	 */
	public static String getMediaDir(Context context) {
		String dir;
		StorageUtils storageUtils = StorageUtils.getInstance(context);
		// 有外卡就使用外卡
		if (storageUtils.isHasExternalStorage()) {
			dir = storageUtils.getExternalDirectory() + File.separator +
					PackageUtils.getAppPackageName(context) + File.separator + MEDIA;
		}
		// 没有外卡则使用内卡
		else {
			dir = storageUtils.getInternalDirectory() + File.separator +
					PackageUtils.getAppPackageName(context) + File.separator + MEDIA;
		}
		LogUtils.i(TAG, "Media路径->" + dir);
		if (!FileUtils.isExist(dir)) FileUtils.makeDir(dir);
		return dir;
	}

	/**
	 * 获取相机拍照路径
	 *
	 * @param context Context
	 * @return 返回相机拍照路径
	 */
	public static String getCameraDir(Context context) {
		String imageDir = getMediaDir(context) + File.separator + IMAGE;
		LogUtils.i(TAG, "相机拍照路径->" + imageDir);
		if (!FileUtils.isExist(imageDir)) FileUtils.makeDir(imageDir);
		return imageDir;
	}

	/**
	 * 获取音频录音文件路径
	 *
	 * @param context Context
	 * @return 返回音频录音文件路径
	 */
	public static String getAudioDir(Context context) {
		String audioDir = getMediaDir(context) + File.separator + AUDIO;
		LogUtils.i(TAG, "音频文件路径->" + audioDir);
		if (!FileUtils.isExist(audioDir)) FileUtils.makeDir(audioDir);
		return audioDir;
	}

	/**
	 * 获取视频录制文件路径
	 *
	 * @param context Context
	 * @return 返回获取视频录制文件路径
	 */
	public static String getVideoDir(Context context) {
		String videoDir = getMediaDir(context) + File.separator + VIDEO;
		LogUtils.i(TAG, "视频文件路径->" + videoDir);
		if (!FileUtils.isExist(videoDir)) FileUtils.makeDir(videoDir);
		return videoDir;
	}


}
