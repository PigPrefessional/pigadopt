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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;

import com.ai2020lab.aimedia.model.FlashMode;
import com.ai2020lab.aimedia.model.HDRMode;
import com.ai2020lab.aimedia.model.Quality;
import com.ai2020lab.aimedia.model.Ratio;
import com.ai2020lab.aiutils.common.LogUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 摄像头参数配置类
 * <p/>
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
final class CameraSettingManager {
	/**
	 * 日志标题
	 */
	private static final String TAG = CameraSettingManager.class
			.getSimpleName();
	/**
	 * 正则表达式表现模式
	 */
	private static final Pattern COMMA_PATTERN = Pattern.compile(",");
	/**
	 * 上下文引用
	 */
	private Context context;

	private Camera camera;
	private Camera.Parameters parameters;

	/**
	 * 预览分辨率Map
	 */
	private Map<Ratio, Size> previewSizes;
	/**
	 * 拍照分辨率Map
	 */
	private Map<Ratio, Map<Quality, Camera.Size>> pictureSizes;
	/**
	 * 是否支持HDR模式 high dynamic range imaging
	 * 高动态范围成像，用来实现比普通数字图像技术更大曝光动态范围
	 * 适合场景：阴暗变化明显的场景下，可以使明处的景物不至于过曝，
	 * 暗处的景物不至于欠曝,特别是在逆光下，可以将人物和环境都拍清晰
	 */
	private boolean supportedHDR = false;
	/**
	 * 是否支持闪光灯
	 */
	private boolean supportedFlash = false;

	/**
	 * 构造方法
	 * <p/>
	 */
	CameraSettingManager() {
	}

	/**
	 * 初始化相机配置参数，根据设备屏幕分辨率计算得到<br>
	 *
	 * @param camera Camera
	 */
	void initFromCameraParameters(Context context, Camera camera) {
		if (context == null) {
			LogUtils.i(TAG, "context为空，初始化相机参数失败");
			return;
		}
		if (camera == null) {
			LogUtils.i(TAG, "-->camera对象为空，初始化相机参数失败");
			return;
		}
		this.context = context;
		this.camera = camera;
		parameters = camera.getParameters();
		// 得到预览分辨率集合
		previewSizes = buildPreviewSizesRatioMap(parameters.getSupportedPreviewSizes());
		// 得到拍照分辨率集合
		pictureSizes = buildPictureSizesRatioMap(parameters.getSupportedPictureSizes());
		// 检查相机是否支持闪光灯
		List<String> flashModes = parameters.getSupportedFlashModes();
		if (flashModes == null || flashModes.size() <= 1) {
			supportedFlash = false;
		} else {
			supportedFlash = true;
		}
		// 检查相机是否支持HDR模式
		List<String> sceneModes = parameters.getSupportedSceneModes();
		if (sceneModes != null) {
			for (String mode : sceneModes) {
				if (mode.equals(Camera.Parameters.SCENE_MODE_HDR)) {
					supportedHDR = true;
					break;
				}
			}
		}
		LogUtils.i(TAG, "是否支持闪光灯-->" + supportedFlash);
		LogUtils.i(TAG, "是否支持HDR模式-->" + supportedHDR);
	}

	/**
	 * 设置拍照分辨率
	 */
	void setPictureSize(Quality quality, Ratio ratio) {
		if (pictureSizes == null || parameters == null) {
			LogUtils.i(TAG, "--还没有初始化，设置拍照分辨率失败");
			return;
		}
		Camera.Size size = pictureSizes.get(ratio).get(quality);
		if (size != null) {
			Log.i(TAG, "照片分辨率-->" + size.width + "x" + size.height);
			parameters.setPictureSize(size.width, size.height);
		}
	}

	/**
	 * 设置预览分辨率
	 */
	void setPreviewSize(Ratio ratio) {
		if (previewSizes == null || parameters == null) {
			LogUtils.i(TAG, "--还没有初始化，设置预览分辨率失败");
			return;
		}
		Camera.Size size = previewSizes.get(ratio);
		Log.i(TAG, "预览分辨率-->" + size.width + "x" + size.height);
		parameters.setPreviewSize(size.width, size.height);
	}

	/**
	 * 获取相机是否支持闪光灯
	 *
	 * @return true-支持闪光灯，false-不支持闪光灯
	 */
	boolean isSupportedFlash() {
		if (parameters == null) {
			LogUtils.i(TAG, "--还没有初始化，获取闪光灯支持失败");
			return false;
		}
		return supportedFlash;
	}

