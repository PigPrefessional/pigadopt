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
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;

import com.ai2020lab.aiutils.common.LogUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 摄像头参数配置类
 * <p/>
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
final class CameraConfigurationManager {
	/**
	 * 日志标题
	 */
	private static final String TAG = CameraConfigurationManager.class
			.getSimpleName();
	/**
	 * 正则表达式表现模式
	 */
	private static final Pattern COMMA_PATTERN = Pattern.compile(",");
	/**
	 * 上下文引用
	 */
	private final Context context;

	/**
	 * 屏幕分辨率
	 */
	private Point screenResolution;
	/**
	 * 摄像头预览分辨率
	 */
	private Point previewResolution;
	/**
	 * 摄像头录像使用分辨率
	 */
	private Point videoResolution;

	/**
	 * 预览格式
	 */
	private int previewFormat;
	private String previewFormatString;

	/**
	 * 构造方法
	 * <p/>
	 *
	 * @param context Context
	 */
	CameraConfigurationManager(Context context) {
		this.context = context;
		getScreenResolution(context);
	}

	/**
	 * 得到相机预览分辨率<br>
	 * 根据相机参数和屏幕分辨率计算得到
	 *
	 * @param parameters
	 * @param screenResolution
	 * @return
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
	 *
	 * @param previewSizeValueString
	 * @param screenResolution
	 * @return
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
		if (VERSION.SDK_INT > 10) {
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
	 * @param sizes List
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
	 * @param parameters Parameters
	 * @param screenResolution Point
	 * @return Point
	 */
	@SuppressLint("NewApi")
	public static Point getVideoSizeValue(Parameters parameters,
	                                      Point screenResolution) {
		List<Size> sizes = null;
		Size size = null;
		// API 10以上
		if (VERSION.SDK_INT > 10) {
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

	/**
	 * 得到预览分辨率
	 *
	 * @return Point
	 */
	public Point getPreViewResolution() {
		return previewResolution;
	}

	/**
	 * 得到视频拍摄分辨率
	 *
	 * @return Point
	 */
	public Point getVideoResolution() {
		return videoResolution;
	}

	/**
	 * 获取屏幕分辨率,构造方法初始化的时候加载,只获取一次<br>
	 */
	private void getScreenResolution(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		screenResolution = new Point(dm.widthPixels, dm.heightPixels);
		LogUtils.i(TAG, "屏幕分辨率: " + screenResolution);

	}

	/**
	 * 初始化相机配置参数，根据设备屏幕分辨率计算得到<br>
	 *
	 * @param camera Camera
	 */
	void initFromCameraParameters(Camera camera) {
		// 得到相机参数
		Parameters parameters = camera.getParameters();
		previewFormat = parameters.getPreviewFormat();
		previewFormatString = parameters.get("preview-format");
		LogUtils.i(TAG, "默认预览格式: " + previewFormat + '/'
				+ previewFormatString);
		// 得到屏幕分辨率
		// WindowManager manager = (WindowManager) context
		// .getSystemService(Context.WINDOW_SERVICE);
		// Display display = manager.getDefaultDisplay();
		//
		// DisplayMetrics dm = context.getResources().getDisplayMetrics();
		// // getSize方法从API 13开始
		// if (VERSION.SDK_INT >= 13) {
		// screenResolution = new Point();
		// display.getSize(screenResolution);
		// }
		// // 13以下使用老方法
		// else {
		// screenResolution = new Point(display.getWidth(),
		// display.getHeight());
		// }
		// DisplayMetrics dm = context.getResources().getDisplayMetrics();
		// screenResolution = new Point(dm.widthPixels, dm.heightPixels);
		// System.out.println("屏幕分辨率: " + screenResolution);
		// System.out.println("屏幕分辨率:  " + dm.widthPixels + ","
		// + dm.heightPixels);
		// 得到相机分辨率
		previewResolution = getPreviewResolution(parameters, screenResolution);
		// 得到相机录像分辨率
		videoResolution = getVideoSizeValue(parameters);

		// videoResolution = getVideoSizeValue(parameters, screenResolution);
		// videoResolution = getMidVideoSizeValue(parameters);
		// videoResolution = getMidVideoSizeValue(parameters, screenResolution);
		LogUtils.i(TAG, "预览分辨率: " + previewResolution);
		LogUtils.i(TAG, "录像分辨率: " + videoResolution);

	}

	/**
	 * 设置相机参数
	 *
	 * @param camera
	 */
	void setDesiredCameraParameters(Camera camera) {
		Parameters parameters = camera.getParameters();
		// 设置相机预览分辨率
		parameters.setPreviewSize(previewResolution.x, previewResolution.y);
		// setFlash(parameters);
		// setZoom(parameters);
		// // setSharpness(parameters);
		// // modify here
		//
		// // camera.setDisplayOrientation(90);
		// 如何计算得到正确的旋转角度？
		setDisplayOrientation(camera, 90);
		camera.setParameters(parameters);
	}

	/**
	 * compatible 1.6
	 *
	 * @param camera
	 * @param angle
	 */
	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[]{
							int.class
					});
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[]{
						angle
				});
		} catch (Exception e1) {
		}
	}

}
