/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aimedia.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.AsyncTask;

import com.ai2020lab.aimedia.interfaces.PhotoSavedListener;
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.image.ImageUtils;
import com.ai2020lab.aiutils.storage.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 保存照片异步任务
 * Created by Justin Z on 2016/4/13.
 * 502953057@qq.com
 */
public class SavingPhotoTask extends AsyncTask<Void, Void, File> {

	private final static String TAG = SavingPhotoTask.class.getSimpleName();
	private final static int COMPRESS_QUALITY = 90;

	private byte[] data;
	private String name;
	private String path;
	private int orientation;
	private PhotoSavedListener callback;

	public SavingPhotoTask(byte[] data, String name, String path, int orientation,
	                       PhotoSavedListener callback) {
		this.data = data;
		this.name = name;
		this.path = path;
		this.orientation = orientation;
		this.callback = callback;
	}

	public SavingPhotoTask(byte[] data, String name, String path, int orientation) {
		this(data, name, path, orientation, null);
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
	private void saveByteArray(File photo, byte[] data) {
		long time = System.currentTimeMillis();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(photo);
			fos.write(data);
		} catch (Exception e) {
			LogUtils.e(TAG, "--保存照片异常", e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				LogUtils.e(TAG, "--保存照片异常", e);
			}
		}
		LogUtils.i(TAG, "保存照片消耗时间-->" + (System.currentTimeMillis() - time));
	}

	/**
	 * 指定旋转角度保存照片
	 */
	private void saveByteArrayWithOrientation(File photo, byte[] data, int orientation) {
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
				// 保存图片
				ImageUtils.saveBitmapAsJpeg(bitmap, photo.getPath(), COMPRESS_QUALITY);
				LogUtils.i(TAG, "保存照片消耗时间-->" + (System.currentTimeMillis() - time));
				LogUtils.i(TAG, "处理照片总消耗时间-->" + (System.currentTimeMillis() - totalTime));
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "--保存旋转照片异常", e);
		}
	}

	private File getOutputMediaFile() {
		FileUtils.makeDir(path);
		return new File(path + File.separator + name);
	}

}