	/**
	 * 获取相机是否支持HDR模式
	 *
	 * @return true-支持HDR,false-不支持HDR
	 */
	boolean isSupportedHDR() {
		if (parameters == null) {
			LogUtils.i(TAG, "--还没有初始化，获取HDR支持失败");
			return false;
		}
		return supportedHDR;
	}

	/**
	 * 设置闪光灯模式
	 */
	void setFlashMode(FlashMode flashMode) {
		if (parameters == null || context == null) {
			LogUtils.i(TAG, "--初始化相机后才能设置闪光灯模式");
			return;

		}
		if (!supportedFlash) {
			LogUtils.i(TAG, "--相机不支持闪光灯,无法设置闪光灯模式");
			return;
		}
		switch (flashMode) {
			case ON:
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
				break;
			case OFF:
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				break;
			case AUTO:
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				break;
		}
		CameraSettingPrefManager.setFlashMode(context, flashMode);
	}

	/**
	 * 设置HDR模式
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	void setHDRMode(HDRMode hdrMode) {
		if (parameters == null || context == null) {
			LogUtils.i(TAG, "--初始化相机后才能设置HDR模式");
			return;

		}
		if (supportedHDR && hdrMode == HDRMode.NONE) {
			hdrMode = HDRMode.OFF;
		}
		switch (hdrMode) {
			case ON:
				parameters.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
				break;
			case OFF:
				parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
				break;
		}
		CameraSettingPrefManager.setHDRMode(context, hdrMode);
	}

	/**
	 * 改变相机参数后必须调用这个方法才能生效
	 */
	void setParameters() {
		if (camera == null || parameters == null) {
			LogUtils.i(TAG, "--先初始化再调用设置参数方法");
			return;
		}
		camera.setParameters(parameters);
	}

	/**
	 * 设置相机参数
	 */
	void setDesiredCameraParameters() {
		if (camera == null) {
			LogUtils.i(TAG, "--还没有初始化");
			return;
		}
		// 设置相机闪光灯模式
		setFlashMode(FlashMode.AUTO);
		// 开启HDR模式
		setHDRMode(HDRMode.OFF);
		// 设置相机预览分辨率，预览画面宽高比为4:3
		setPreviewSize(Ratio.R_4x3);
		// 设置相机拍照分辨率，使用高画质，画面宽高比为4:3
		setPictureSize(Quality.HIGH, Ratio.R_4x3);
		// 可以直接调用系统的setDisplayOrientation，不考虑低版本问题
//		setDisplayOrientation(camera, 90);
		camera.setDisplayOrientation(90);
		setParameters();
	}

