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
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build.VERSION;
import android.view.Surface;

import com.ai2020lab.aiutils.common.LogUtils;


/**
 * 视频录制工具类
 * <p/>
 * 使用方法:<br>
 * 1.调用boolean prepareRecorder(Camera camera, Surface surface, Point resolution,
 * String videoPath)方法初始化录像机<br>
 * 2.调用boolean startRecorder(Camera camera)方法开始录像<br>
 * 3.调用boolean stopRecorder(Camera camera)方法停止录像<br>
 * <p/>
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class VideoRecorderManager {
	/**
	 * 日志标题
	 */
	private static final String TAG = VideoRecorderManager.class
			.getSimpleName();
	/** MediaRecorder的持续时间 */
	// private final static int DURATION = 1000 * 5 * 5;

	/**
	 * MediaRecorder当前状态,初始化的时候为IDLE
	 */
	private int status = MediaRecorderStatus.IDLE;

	/**
	 * MediaRecorder对象的引用
	 */
	private MediaRecorder recorder;
	/**
	 * 音频文件保存路径
	 */
	private String videoPath;
	/**
	 * Surface对象
	 */
	private Surface surface;
	/**
	 * 摄像头对象
	 */
	private Camera camera;
	/**
	 * 拍摄使用分辨率
	 */
	private Point resolution;

	private VideoRecorderManager() {
	}

	/**
	 * 工厂方法
	 *
	 * @return 返回MediaRecorderManager对象的引用
	 */
	public static VideoRecorderManager getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * 得到MediaRecorder当前状态
	 * <p/>
	 * prepare()方法调用成功返回状态PREPARE,start()方法调用成功返回状态START<br>
	 * stop()方法调用成功返回状态STOP,3个方法调用产生异常和还没有调用任何方法时候返回状态IDLE
	 *
	 * @return 返回MediaRecorder的当前状态, 返回状态码在常量类MediaRecorderStatus中定义
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 得到Camera对象的引用
	 *
	 * @return
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * 得到MediaRecorder对象的引用
	 *
	 * @return
	 */
	public MediaRecorder getRecorder() {
		return recorder;
	}

	/**
	 * 得到视频文件保存路径
	 *
	 * @return
	 */
	public String getVideoPath() {
		return videoPath;
	}

	/**
	 * 设置参数
	 *
	 * @param camera    摄像头对象
	 * @param surface   surface对象
	 * @param videoPath 视频文件保存路径
	 * @return boolean
	 */
	private boolean setParameters(Camera camera, Surface surface,
	                              Point resolution, String videoPath) {
		if (camera == null) {
			return false;
		}
		if (surface == null) {
			return false;
		}
		if (resolution == null) {
			return false;
		}
		if (videoPath == null || videoPath.equals("")) {
			return false;
		}
		if (this.camera == null) {
			this.camera = camera;
		} else {
			return false;
		}
		if (this.surface == null) {
			this.surface = surface;
		} else {
			return false;
		}
		if (this.resolution == null) {
			this.resolution = resolution;
		} else {
			return false;
		}
		if (this.videoPath == null) {
			this.videoPath = videoPath;
		} else {
			return false;
		}
		LogUtils.d(TAG, "录像参数设置成功");
		return true;
	}

	/**
	 * 锁定或解锁相机<br>
	 * API 14以下需要调用,API 14开始系统自动调用
	 *
	 * @param camera Camera对象
	 * @param isLock true-锁定，false-解锁
	 * @return
	 */
	private boolean lockOrUnlockCamera(Camera camera, boolean isLock) {
		try {
			// 锁定
			if (isLock) {
				camera.lock();
				LogUtils.d(TAG, "锁定摄像头");
			}
			// 解锁
			else {
				camera.unlock();
				LogUtils.d(TAG, "解锁摄像头");
			}

		} catch (Exception e) {
			LogUtils.e(TAG, "锁定或解锁摄像头异常", e);
			return false;
		}
		return true;
	}

	/**
	 * 设置CamcorderProfile,需要API 8以及以上,API 8以下使用老方法
	 *
	 * @param recorder
	 */
	private void setMediaRecorderProfile(MediaRecorder recorder) {
		if (VERSION.SDK_INT > 7) {
			recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
		} else {
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		}
	}

	/**
	 * 设置视频播放的方向<br>
	 * API 8以上有效
	 *
	 * @param recorder
	 */
	@SuppressLint("NewApi")
	private boolean setOrientationHint(MediaRecorder recorder) {
		if (VERSION.SDK_INT > 8) {
			try {
				recorder.setOrientationHint(90);
			} catch (Exception e) {
				LogUtils.e(TAG, "设置视频播放方向异常", e);
				return false;
			}
		}
		return true;
	}

	/**
	 * 初始化录像
	 */
	private boolean initRecorder() {
		try {

			if (this.recorder == null) {
				// 初始化MediaRecorder
				recorder = new MediaRecorder();
			} else {
				recorder.reset();
			}
			// 解锁摄像头
			if (!lockOrUnlockCamera(camera, false)) {
				return false;
			}
			// 设置视频播放方向
			if (!setOrientationHint(recorder)) {
				return false;
			}
			// 设置摄像头为录像源
			recorder.setCamera(camera);
			// 指定音频源和视频源
			recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// 指定CamcorderProfile
			setMediaRecorderProfile(recorder);
			LogUtils.d(TAG, "录像保存路径->" + videoPath);
			int width = resolution.x > resolution.y ? resolution.x
					: resolution.y;
			int height = resolution.x > resolution.y ? resolution.y
					: resolution.x;
			LogUtils.d(TAG, "录像分辨率宽->" + width);
			LogUtils.d(TAG, "录像分辨率高->" + height);
			recorder.setVideoSize(width, height);
			// 指定输入文件的路径
			recorder.setOutputFile(videoPath);
			// 指定预览输出
			recorder.setPreviewDisplay(surface);

			// recorder.setMaxDuration(DURATION);
			// recorder.setOnInfoListener(new OnInfoListener() {
			//
			// @Override
			// public void onInfo(MediaRecorder mr, int what, int extra) {
			// if(what ==
			// MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
			// System.out.println("达到了MediaRecorder最大会话时间");
			// ret = false;
			// }
			// }
			// });
			recorder.prepare();
			return true;
		} catch (Exception e) {
			LogUtils.e(TAG, "初始化录像异常", e);
		}
		return false;
	}

	/**
	 * 初始化录像
	 * <p/>
	 * 初始化录像失败自动释放资源<br>
	 * 初始化录像成功,MediaRecorder状态将迁移到PREPARE;初始化录像失败,MediaRecorder状态将迁移到IDLE
	 *
	 * @param camera    摄像头对象
	 * @param surface   surface对象
	 * @param videoPath 视频文件保存路径
	 * @return true-初始化成功，false-初始化失败
	 */
	public boolean prepareRecorder(Camera camera, Surface surface,
	                               Point resolution, String videoPath) {
		if (!setParameters(camera, surface, resolution, videoPath)) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			LogUtils.d(TAG, "录像参数设置失败");
			return false;
		}
		if (!initRecorder()) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			return false;
		}
		status = MediaRecorderStatus.PREPARE;
		LogUtils.d(TAG, "初始化录像");
		return true;
	}

	/**
	 * 开始录像
	 * <p/>
	 * 开始录像前检查传入的Camera对象是否是初始化时候设置的Camera对象,如果不一致则不能成功启动录像<br>
	 * 开始录像失败自动释放资源<br>
	 * 开始录像成功,MediaRecorder状态将迁移到START;开始录像失败,MediaRecorder状态将迁移到IDLE
	 *
	 * @param camera 摄像头对象的引用
	 * @return true-开始录像成功，false-开始录像失败
	 */
	public boolean startRecorder(Camera camera) {
		if (this.camera != camera) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			LogUtils.d(TAG, "传入Camera对象不一致，开始录像失败");
			return false;
		}
		try {
			if (recorder != null) {
				recorder.start();
				status = MediaRecorderStatus.START;
				LogUtils.d(TAG, "开始录像");
				return true;
			} else {
				releaseRecorder();
				status = MediaRecorderStatus.IDLE;
				LogUtils.d(TAG, "MediaRecorder对象为空，开始录像失败");
				return false;
			}
		} catch (Exception e) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			LogUtils.e(TAG, "开始录像异常", e);
			return false;
		}
	}

	/**
	 * 停止录像
	 * <p/>
	 * 停止录像前检查传入的Camera对象是否是初始化时候设置的Camera对象,如果不一致则不能成功停止录像<br>
	 * 停止录像失败或成功都会自动释放资源<br>
	 * 停止录像成功,MediaRecorder状态将迁移到STOP;停止录像失败,MediaRecorder状态将迁移到IDLE
	 *
	 * @param camera 摄像头对象的引用
	 * @return true-停止录像成功，false-停止录像失败
	 */
	public boolean stopRecorder(Camera camera) {
		if (this.camera != camera) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			LogUtils.d(TAG, "传入Camera对象不一致，停止录像失败");
			return false;
		}
		try {
			if (recorder != null) {
				recorder.stop();
				releaseRecorder();
				status = MediaRecorderStatus.STOP;
				LogUtils.d(TAG, "停止录像");
				return true;
			} else {
				releaseRecorder();
				status = MediaRecorderStatus.IDLE;
				LogUtils.d(TAG, "MediaRecorder对象为空，停止录像失败");
				return false;
			}
		} catch (Exception e) {
			releaseRecorder();
			status = MediaRecorderStatus.IDLE;
			LogUtils.e(TAG, "停止录像异常", e);
			return false;
		}
	}

	/**
	 * 释放资源并重置参数
	 */
	private void releaseRecorder() {
		if (recorder != null) {
			recorder.reset();
			recorder.release();
			LogUtils.d(TAG, "释放MediaRecorder对象资源");
		}
		// 锁定摄像头
		if (camera != null) {
			lockOrUnlockCamera(camera, true);
		}
		// 重置参数
		resetParameters();
	}

	/**
	 * 重置参数
	 */
	private void resetParameters() {
		this.recorder = null;
		this.camera = null;
		this.videoPath = null;
		this.surface = null;
		this.resolution = null;
		LogUtils.d(TAG, "重置录像参数");
	}

	private static class SingletonHolder {
		public static VideoRecorderManager instance = new VideoRecorderManager();
	}

}
