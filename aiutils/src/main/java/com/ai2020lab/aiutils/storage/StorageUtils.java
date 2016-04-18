/*
 * Copyright 2015 Justin Z
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

package com.ai2020lab.aiutils.storage;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.SparseArray;

import com.ai2020lab.aiutils.common.LogUtils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 存储工具类<p>
 * 获取设备内卡和外卡路径，以及计算指定路径的存储容量等
 * Created by Justin on 2015/7/14.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class StorageUtils {

	private final static String TAG = StorageUtils.class.getSimpleName();


	private static StorageUtils instance = null;

	private SparseArray<String> sdMap = null;

	private StorageUtils(Context context) {
		initSDMap();
		initInExSDDirs(context);
	}

	/**
	 * 工厂方法，得到本类的实例
	 *
	 * @param context
	 * @return
	 */
	public synchronized static StorageUtils getInstance(Context context) {
		if (instance == null) {
			instance = new StorageUtils(context);
		}
		return instance;
	}

	/**
	 * 测试打印方法
	 *
	 * @param files
	 */
	private void testPrintFiles(File[] files) {
		int size = files.length;
		LogUtils.i(TAG, "可读可写的目录个数:" + size);
		// 打印
		for (int i = 0; i < size; i++) {
			LogUtils.i(TAG, files[i].getAbsolutePath());
		}

	}

	/**
	 * 获取手机外置存储卡
	 * <p/>
	 * 这个方法在API 14以下有效
	 *
	 */
	private void setExternalSD() {
		// 得到一个表示所有环境参数的Map
		Map<String, String> map = System.getenv();
		String values[] = new String[map.values().size()];
		// 将map转换为数组
		map.values().toArray(values);
		// 获取最后一项，就是外置SDCard的路径
		String exSDPath = values[values.length - 1];
		LogUtils.i(TAG, "外置卡-->" + exSDPath);
		if (exSDPath.startsWith("/mnt/") && exSDPath.indexOf("legacy") == -1) {
			File extSD = new File(exSDPath);
			if (extSD.isDirectory() && extSD.canRead() && extSD.canWrite()
					&& !extSD.getName().startsWith(".")) {
				File[] files = extSD.listFiles();
				if (files != null && files.length > 0) {
					sdMap.put(SDCardCategory.EXTERNAL, exSDPath);
				}
			}
		} else {
			sdMap.put(SDCardCategory.EXTERNAL, "");
		}
	}

	/**
	 * 获取手机外置存储卡
	 * <p/>
	 * 遍历mnt目录，找到可读，可写的所有目录
	 */
	private void setExternalSD1() {
		File mntFile = new File("/mnt");
		File[] files = mntFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File rf) {
				return rf.exists() && rf.canRead() && rf.canWrite()
						&& rf.isDirectory()
						&& !rf.getName().startsWith(".")
						&& !rf.getPath().contains("legacy");
			}
		});
		int size = files.length;
		// 测试打印
		testPrintFiles(files);
		// // 多余2个的情况
		// if (size >= 2) {
		// sdMap.put(SDCardCategory.EXTERNAL, files[size -
		// 1].getAbsolutePath());
		// }
		// // 只有一个的情况
		// else {
		// sdMap.put(SDCardCategory.EXTERNAL, files[0].getAbsolutePath());
		// }
	}

	private void initSDMap() {
		if (sdMap == null) {
			sdMap = new SparseArray<String>();
		}
		sdMap.clear();
		sdMap.put(SDCardCategory.INTERNAL, "");
		sdMap.put(SDCardCategory.EXTERNAL, "");
	}

	/**
	 * 获取手机外置存储卡
	 *
	 * @param context 上下文引用
	 */
	private void initInExSDDirs(Context context) {
		setInternalSD();
		if (Build.VERSION.SDK_INT < 14) {
			LogUtils.i(TAG, "小于API 14方法调用");
			if (!TextUtils.isEmpty(getExternalDirectory()))
				return;
			setExternalSD();
		} else {
			LogUtils.i(TAG, "大于API 14方法调用");
			if (!TextUtils.isEmpty(getExternalDirectory()))
				return;
			setExternalSD(context);
		}
		checkExternalSD();
	}

	/**
	 * 获取手机内置存储卡
	 */
	private void setInternalSD() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			sdMap.put(SDCardCategory.INTERNAL, Environment.getExternalStorageDirectory()
					.getAbsolutePath());
		} else {
			sdMap.put(SDCardCategory.INTERNAL, "");
		}
	}

	/**
	 * 验证外卡路径，如何外卡路径和内卡路径一样，那么认为无效
	 */
	private void checkExternalSD() {
		String inDir = sdMap.get(SDCardCategory.INTERNAL);
		String exDir = sdMap.get(SDCardCategory.EXTERNAL);
		if (!inDir.equals("") && !exDir.equals("") && inDir.equals(exDir)) {
			sdMap.put(SDCardCategory.EXTERNAL, "");
		}
	}

	/**
	 * 获取手机外置存储卡
	 * <p/>
	 * 这个方法在API 14及以上有效
	 *
	 * @param context 上下文引用
	 */
	private void setExternalSD(Context context) {
		// if(VERSION.SDK_INT < 14){
		// return;
		// }
		try {
			Class<?> localClass = Class.forName("android.os.Environment");
			StorageManager storageManager = (StorageManager) context.getSystemService(
					Context.STORAGE_SERVICE);
			Method method = StorageManager.class.getMethod("getVolumePaths");
			String[] storagePathList = (String[]) method.invoke(storageManager);
			// 没有找到数据
			if (storagePathList == null || storagePathList.length == 0) {
				LogUtils.i(TAG, "没有找到数据");
				return;
			}
			// 找到2个数据
			if (storagePathList.length >= 2) {
				LogUtils.i(TAG, "找到2个数据");
				String extDir = storagePathList[storagePathList.length - 1];
				if (extDir == null) {
					LogUtils.i(TAG, "为空返回");
					return;
				}
				method = StorageManager.class.getMethod("getVolumeState", String.class);
				String state = (String) method.invoke(storageManager, extDir);
				if (Environment.MEDIA_MOUNTED.equals(state) && new File(extDir).exists()) {
					LogUtils.i(TAG, "得到外卡路径");
					sdMap.put(SDCardCategory.EXTERNAL, extDir);
				}
			}
			// 找到一个数据
			else {
				LogUtils.i(TAG, "找到1个数据");
				boolean isExternalStorageEmulated = (Boolean) localClass.getMethod(
						"isExternalStorageEmulated", new Class[0]).invoke(localClass.getClass());
				// 内卡的情况
				if (isExternalStorageEmulated) {
					LogUtils.i(TAG, "内卡的情况");
					sdMap.put(SDCardCategory.INTERNAL, Environment.getExternalStorageDirectory()
							.getAbsolutePath());
					sdMap.put(SDCardCategory.EXTERNAL, "");
				}
				// 外卡的情况
				else {
					LogUtils.i(TAG, "外卡的情况");
					sdMap.put(SDCardCategory.INTERNAL, "");
					sdMap.put(SDCardCategory.EXTERNAL, Environment.getExternalStorageDirectory()
							.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "获取外部SD卡异常", e);
		}
	}

	/**
	 * 得到外部SD卡存储路径
	 *
	 * @return 返回外卡路径
	 */
	public String getExternalDirectory() {
		return sdMap.get(SDCardCategory.EXTERNAL);
	}

	/**
	 * 得到内部SD卡存储路径
	 *
	 * @return 返回内卡路径
	 */
	public String getInternalDirectory() {
		return sdMap.get(SDCardCategory.INTERNAL);
	}

	/**
	 * 判断是否有外置存储卡
	 *
	 * @return boolean
	 */
	public boolean isHasExternalStorage() {
		String SDCardDir = sdMap.get(SDCardCategory.EXTERNAL);
		return !SDCardDir.equals("");
	}

	/**
	 * 判断是否有内置存储卡
	 *
	 * @return boolean
	 */
	public boolean isHasInternalStorage() {
		String SDCardDir = sdMap.get(SDCardCategory.INTERNAL);
		return !SDCardDir.equals("");
	}

	/**
	 * 根据指定路径获取总容量大小，单位为byte
	 *
	 * @param path 指定路径
	 * @return 返回指定路径的总容量大小，单位为byte,计算失败返回-1l
	 */
	@SuppressWarnings("deprecation")
	public long getTotalSize(String path) {
		if (TextUtils.isEmpty(path)) {
			LogUtils.i(TAG, "getTotalSize:指定路径不能为空，计算总容量失败");
			return -1l;
		}
		StatFs stat = new StatFs(path);
		// 获取每个block(分区)的大小
		long blockSize = stat.getBlockSize();
		// 获取总block数
		long totalBlocks = stat.getBlockCount();
		return blockSize * totalBlocks;
	}

	/**
	 * 根据指定路径获取可用容量大小，单位为byte
	 *
	 * @param path 指定路径
	 * @return 返回指定路径的可用容量大小，单位为byte
	 */
	@SuppressWarnings("deprecation")
	public long getAvailableSize(String path) {
		if (TextUtils.isEmpty(path)) {
			LogUtils.i(TAG, "getAvailableSize:指定路径不能为空，计算可用容量失败");
			return -1l;
		}
		StatFs stat = new StatFs(path);
		// 获取每个block(分区)的大小
		long blockSize = stat.getBlockSize();
		// 获取总block数
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/**
	 * 存储类型常量接口
	 * <p/>
	 * 内部存储和外部存储
	 */
	public interface SDCardCategory {
		/**
		 * 内置存储卡
		 */
		public final static int INTERNAL = 1;
		/**
		 * 外置存储卡
		 */
		public final static int EXTERNAL = 2;

	}

	/**
	 * 存储容量转换常量接口
	 */
	public interface VolumeConvert {
		/**
		 * gb to byte *
		 */
		public static final long GB_2_BYTE = 1073741824;
		/**
		 * mb to byte *
		 */
		public static final long MB_2_BYTE = 1048576;
		/**
		 * kb to byte *
		 */
		public static final long KB_2_BYTE = 1024;
	}


}