	/**
	 * compatible 1.6
	 */
	private void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", int.class);
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, angle);
		} catch (Exception e) {
			LogUtils.e(TAG, "Exception", e);
		}
	}

	private Map<Ratio, Camera.Size> buildPreviewSizesRatioMap(List<Camera.Size> sizes) {
		Map<Ratio, Camera.Size> map = new HashMap<>();

		for (Camera.Size size : sizes) {
			Ratio ratio = Ratio.pickRatio(size.width, size.height);
			if (ratio != null) {
				Camera.Size oldSize = map.get(ratio);
				if (oldSize == null || (oldSize.width < size.width || oldSize.height < size.height)) {
					map.put(ratio, size);
				}
			}
		}
		return map;
	}

	private Map<Ratio, Map<Quality, Camera.Size>> buildPictureSizesRatioMap(List<Camera.Size> sizes) {
		Map<Ratio, Map<Quality, Camera.Size>> map = new HashMap<>();

		Map<Ratio, List<Camera.Size>> ratioListMap = new HashMap<>();
		for (Camera.Size size : sizes) {
			Ratio ratio = Ratio.pickRatio(size.width, size.height);
			if (ratio != null) {
				List<Camera.Size> sizeList = ratioListMap.get(ratio);
				if (sizeList == null) {
					sizeList = new ArrayList<>();
					ratioListMap.put(ratio, sizeList);
				}
				sizeList.add(size);
			}
		}
		for (Ratio r : ratioListMap.keySet()) {
			List<Camera.Size> list = ratioListMap.get(r);
			ratioListMap.put(r, sortSizes(list));
			Map<Quality, Camera.Size> sizeMap = new HashMap<>();
			int i = 0;
			for (Quality q : Quality.values()) {
				Camera.Size size = null;
				if (i < list.size()) {
					size = list.get(i++);
				}
				sizeMap.put(q, size);
			}
			map.put(r, sizeMap);
		}

		return map;
	}

	private List<Camera.Size> sortSizes(List<Camera.Size> sizes) {
		int count = sizes.size();

		while (count > 2) {
			for (int i = 0; i < count - 1; i++) {
				Camera.Size current = sizes.get(i);
				Camera.Size next = sizes.get(i + 1);

				if (current.width < next.width || current.height < next.height) {
					sizes.set(i, next);
					sizes.set(i + 1, current);
				}
			}
			count--;
		}

		return sizes;
	}

	/**
	 * 得到相机预览分辨率<br>
	 * 根据相机参数和屏幕分辨率计算得到
	 */
	private static Point getPreviewResolution(Parameters parameters,
	                                          Point screenResolution) {

		String previewSizeValueString = parameters.get("preview-size-values");
		// saw this on Xperia
		if (previewSizeValueString == null) {
			previewSizeValueString = parameters.get("preview-size-value");
		}

		Point cameraResolution = null;
		// 找到最好的相机分辨率设置
		if (previewSizeValueString != null) {
			LogUtils.d(TAG, "preview-size-values parameter: "
					+ previewSizeValueString);
			cameraResolution = findBestPreviewSizeValue(previewSizeValueString,
					screenResolution);
		}
		LogUtils.i(TAG, "手机支持的预览分辨率--->" + previewSizeValueString);
		// 没有找到的情况设置为屏幕分辨率，并保证为8的倍数
		if (cameraResolution == null) {
			LogUtils.i(TAG, "没有找到预览分辨率");
			// Ensure that the camera resolution is a multiple of 8, as the
			// screen may not be.
			cameraResolution = new Point((screenResolution.x >> 3) << 3,
					(screenResolution.y >> 3) << 3);
		}
		return cameraResolution;
	}

	/**
	 * 找到最好的预览分辨率
	 */
	private static Point findBestPreviewSizeValue(
			CharSequence previewSizeValueString, Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		// Integer的最大值
		int diff = Integer.MAX_VALUE;

		// previewSizeValueString的形式：176x144,320x240...
		// 使用“，”作为每个分辨率的分割符
		for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {
			// 去掉空格
			previewSize = previewSize.trim();
			// 找到每个分辨率中间"x"的位置
			int dimPosition = previewSize.indexOf('x');
			// 没有找到则是无效分辨率
			if (dimPosition < 0) {
				LogUtils.w(TAG, "Bad preview-size: " + previewSize);
				continue;
			}

			int newX;
			int newY;
			try {
				// 得到"x"前的数字，width
				newX = Integer.parseInt(previewSize.substring(0, dimPosition));
				// 得到"x"后的数字，height
				newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
			} catch (NumberFormatException nfe) {
				// 转换异常则认为是无效分辨率
				LogUtils.w(TAG, "Bad preview-size: " + previewSize);
				continue;
			}

			int newDiff = Math.abs(newX - screenResolution.x)
					+ Math.abs(newY - screenResolution.y);
			// 这种情况是找到和屏幕分辨率相等的分辨率
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			} else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}

		}
		if (bestX > 0 && bestY > 0) {
			LogUtils.i(TAG, "bestX-->" + bestX);
			LogUtils.i(TAG, "bestY-->" + bestY);
			return new Point(bestX, bestY);
		}
		return null;
	}

	/**
	 * 找到最小的分辨率
	 *
	 * @param sizes
	 * @return
	 */
	private static Size findMinSize(List<Size> sizes) {
		if (sizes == null || sizes.size() == 0) {
			return null;
		}
		if (sizes.size() > 1) {
			// 快速排序,按从小到大
			Collections.sort(sizes, new Comparator<Size>() {

				@Override
				public int compare(Size s1, Size s2) {
					int result = 0;
					long s1Result = s1.width * s1.height;
					long s2Result = s2.width * s2.height;
					if (s1Result > s2Result) {
						result = 1;
					} else if (s1Result < s2Result) {
						result = -1;
					}
					return result;
				}
			});
		}
		return sizes.get(0);

	}

	/**
	 * 找到排在集合数据中间大小的分辨率
	 *
	 * @param sizes
	 * @return
	 */
	private static Size findMidSize(List<Size> sizes) {
		if (sizes == null || sizes.size() == 0) {
			return null;
		}
		int length = sizes.size();
		if (length > 1) {
			return sizes.get(length / 2 - 1);
		} else {
			return sizes.get(0);
		}
	}

	/**
	 * 找到视频采集分辨率
	 *
	 * @param parameters Parameters
	 * @return Point
	 */
	@SuppressLint("NewApi")
	public static Point getVideoSizeValue(Parameters parameters) {
		List<Size> sizes = null;
		Size size = null;
		// API 10以上
		if (Build.VERSION.SDK_INT > 10) {
			// 这个方法在摄像头预览和视频输出分开的时候才有返回
			sizes = parameters.getSupportedVideoSizes();
			if (sizes != null) {
				LogUtils.i(TAG, "得到的是视频录制推荐分辨率");
				// getSupportedVideoSizes返回不为空的时候这个方法才有返回值
				size = parameters.getPreferredPreviewSizeForVideo();
				// return size;
			} else {
				LogUtils.i(TAG, "得到的是预览分辨率");
				// 返回支持的预览分辨率中的最小值
				sizes = parameters.getSupportedPreviewSizes();
				size = findMinSize(sizes);
			}
		}// API 10及以下
		else {
			LogUtils.i(TAG, "得到的是预览分辨率");
			// 返回支持的预览分辨率中的最小值
			sizes = parameters.getSupportedPreviewSizes();
			size = findMinSize(sizes);
		}
		return new Point(size.width, size.height);
	}

	/**
	 * 找到和屏幕比例一样的分辨率
	 * <p/>
	 * 如果有多个，则返回中间大小的分辨率
	 *
	 * @param sizes            List
	 * @param screenResolution Point
	 * @return Size
	 */
	private static Size findEquivalentScreenRateSize(List<Size> sizes,
	                                                 Point screenResolution) {
		if (sizes == null || sizes.size() == 0) {
			return null;
		}
		if (screenResolution == null || screenResolution.x == 0
				|| screenResolution.y == 0) {
			return null;
		}

		float screenRate = screenResolution.x < screenResolution.y ? (float) screenResolution.x
				/ screenResolution.y
				: (float) screenResolution.y / screenResolution.x;
		LogUtils.i(TAG, "屏幕比率-->" + screenRate);
		List<Size> rateSizes = new ArrayList<Size>();
		// 找到和屏幕比例一样的分辨率
		for (Size size : sizes) {
			LogUtils.i(TAG, size.width + " x " + size.height);
			float rate = size.width < size.height ? (float) size.width
					/ size.height : (float) size.height / size.width;
			if (screenRate == rate) {
				rateSizes.add(size);
			}
		}
		// 找到集合中间大小的分辨率
		return findMidSize(rateSizes);

	}

	/**
	 * 获取录像分辨率
	 * <p/>
	 * 在预览分辨率中找到和屏幕分辨率比例一样的中间值
	 *
	 * @param parameters Parameters
	 * @return
	 */
	public static Point getMidVideoSizeValue(Parameters parameters) {
		Size size = findMidSize(parameters.getSupportedPreviewSizes());
		return new Point(size.width, size.height);
	}

	/**
	 * 获取录像分辨率
	 * <p/>
	 * 在预览分辨率中找到最小的分辨率
	 *
	 * @return Point
	 */
	public static Point getMinVideoSizeValue(Parameters parameters) {
		Size size = findMinSize(parameters.getSupportedPreviewSizes());
		return new Point(size.width, size.height);
	}

	/**
	 * 找到视频录制分辨率<br>
	 * 这个方法找到和屏幕比率一样的分辨率
	 *
	 * @param parameters       Parameters
	 * @param screenResolution Point
	 * @return Point
	 */
	@SuppressLint("NewApi")
	public static Point getVideoSizeValue(Parameters parameters,
	                                      Point screenResolution) {
		List<Size> sizes = null;
		Size size = null;
		// API 10以上
		if (Build.VERSION.SDK_INT > 10) {
			// 这个方法在摄像头预览和视频输出分开的时候才有返回
			sizes = parameters.getSupportedVideoSizes();
			if (sizes != null) {
				LogUtils.i(TAG, "得到的是视频录制推荐分辨率");
				// getSupportedVideoSizes返回不为空的时候这个方法才有返回值
				size = parameters.getPreferredPreviewSizeForVideo();
			} else {
				LogUtils.i(TAG, "得到的是预览分辨率");
				// 返回支持的预览分辨率中和屏幕分辨率比例一样的中间值
				sizes = parameters.getSupportedPreviewSizes();
				size = findEquivalentScreenRateSize(sizes, screenResolution);
			}
		}
		// API 10及以下
		else {
			LogUtils.i(TAG, "得到的是预览分辨率");
			// 返回支持的预览分辨率中和屏幕分辨率比例一样的中间值
			sizes = parameters.getSupportedPreviewSizes();
			size = findEquivalentScreenRateSize(sizes, screenResolution);
		}
		if (size != null) {
			return new Point(size.width, size.height);

		}
		return screenResolution;
	}


}
