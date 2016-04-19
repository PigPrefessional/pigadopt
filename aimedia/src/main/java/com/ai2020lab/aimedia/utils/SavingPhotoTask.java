/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aimedia.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.ai2020lab.aimedia.CameraManager;
import com.ai2020lab.aimedia.interfaces.PhotoSavedListener;
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.image.ImageUtils;
import com.ai2020lab.aiutils.storage.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 保存照片异步任务
 * Created by Justin Z on 2016/4/13.
 * 502953057@qq.com
 */
public class SavingPhotoTask extends AsyncTask<Void, Void, File> {

	private final static String TAG = SavingPhotoTask.class.getSimpleName();
	private final static int COMPRESS_QUALITY = 90;

	private Context context;
	private byte[] data;
	private String name;
	private String path;
	private int orientation;
	private boolean isUpdateMedia;
	private Rect rect;
	private PhotoSavedListener callback;

	/**
	 * @param context       Context
	 * @param data          byte[]
	 * @param name          String
	 * @param path          String
	 * @param orientation   int
	 * @param isUpdateMedia boolean
	 * @param rect          Rect
	 * @param callback      PhotoSavedListener
	 */
	public SavingPhotoTask(Context context, byte[] data,
	                       String name, String path,
	                       int orientation, boolean isUpdateMedia,
	                       Rect rect, PhotoSavedListener callback) {
		this.context = context;
		this.data = data;
		this.name = name;
		this.path = path;
		this.orientation = orientation;
		this.isUpdateMedia = isUpdateMedia;
		this.rect = rect;
		this.callback = callback;
	}

	/**
	 * @param context       Context
	 * @param data          byte[]
	 * @param name          String
	 * @param path          String
	 * @param orientation   int
	 * @param isUpdateMedia boolean
	 * @param rect          Rect
	 */
	public SavingPhotoTask(Context context, byte[] data,
	                       String name, String path,
	                       int orientation, boolean isUpdateMedia,
	                       Rect rect) {
		this(context, data, name, path, orientation, isUpdateMedia, rect, null);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (callback != null) {
			callback.savedBefore();
		}
	}

	@Override
	protected File doInBackground(Void... params) {
		File photo = getOutputMediaFile();
		if (photo == null) {
			LogUtils.i(TAG, "--photo为空");
			return null;
		}
		if (orientation == ExifInterface.ORIENTATION_UNDEFINED) {
			saveByteArray(photo, data);
		} else {
			saveByteArrayWithOrientation(photo, data, orientation);
		}
		if (FileUtils.isExist(photo.getPath()) && isUpdateMedia) {
			// 同步更新到系统图库
			updateToSystemMedia(context, photo);
		}
		return photo;
	}

	@Override
	protected void onPostExecute(File file) {
		super.onPostExecute(file);
		photoSaved(file);
	}

	private void photoSaved(File photo) {
		if (callback != null) {
			if (photo != null)
				callback.savedSuccess(photo);
			else
				callback.savedFailure();
		}
	}

	/**
	 * 0旋转角度保存照片
	 */
	private boolean saveByteArray(File photo, byte[] data) {
		long totalTime = System.currentTimeMillis();
		long time = System.currentTimeMillis();
		try {
			Bitmap bitmap = null;
			if (data != null) {
				// 从byte数组中读出bitmap
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				LogUtils.i(TAG, "编码Bitmap消耗时间-->" + (System.currentTimeMillis() - time));
			}
			if (bitmap != null) {
				time = System.currentTimeMillis();
				// 截取图片
//				bitmap = cropPhoto(bitmap);
				// 保存图片
				ImageUtils.saveBitmapAsJpeg(bitmap, photo.getPath(), COMPRESS_QUALITY);
				LogUtils.i(TAG, "保存照片消耗时间-->" + (System.currentTimeMillis() - time));
				LogUtils.i(TAG, "处理照片总消耗时间-->" + (System.currentTimeMillis() - totalTime));
				return true;
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "--保存照片异常", e);
		}
		LogUtils.i(TAG, "保存照片消耗时间-->" + (System.currentTimeMillis() - time));
		return false;
	}

	/**
	 * 指定旋转角度保存照片
	 */
	private boolean saveByteArrayWithOrientation(File photo, byte[] data, int orientation) {
		LogUtils.i(TAG, "旋转角度-->" + orientation);
		long totalTime = System.currentTimeMillis();
		long time = System.currentTimeMillis();
		try {
			Bitmap bitmap = null;
			if (data != null) {
				// 从byte数组中读出bitmap
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				LogUtils.i(TAG, "编码Bitmap消耗时间-->" + (System.currentTimeMillis() - time));
			}
			if (bitmap != null) {
				time = System.currentTimeMillis();
				// 旋转图片
				if (orientation != 0 && bitmap.getWidth() > bitmap.getHeight()) {
					bitmap = ImageUtils.getRotateBitmap(bitmap, orientation, true);
				}
				// 截取图片
//				bitmap = cropPhoto(bitmap);
				// 保存图片
				ImageUtils.saveBitmapAsJpeg(bitmap, photo.getPath(), COMPRESS_QUALITY);
				LogUtils.i(TAG, "保存照片消耗时间-->" + (System.currentTimeMillis() - time));
				LogUtils.i(TAG, "处理照片总消耗时间-->" + (System.currentTimeMillis() - totalTime));
				return true;
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "--保存旋转照片异常", e);
		}
		return false;
	}

	/**
	 * 使用景框区域截取的Bitmap
	 */
	private Bitmap cropPhoto(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		Camera.Size pictureSize = CameraManager.getInstance().getPictureSize();
		Camera.Size previewSize = CameraManager.getInstance().getPreviewSize();
		if (pictureSize == null) {
			return null;
		}
		if (previewSize == null) {
			return null;
		}
		int pictureWidth = pictureSize.width;
		int pictureHeight = pictureSize.height;
		int previewWidth = previewSize.width;
		int previewHeight = previewSize.height;
		LogUtils.i(TAG, "picture size width-->" + pictureWidth);
		LogUtils.i(TAG, "picture size height-->" + pictureHeight);
		LogUtils.i(TAG, "preview size width-->" + previewWidth);
		LogUtils.i(TAG, "preview size height-->" + previewHeight);
		float ratio = (float) pictureWidth / previewWidth;
		LogUtils.i(TAG, "缩放比例-->" + ratio);
		int bitmapLeft = roundInt(rect.left * ratio);
		int bitmapTop = roundInt(rect.top * ratio);
		int bitmapWidth = roundInt((rect.right - rect.left) * ratio);
		int bitmapHeight = roundInt((rect.bottom - rect.top) * ratio);
		return Bitmap.createBitmap(bitmap, bitmapLeft, bitmapTop, bitmapWidth, bitmapHeight);
	}

	private int roundInt(float num) {
		return (int) (num + 0.5);
	}

	private File getOutputMediaFile() {
		FileUtils.makeDir(path);
		return new File(path + File.separator + name);
	}

	/**
	 * 把照片同步更新到系统图库
	 * 需要在系统图库同步看到拍照结果可以调用
	 */
	private void updateToSystemMedia(Context context, File file) {
		LogUtils.i(TAG, "--同步更新到系统图库");
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), file.getName(), null);
		} catch (FileNotFoundException e) {
			LogUtils.e(TAG, "--更新到系统图库异常", e);
		}
		// 通知系统图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + file.getPath())));
	}

}
