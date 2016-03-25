/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aimedia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.TimeUtils;
import com.ai2020lab.aiutils.image.ImageUtils;
import com.ai2020lab.aiutils.storage.FileUtils;
import com.ai2020lab.aiutils.storage.StorageUtils;
import com.ai2020lab.aiutils.system.DeviceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * 调用系统自带相机拍照管理工具类
 * <p/>
 * 使用方法:<br>
 * 1.调用下面的方法打开系统相机，界面将跳转到系统相机界面
 * <ul><li>{@link SysCameraManager#openCamera(Context, int)}</li></ul>
 * 2.在调用第一个方法openCamera的Activity的onActivityResult方法中调用下面的方法保存相片
 * <ul><li>{@link SysCameraManager#savePhoto()}</li></ul>
 * 3.在onActivityResult方法中调用下面的方法绑定保存照片监听，并在接口的回调方法中获取照片路径
 * <ul><li>{@link SysCameraManager#setOnPhotoSavedListener(SysCameraManager.OnPhotoSavedListener)}</li></ul>
 * Created by Justin on 2015/12/2.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class SysCameraManager {
	/**
	 * 处理相片的Message what
	 */
	public final static int HANDLE_PHOTO = 0x10001;
	/**
	 * 照片保存路径--
	 */
	public final static String CAMERA_PATH = "DCIM";
	/**
	 * 日志标题
	 */
	private final static String TAG = SysCameraManager.class.getSimpleName();

	private OnPhotoSavedListener onPhotoSavedListener;

	/**
	 * 照片文件
	 */
	private File file;

	private SysCameraManager() {
	}

	/**
	 * 工厂方法
	 *
	 * @return SysCameraManager
	 */
	public static SysCameraManager getInstance() {
		return Singleton.INSTANCE;
	}

	public void setOnPhotoSavedListener(OnPhotoSavedListener onPhotoSavedListener) {
		this.onPhotoSavedListener = onPhotoSavedListener;
	}

	/**
	 * 生成照片保存全路径
	 */
	private String generatePhotoPath(Context context) {
		StringBuilder photoPath = new StringBuilder();
		// 先获取外卡
		String sdPath = StorageUtils.getInstance(context).getExternalDirectory();
		LogUtils.i(TAG, "SD外卡路径-->" + sdPath);
		// 外卡没有就使用内卡
		if (TextUtils.isEmpty(sdPath)) {
			sdPath = StorageUtils.getInstance(context).getInternalDirectory();
			LogUtils.i(TAG, "SD内卡路径-->" + sdPath);
		}
		photoPath.append(sdPath).append(File.separator).append(CAMERA_PATH).append(File.separator);
		photoPath.append(TimeUtils.formatDate(new
						Date(System.currentTimeMillis()),
				TimeUtils.Template.YMDHMS_IMG));
		photoPath.append(".").append(FileUtils.ExtensionName.JPG);
		return photoPath.toString();
	}

	/**
	 * 创建照片文件
	 */
	private boolean createPhotoFile(Context context) {
		if (context == null) {
			return false;
		}
		String photoPath = generatePhotoPath(context);
		LogUtils.i(TAG, "照片文件路径--->" + photoPath);
		file = new File(photoPath);
		if (!file.exists()) {
			// 创建照片文件
//			FileUtils.createNewFile(photoPath);
			FileUtils.makeDir(file.getParent());
		}
//		return !(file == null || !file.exists());
		return true;
	}

	/**
	 * 打开系统自带相机
	 *
	 * @param context     上下文引用
	 * @param requestCode 打开系统相机请求码
	 */
	public void openCamera(Context context, int requestCode) {
		if (!createPhotoFile(context)) {
			LogUtils.i(TAG, "创建相片文件失败，操作停止");
			return;
		}
		// 调用系统Camera
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 设置照片保存路径，以便返回全尺寸的照片文件
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		// ensure that there's a camera activity to handle the intent
		if (cameraIntent.resolveActivity(context.getPackageManager()) == null) {
			LogUtils.i(TAG, "Intent无法处理，结束");
//			Toast.makeText(context, "Intent无法处理，结束", Toast.LENGTH_LONG).show();
			return;
		}
		// TODO:判断小米系统
		getSystemInfo();
		// 小米手机需要手动在安全中心中授权,否则会报错
		((Activity) context).startActivityForResult(cameraIntent, requestCode);
	}

	public String getPhotoPath(){
		if(file == null){
			return null;
		}
		return file.getPath();
	}

	/**
	 * 处理拍照返回结果 在onActivityResult中调用
	 */
	public void savePhoto() {
		LogUtils.i(TAG, "------------保存拍照返回的照片-----------------");
		if (file == null) {
			LogUtils.i(TAG, "文件不存在，操作终止");
			return;
		}
		final Handler mHandler = new mHandler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					FileOutputStream fos = new FileOutputStream(file);
					Bitmap photo = ImageUtils.getBitmapFromSDCard(file.getPath(), 2);
					assert photo != null;
					if (photo.compress(CompressFormat.JPEG, 100, fos)) {
						fos.flush();
						fos.close();
					}
					Message msg = mHandler.obtainMessage(HANDLE_PHOTO,
							photo);
					mHandler.sendMessage(msg);
				} catch (FileNotFoundException e) {
					LogUtils.e(TAG, "FileNotFoundException", e);
				} catch (IOException e) {
					LogUtils.e(TAG, "IOException", e);
				}

			}
		};
		new Thread(runnable).start();
	}

	public interface OnPhotoSavedListener {

		void onPhotoSaved(String path);
	}

	private static class Singleton {
		public final static SysCameraManager INSTANCE = new SysCameraManager();
	}

	/**
	 * 照片处理完成后的操作
	 *
	 * @author Justin Z
	 */
	@SuppressLint("HandlerLeak")
	private class mHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				// 照片保存完成之后的操作
				case HANDLE_PHOTO:
					Bitmap bitmap = (Bitmap) msg.obj;
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();
						bitmap = null;
					}
					// 回调照片路径
					if (onPhotoSavedListener != null) {
						onPhotoSavedListener.onPhotoSaved(file.getPath());
					}
					break;
			}
		}
	}

	// TODO:测试获取系统的一些属性
	private void getSystemInfo() {
		LogUtils.i(TAG, "固件版本-->" + DeviceUtils.getFirmwareVersion());
		LogUtils.i(TAG, "操作系统版本-->" + DeviceUtils.getSysVersion());
		LogUtils.i(TAG, "设备型号-->" + DeviceUtils.getModel());
		LogUtils.i(TAG, "制造商-->" + DeviceUtils.getManufacturer());
		LogUtils.i(TAG, "cpu型号-->" + DeviceUtils.getCpuName());

	}

}
