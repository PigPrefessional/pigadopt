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
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build.VERSION;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;

import com.ai2020lab.aimedia.interfaces.PhotoTakenCallback;
import com.ai2020lab.aimedia.model.CameraStatus;
import com.ai2020lab.aimedia.model.FlashMode;
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.system.DeviceUtils;

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
	private Context context;
	/**
	 * 摄像头配置管理器
	 */
	private final CameraSettingManager configManager;
	/**
	 * 摄像头对象的引用
	 */
	private Camera camera;

	/**
	 * 是否正在预览标志位，true-正在预览，false-没有预览
	 */
	private boolean previewing = false;
	/**
	 * 当前打开的相机id
	 */
	private int cameraId;
	/**
	 * 照片旋转方向
	 */
	private int outputOrientation;

	private OrientationEventListener orientationListener;
	/**
	 * 拍照监听
	 */
	private PhotoTakenCallback photoTakenCallback;

	/**
	 * 私有化构造方法
	 * <p/>
	 * 初始化相机配置类对象
	 */
	private CameraManager() {
		this.configManager = new CameraSettingManager();
	}

	/**
	 * 工厂方法得到相机管理类单例,并初始化相机管理类对象
	 */
	public synchronized static CameraManager getInstance() {
		if (cameraManager == null) {
			cameraManager = new CameraManager();
		}
		return cameraManager;

	}

	/**
	 * 获取当前摄像头是否正处于预览状态中
	 *
	 * @return true-正在预览，false-没有预览
	 */
	public boolean isPreviewing() {
		return previewing;
	}

	/**
	 * 得到摄像头对象
	 *
	 * @return Camera
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * 遍历找到可用的摄像头id<br>
	 * 默认获取后置摄像头来录像<br>
	 * 大于API 9 可用
	 *
	 * @return 返回摄像头的ID, 多余一个摄像头的情况返回找到的第一个后置摄像头ID，只有一个则返回当前摄像头ID，没有找到返回-1
	 */
	@Deprecated
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
	@Deprecated
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
		} catch (Exception e) {
			closeDriver();
			result = CameraStatus.OPEN_FAIL;
			LogUtils.e(TAG, "相机打开异常", e);
		}
		return result;
	}


	/**
	 * 获取相机id
	 *
	 * @param useFrontCamera true-使用前置摄像头，false-使用后置摄像头
	 * @return 返回cameraId，只有一个摄像头会返回0
	 */
	private int getCameraId(boolean useFrontCamera) {
		int count = Camera.getNumberOfCameras();
		int result = -1;
		if (count > 0) {
			result = 0;
			Camera.CameraInfo info = new Camera.CameraInfo();
			for (int i = 0; i < count; i++) {
				Camera.getCameraInfo(i, info);
				if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK
						&& !useFrontCamera) {
					result = i;
					break;
				} else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT
						&& useFrontCamera) {
					result = i;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 打开摄像头方法，不考虑API 9 以下
	 *
	 * @param useFrontCamera true-使用前置摄像头,false-使用后置摄像头
	 * @return 返回打开的cameraId，失败返回-1
	 */
	private int openCamera(boolean useFrontCamera) {
		int result = getCameraId(useFrontCamera);
		try {
			camera = Camera.open(result);
		} catch (Exception e) {
			LogUtils.e(TAG, "打开相机失败", e);
			closeDriver();
		}
		// 赋值当前打开的相机id
		cameraId = result;
		return result;
	}

	/**
	 * 打开摄像头,并配置摄像头参数<br>
	 * 注意:<br>
	 * 返回打开的摄像头的状态，状态码在常量类CameraStatus中定义<br>
	 * 客户端可通过返回的状态来判断相机是否正确打开并提示用户或者做其他的操作
	 *
	 * @param context Context
	 * @return 返回打开摄像头的状态
	 */
	@Deprecated
	public int openDriver(Context context) {
		this.context = context;
		int result = CameraStatus.INVALID;
		// 检查是否有摄像头
		if (DeviceUtils.isSupportedCamera(context)) {
			// 打开摄像头
			result = openCamera();
			LogUtils.i(TAG, "打开摄像头的状态-->" + result);
			if (result != CameraStatus.INVALID && result != CameraStatus.OPEN_FAIL) {
				configManager.initFromCameraParameters(context, camera);
				configManager.setDesiredCameraParameters();
			}
		}
		return result;
	}

	/**
	 * 打开摄像头
	 *
	 * @param context        Context
	 * @param useFrontCamera true-使用前置摄像头，false-使用后置摄像头
	 * @return 返回打开的摄像头id, 失败返回-1
	 */
	public int openDriver(Context context, boolean useFrontCamera) {
		this.context = context;
		int result = -1;
		// 检查是否有摄像头
		if (DeviceUtils.isSupportedCamera(context)) {
			// 打开摄像头
			result = openCamera(useFrontCamera);
			LogUtils.i(TAG, "打开摄像头的id-->" + result);
			if (result != -1) {
				// 初始化摄像头参数
				configManager.initFromCameraParameters(context, camera);
				configManager.setDesiredCameraParameters();
				// 方向监听
				if (orientationListener == null) {
					initOrientationListener(context);
				}
				orientationListener.enable();
			}
		}
		return result;
	}

	/**
	 * 获取相机是否支持闪光灯
	 *
	 * @return true-支持闪光灯，false-不支持闪光灯
	 */
	public boolean isSupportedFlash() {
		return configManager.isSupportedFlash();
	}

	/**
	 * 设置闪光灯模式
	 *
	 * @param flashMode FlashMode
	 */
	public void setFlashMode(FlashMode flashMode) {
		if (camera == null) {
			LogUtils.i(TAG, "--请先打开相机--");
			return;
		}
		LogUtils.i(TAG, "切换闪光灯模式-->" + flashMode.toString());
		configManager.setFlashMode(flashMode);
		configManager.setParameters();
	}

	/**
	 * 获取当前闪光灯模式
	 *
	 * @return FlashMode
	 */
	public FlashMode getFlashMode() {
		return CameraSettingPrefManager.getFlashMode(context);
	}

	/**
	 * 获取当前拍照分辨率
	 *
	 * @return Camera.Size
	 */
	public Camera.Size getPictureSize() {
		if (camera == null) {
			LogUtils.i(TAG, "--请先打开相机--");
			return null;
		}
		return camera.getParameters().getPictureSize();
	}

	/**
	 * 获取当前预览分辨率
	 *
	 * @return Camera.Size
	 */
	public Camera.Size getPreviewSize() {
		if (camera == null) {
			LogUtils.i(TAG, "--请先打开相机--");
			return null;
		}
		return camera.getParameters().getPreviewSize();
	}

	/**
	 * 获取当前照片分辨率同预览分辨率的缩放比
	 * @return 必须先打开相机，并且预览分辨率和照片分辨率的宽高比必须一致，否则都将返回-1
	 */
	public float getPicturePreviewRatio() {
		Camera.Size pictureSize = getPictureSize();
		Camera.Size previewSize = getPreviewSize();
		if (pictureSize == null || previewSize == null) {
			return -1f;
		}
		float pictureWidth = pictureSize.width;
		float pictureHeight = pictureSize.height;
		float previewWidth = previewSize.width;
		float previewHeight = previewSize.height;
		// 预览分辨率和照片分辨率必须比例一致才能计算照片分辨率和预览分辨率的缩放比
		if (pictureWidth / pictureHeight != previewWidth / previewHeight) {
			LogUtils.i(TAG, "--当前预览分辨率同照片分辨率比例不一致--");
			return -1f;
		}
		float ratio = pictureWidth / previewWidth;
		LogUtils.i(TAG, "当前照片分辨率同预览分辨率的缩放比为-->" + ratio);
		return ratio;
	}

	/**
	 * 关闭摄像头,当界面回到后台的时候调用
	 */
	public void closeDriver() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
		if (orientationListener != null) {
			orientationListener.disable();
			orientationListener = null;
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
	 * 拍照方法
	 *
	 * @param photoTakenCallback PhotoTakenCallback
	 */
	public void takePicture(PhotoTakenCallback photoTakenCallback) {
		if (camera == null || !previewing) {
			LogUtils.i(TAG, "--请先初始化再拍照");
			return;
		}
		this.photoTakenCallback = photoTakenCallback;
		// 对焦成功后拍照
		camera.autoFocus(new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if (success) {
					camera.takePicture(null, null, pictureCallback);
				}
				else {
					CameraManager.this.photoTakenCallback.photoTakenFailure();
				}

			}
		});
	}

	private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (photoTakenCallback != null) {
				photoTakenCallback.photoTakenSuccess(data.clone(), outputOrientation);
			}
			// 拍照过程会自动停止预览,需要手动重新开始预览
			camera.startPreview();
			previewing = true;
		}

	};

	private void initOrientationListener(Context context) {
		orientationListener = new OrientationEventListener(context) {

			@Override
			public void onOrientationChanged(int orientation) {
				if (camera != null && orientation != ORIENTATION_UNKNOWN) {
					int newOutputOrientation = getCameraPictureRotation(orientation);

					if (newOutputOrientation != outputOrientation) {
						outputOrientation = newOutputOrientation;

						Camera.Parameters params = camera.getParameters();
						params.setRotation(outputOrientation);
						try {
							camera.setParameters(params);
						} catch (Exception e) {
							LogUtils.e(TAG, "设置相机旋转方向参数异常", e);
						}
					}
				}
			}
		};
	}

	private int getCameraPictureRotation(int orientation) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation;

		orientation = (orientation + 45) / 90 * 90;

		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			rotation = (info.orientation - orientation + 360) % 360;
		} else { // back-facing camera
			rotation = (info.orientation + orientation) % 360;
		}

		return (rotation);
	}

}
