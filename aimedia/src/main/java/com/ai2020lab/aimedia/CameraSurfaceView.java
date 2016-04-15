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
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ai2020lab.aiutils.common.LogUtils;

/**
 * 自定义相机SurfaceView类<br>
 * 用于摄像头画面预览等操作<br>
 * 建议在所有使用相机的地方都使用这个自定义的SurfaceView
 * <p/>
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class CameraSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	/**
	 * 日志标题
	 */
	private final static String TAG = CameraSurfaceView.class.getSimpleName();
	/**
	 * SurfaceHolder对象的引用
	 */
	private SurfaceHolder mHolder;

	/**
	 * SurfaceView状态改变监听
	 */
	private OnSurfaceStateChangeListener onSurfaceStateChangeListener;

	/**
	 * 构造方法
	 * <p/>
	 *
	 * @param context Context
	 */
	public CameraSurfaceView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 * <p/>
	 *
	 * @param context  Context
	 * @param attrs    AttributeSet
	 * @param defStyle int
	 */
	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 构造方法
	 * <p/>
	 *
	 * @param context Context
	 * @param attrs   AttributeSet
	 */
	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化各个参数
	 */
	@SuppressWarnings("deprecation")
	private void init(Context context) {
		// mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setKeepScreenOn(true);
		// deprecated setting, but required on Android versions prior to 3.0
		// 经测试发现Android 4.0.3以下没有废弃
		if (VERSION.SDK_INT < 15) {
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	/**
	 * 绑定监听事件
	 *
	 * @param onSurfaceStateChangeListener OnSurfaceStateChangeListener
	 */
	public void setOnSurfaceStateChangeListener(
			OnSurfaceStateChangeListener onSurfaceStateChangeListener) {
		this.onSurfaceStateChangeListener = onSurfaceStateChangeListener;
	}

	/**
	 * surface创建
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		LogUtils.d(TAG, "surfaceView创建");
		if (onSurfaceStateChangeListener != null) {
			onSurfaceStateChangeListener.surfaceCreated(holder);
		}
	}

	/**
	 * surface改变
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
	                           int height) {
		LogUtils.d(TAG, "surfaceView改变");
		// 预览的surface不存在则不作操作并返回
		if (mHolder.getSurface() == null) {
			return;
		}
		// 停止预览，改变设置，并重新开始预览
		if (onSurfaceStateChangeListener != null) {
			onSurfaceStateChangeListener.surfaceChanged(holder, format, width,
					height);
		}
	}

	/**
	 * surface销毁
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		LogUtils.d(TAG, "surfaceView销毁");
		// 停止预览并释放照相机资源
		if (onSurfaceStateChangeListener != null) {
			onSurfaceStateChangeListener.surfaceDestroyed(holder);
		}
	}

	/**
	 * SurfaceView状态监听
	 */
	public interface OnSurfaceStateChangeListener {
		/**
		 * surface创建<br>
		 * 在这里初始化相机
		 *
		 * @param holder SurfaceHolder
		 */
		void surfaceCreated(SurfaceHolder holder);

		/**
		 * surface改变<br>
		 * SurfaceView旋转或者改变大小的时候的事件操作<br>
		 * 在改变之前需要停止预览
		 *
		 * @param holder SurfaceHolder
		 * @param format int
		 * @param width  int
		 * @param height int
		 */
		void surfaceChanged(SurfaceHolder holder, int format, int width,
		                    int height);

		/**
		 * surface销毁<br>
		 * 这个方法留空，在Activity中释放照相机资源
		 *
		 * @param holder SurfaceHolder
		 */
		void surfaceDestroyed(SurfaceHolder holder);
	}

	// public void surfaceCreated(SurfaceHolder holder) {
	// // The Surface has been created, now tell the camera where to draw the
	// // preview.
	// // try {
	// // mCamera.setPreviewDisplay(holder);
	// // mCamera.startPreview();
	// // } catch (IOException e) {
	// // Log.d(TAG, "Error setting camera preview: " + e.getMessage());
	// // }
	// }
	//
	// public void surfaceDestroyed(SurfaceHolder holder) {
	// // empty. Take care of releasing the Camera preview in your activity.
	// }
	//
	// public void surfaceChanged(SurfaceHolder holder, int format, int w, int
	// h) {
	// // If your preview can change or rotate, take care of those events here.
	// // Make sure to stop the preview before resizing or reformatting it.
	//
	// if (mHolder.getSurface() == null) {
	// // preview surface does not exist
	// return;
	// }
	//
	// // stop preview before making changes
	// // try {
	// // mCamera.stopPreview();
	// // } catch (Exception e) {
	// // // ignore: tried to stop a non-existent preview
	// // }
	//
	// // set preview size and make any resize, rotate or
	// // reformatting changes here
	//
	// // start preview with new settings
	// // try {
	// // mCamera.setPreviewDisplay(mHolder);
	// // mCamera.startPreview();
	// //
	// // } catch (Exception e) {
	// // Log.d(TAG, "Error starting camera preview: " + e.getMessage());
	// // }
	// }
}
