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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build.VERSION;
import android.view.SurfaceHolder;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.image.ImageUtils;
import com.ai2020lab.aiutils.storage.FileUtils;

import java.io.File;

/**
 * 摄像头管理类
 * <p/>
 * 使用方法:<br>
 * 1.打开摄像头CameraManager.openDriver()<br>
 * 这个方法会返回打开摄像头的状态，状态信息在类CameraStatus中定义<br>
 * 3.开始预览CameraManager.startPreview(surfaceHolder),需要传入一个surfaceHolder对象的引用<br>
 * 4.停止预览CameraManager.stopPreview();<br>
 * 5.关闭摄像头释放资源CameraManager.closeDriver();
 * <p/>
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public final class CameraManager {
	/**
	 * 日志标题
	 */
	private static final String TAG = CameraManager.class.getSimpleName();
	/**
	 * CameraManager对象
	 */
	private static CameraManager cameraManager = null;
	private final Context context;
	/**
	 * 摄像头配置管理器
	 */
	private final CameraConfigurationManager configManager;
	/**
	 * 摄像头对象的引用
	 */
	private Camera camera;

	/**
	 * 是否初始化摄像头标志位，true-已经初始化，false-没有初始化
	 */
	private boolean initialized = false;
	/**
	 * 是否正在预览标志位，true-正在预览，false-没有预览
	 */
	private boolean previewing = false;

	/**
	 * 私有化构造方法
	 * <p/>
	 * 初始化上下文引用和相机配置类对象
	 *
	 * @param context 上下文引用
	 */
	private CameraManager(Context context) {
		this.context = context;
		this.configManager = new CameraConfigurationManager(context);
	}

	/**
	 * 工厂方法得到相机管理类单例,并初始化相机管理类对象
	 *
	 * @param context 上下文引用
	 */
	public synchronized static CameraManager getInstance(Context context) {
		if (cameraManager == null) {
			cameraManager = new CameraManager(context);
		}
		return cameraManager;

	}

	/**
	 * 获取当前摄像头是否正处于预览状态中
	 *
	 * @return true-正在预览，false-没有预览
	 */
	public boolean getIsPreviewing() {
		return previewing;
	}

	/**
	 * 得到当前上下文引用
	 *
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * 得到摄像头对象
	 *
	 * @return
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * 得到预览分辨率
	 *
	 * @return
	 */
	public Point getPreViewResolution() {
		return configManager.getPreViewResolution();
	}

	/**
	 * 得到视频录制分辨率
	 *
	 * @return
	 */
	public Point getVideoResolution() {
		return configManager.getVideoResolution();
	}

	/**
	 * 检查设备是否有可用的摄像头
	 *
	 * @return true-有可用的摄像头，false-没有可用的摄像头
	 */
	private boolean checkCamera() {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 遍历找到可用的摄像头id<br>
	 * 默认获取后置摄像头来录像<br>
	 * 大于API 9 可用
	 *
	 * @return 返回摄像头的ID, 多余一个摄像头的情况返回找到的第一个后置摄像头ID，只有一个则返回当前摄像头ID，没有找到返回-1
	 */
	@SuppressLint("NewApi")
	private int getDefCameraId() {
		int defCameraId = -1;
		CameraInfo cameraInfo = new CameraInfo();
		// 获取本设备上可用的摄像头总数
		int cameraCount = Camera.getNumberOfCameras();
		LogUtils.i(TAG, "本机摄像头总数--->" + cameraCount);
		// 多余一个的情况，则默认使用第一个后置摄像头
		if (cameraCount > 1) {
			// 默认为后置摄像头
			for (int i = 0; i < cameraCount; i++) {
				Camera.getCameraInfo(i, cameraInfo);
				// 后置摄像头
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
					// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
					defCameraId = i;
					break;
				}
			}
		}
		// 只有一个则使用第一个即可
		else {
			defCameraId = 0;
		}
		return defCameraId;
	}

	/**
	 * 打开摄像头方法<br>
	 * 打开摄像头方法，返回的摄像头状态在CameraStatus类中定义
	 *
	 * @return 返回打开摄像头的状态
	 */
	@SuppressLint("NewApi")
	private int openCamera() {
		int result = CameraStatus.OPEN_FAIL;
		try {
			// API 9 及以上的打开方法
			if (VERSION.SDK_INT > 8) {
				// 得到相机默认ID
				int cameraId = getDefCameraId();
				if (cameraId != -1) {
					CameraInfo cameraInfo = new CameraInfo();
					Camera.getCameraInfo(cameraId, cameraInfo);
					if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
						result = CameraStatus.FACING_BACK;
					} else if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
						result = CameraStatus.FACING_FRONT;
					}
					camera = Camera.open(cameraId);
				}
			}
			// API 9 及以下打开默认的后置第一个相机
			else {
				result = CameraStatus.FACING_BACK;
				camera = Camera.open();
			}
			// 测试异常情况
			// throw new Exception();
		} catch (Exception e) {
			closeDriver();
			result = CameraStatus.OPEN_FAIL;
			LogUtils.e(TAG, "相机打开异常", e);
		}
		return result;
	}

	/**
	 * 打开摄像头,并配置摄像头参数<br>
	 * 注意:<br>
	 * 返回打开的摄像头的状态，状态码在常量类CameraStatus中定义<br>
	 * 客户端可通过返回的状态来判断相机是否正确打开并提示用户或者做其他的操作
	 *
	 * @return 返回打开摄像头的状态
	 */
	public int openDriver() {
		int result = CameraStatus.INVALID;
		// 检查是否有摄像头
		if (checkCamera()) {
			if (camera == null) {
				// 打开摄像头
				result = openCamera();
				LogUtils.i(TAG, "打开摄像头的状态-->" + result);
				if (camera != null) {
					// 如果没有初始化则配置初始化参数
					if (!initialized) {
						initialized = true;
						configManager.initFromCameraParameters(camera);
					}
					// 设置相机参数
					configManager.setDesiredCameraParameters(camera);
				}
			}
		}
		return result;
	}

	/**
	 * 计算并设置摄像头参数，在此之前必须调用openDriver(surfaceHolder)方法确保Camera已经初始化，否则无效
	 */
	public void setParameters() {
		if (camera != null) {
			// 计算相机参数
			configManager.initFromCameraParameters(camera);
			// 设置相机参数
			configManager.setDesiredCameraParameters(camera);
		}
	}

	/**
	 * 关闭摄像头,当界面回到后台的时候调用
	 */
	public void closeDriver() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	/**
	 * 开始预览
	 */
	public void startPreview(SurfaceHolder holder) {
		if (camera != null && !previewing) {
			try {
				// 设置相机预览
				camera.setPreviewDisplay(holder);
				// 开始预览
				camera.startPreview();
				previewing = true;
			} catch (Exception e) {
				LogUtils.e(TAG, "开始预览异常", e);
			}
		}
	}

	/**
	 * 停止预览
	 */
	public void stopPreview() {
		if (camera != null && previewing) {
			try {
				camera.stopPreview();
				previewing = false;
			} catch (Exception e) {
				LogUtils.e(TAG, "停止预览异常", e);
			}
		}
	}

	/**
	 * 拍照
	 *
	 */
	public void takePicture(String filePath, String fileName,
	                        TakePictureCallback takePictureCallback) {
		if (camera != null && previewing && FileUtils.makeDir(filePath)) {
			String path = filePath + File.separator + fileName;
			LogUtils.i(TAG, "--处理照片路径-->" + path);
			// TODO:需要实现第一个参数的接口，拍照的同时播放咔嚓声音
			camera.takePicture(null, null, new HandleJpegCallback(path, takePictureCallback));
		}
	}

	/**
	 * 拍照并处理照片
	 */
	private class HandleJpegCallback implements Camera.PictureCallback {
		String filePath;
		TakePictureCallback takePictureCallback;

		public HandleJpegCallback(String filePath, TakePictureCallback takePictureCallback) {
			this.filePath = filePath;
			this.takePictureCallback = takePictureCallback;
		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				Bitmap bitmap = null;
				if (data != null) {
					bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					// 停止预览
					camera.startPreview();
					previewing = false;
				}
				if (bitmap != null) {
					// 保存图片
					ImageUtils.saveBitmap(ImageUtils.getRotateBitmap(bitmap, 90), filePath);
					// 再次开始预览
					camera.startPreview();
					previewing = true;
					if (takePictureCallback != null)
						takePictureCallback.onPictureSaved(filePath);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, "拍照异常", e);
			}
		}
	}

	public interface TakePictureCallback {
		void onPictureSaved(String path);
	}

}
