package com.ai2020lab.pigadopted.common;

import android.content.Context;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.StringUtils;
import com.ai2020lab.aiutils.storage.DiskLruCache;
import com.ai2020lab.aiutils.storage.FileUtils;
import com.ai2020lab.aiutils.system.PackageUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 磁盘缓存管理工具类
 * Created by Justin on 2016/1/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class DiskCacheManager {

	private final static String TAG = DiskCacheManager.class.getSimpleName();

	private HashMap<String, DiskLruCache> diskCaches;

	private DiskCacheManager() {
		if (diskCaches == null) {
			diskCaches = new HashMap<>();
		}
	}

	/**
	 * 获取本类全局唯一实例
	 *
	 * @return 返回本类全局唯一实例
	 */
	public static DiskCacheManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * 初始化磁盘缓存<br>
	 * 一般来讲，如果有SD卡存储，则存放在sdcard/Android/data/application package/cache/cachePath下<br>
	 * 如果没有则存储在dada/dada/application package/cache/cachePath下<br>
	 *
	 * @param context   上下文引用
	 * @param maxSize   磁盘缓存最大容量，单位为字节
	 * @param cachePath 缓存路径根目录，用来区分不同的缓存
	 * @return true-初始化磁盘缓存成功，false-初始化磁盘缓存失败
	 */
	public void openCache(Context context, long maxSize, String cachePath) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		if (diskCache == null) {
			// 创建缓存目录
			File cacheDir = FileUtils.getDiskCacheDir(context, cachePath);
			cacheDir.mkdirs();
			try {
				diskCache = DiskLruCache.open(cacheDir, PackageUtils.getAppVersion(context),
						1, maxSize);
				diskCaches.put(cachePath, diskCache);
			} catch (IOException e) {
				LogUtils.e(TAG, "IOException", e);
			}
		}
	}

	/**
	 * 关闭所有的磁盘缓存对象<br>
	 * 在Activity的onDestory()中调用,关闭之后不能再调用操作数据的方法
	 */
	public void closeCache() {
		// 循环遍历所有的磁盘缓存并关闭
		Set<Map.Entry<String, DiskLruCache>> set = diskCaches.entrySet();
		for (Map.Entry<String, DiskLruCache> entry : set) {
			DiskLruCache diskCache = entry.getValue();
			try {
				if (diskCache != null)
					diskCache.close();
			} catch (IOException e) {
				LogUtils.e(TAG, "IOException", e);
			}
		}
		diskCaches.clear();
	}

	/**
	 * 关闭指定根目录的磁盘缓存<br>
	 * 在Activity的onDestory()中调用,关闭之后不能再调用操作数据的方法
	 *
	 * @param cachePath 缓存路径根目录，用来区分不同的缓存
	 */
	public void closeCache(String cachePath) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		try {
			if (diskCache != null)
				diskCache.close();
		} catch (IOException e) {
			LogUtils.e(TAG, "IOException", e);
		}
		diskCaches.remove(cachePath);
	}

	/**
	 * 清除所有的磁盘缓存,同时会将这些缓存都关闭
	 */
	public void clearCache() {
		Set<Map.Entry<String, DiskLruCache>> set = diskCaches.entrySet();
		for (Map.Entry<String, DiskLruCache> entry : set) {
			DiskLruCache diskCache = entry.getValue();
			try {
				if (diskCache != null)
					diskCache.delete();
			} catch (IOException e) {
				LogUtils.e(TAG, "IOException", e);
			}
		}
		diskCaches.clear();
	}

	/**
	 * 清除指定根目录的磁盘缓存<br>
	 * 注意清除缓存的同时会关闭这个缓存
	 *
	 * @param cachePath 缓存路径根目录，用来区分不同的缓存
	 */
	public void clearCache(String cachePath) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		try {
			if (diskCache != null)
				diskCache.delete();
		} catch (IOException e) {
			LogUtils.e(TAG, "IOException", e);
		}
		diskCaches.remove(cachePath);
	}

	/**
	 * 获取指定根目录的磁盘缓存的当前数据大小，单位为byte<br>
	 *
	 * @param cachePath 缓存路径根目录，用来区分不同的缓存
	 * @return 返回当前缓存数据的大小，没有则返回0
	 */
	public long getCacheSize(String cachePath) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		if (diskCache != null) {
			return diskCache.size();
		}
		return 0l;
	}

	/**
	 * 获取所有的磁盘缓存数据大小，单位为byte
	 *
	 * @return 返回当前缓存数据的大小，没有则返回0
	 */
	public long getCacheSize() {
		long sumSize = 0l;
		Set<Map.Entry<String, DiskLruCache>> set = diskCaches.entrySet();
		for (Map.Entry<String, DiskLruCache> entry : set) {
			DiskLruCache diskCache = entry.getValue();
			if (diskCache != null)
				sumSize += diskCache.size();
		}
		return sumSize;
	}

	/**
	 * 将对象写入磁盘缓存<p>
	 *
	 * @param cachePath 磁盘缓存的路径
	 * @param key       键，可用当前时间戳来做键
	 * @param obj       要写入缓存的对象
	 */
	public void putObject(String cachePath, String key, Object obj) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		if (diskCache == null) {
			LogUtils.i(TAG, "还没有初始化key为 '" + cachePath + "' 的磁盘缓存");
			return;
		}
		try {
			DiskLruCache.Editor editor = diskCache.edit(key);
			if (editor != null) {
				if (FileUtils.writeObjectToStream(obj, editor.newOutputStream(0))) {
					editor.commit();
				} else {
					editor.abort();
				}
			}
			diskCache.flush();
			LogUtils.i(TAG, "对象写入磁盘缓存成功");
		} catch (Exception e) {
			LogUtils.e(TAG, "Exception", e);
		}
	}

	/**
	 * 将对象从磁盘缓存中读出<p>
	 *
	 * @param cachePath 磁盘缓存的路径
	 * @param key       键，可用当前时间戳来做键
	 * @return 读取成功返回之前缓存的对象的拷贝，否则返回null
	 */
	public Object getObject(String cachePath, String key) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		if (diskCache == null) {
			LogUtils.i(TAG, "还没有初始化key为 '" + cachePath + "' 的磁盘缓存");
			return null;
		}
		try {
			DiskLruCache.Snapshot snapShot = diskCache.get(key);
			if (snapShot != null) {
				return FileUtils.readObjectFromStream(snapShot.getInputStream(0));
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "Exception", e);
		}
		return null;
	}

	/**
	 * 将字符窜内容写入磁盘缓存<p>
	 * 这个方法用来缓存文件名可能超长的情况
	 * <strong>注意：文件名命名长度的限制,android下文件最长127个中文，255个英文字符</strong>
	 *
	 * @param cachePath 磁盘缓存的路径
	 * @param key       key
	 * @param content   要缓存的字符窜内容
	 */
	public void putString(String cachePath, String key, String content) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		if (diskCache == null) {
			LogUtils.i(TAG, "还没有初始化key为 '" + cachePath + "' 的磁盘缓存");
			return;
		}
		// 将要保存的字符窜转换成md5字符窜，用来做文件名
		try {
			DiskLruCache.Editor editor = diskCache.edit(StringUtils.encodeToMD5(key));
			if (editor != null) {
				if (FileUtils.writeStringToStream(content, "utf-8", editor.newOutputStream(0))) {
					editor.commit();
				} else {
					editor.abort();
				}
			}
			diskCache.flush();
		} catch (Exception e) {
			LogUtils.e(TAG, "Exception", e);
		}
	}

	/**
	 * 将字符窜内容写入磁盘缓存<p>
	 * <strong>注意：文件名命名长度的限制,android下文件最长127个中文，255个英文字符</strong>
	 *
	 * @param cachePath 磁盘缓存的路径
	 * @param content   要缓存的字符窜内容
	 */
	public void putString(String cachePath, String content) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		if (diskCache == null) {
			LogUtils.i(TAG, "还没有初始化key为 '" + cachePath + "' 的磁盘缓存");
			return;
		}
		// 将要保存的字符窜转换成md5字符窜，用来做文件名
		try {
			DiskLruCache.Editor editor = diskCache.edit(StringUtils.encodeToMD5(content));
			if (editor != null) {
				if (FileUtils.writeStringToStream(content, "utf-8", editor.newOutputStream(0))) {
					editor.commit();
				} else {
					editor.abort();
				}
			}
			diskCache.flush();
		} catch (Exception e) {
			LogUtils.e(TAG, "Exception", e);
		}
	}

	/**
	 * 将字符窜内容从磁盘缓存中读出<p>
	 * <strong>注意：文件名命名长度的限制,android下文件最长127个中文，255个英文字符</strong>
	 *
	 * @param cachePath 磁盘缓存的路径
	 * @param key   要读取的缓存内容的key
	 * @return 读取成功返回字符窜内容，否则返回null
	 */
	public String getString(String cachePath, String key) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		if (diskCache == null) {
			LogUtils.i(TAG, "还没有初始化key为 '" + cachePath + "' 的磁盘缓存");
			return null;
		}
		try {
			DiskLruCache.Snapshot snapShot = diskCache.get(StringUtils.encodeToMD5(key));
			if (snapShot != null) {
				return FileUtils.readStringFromStream(snapShot.getInputStream(0));
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "IOException", e);
		}
		return null;
	}

	/**
	 * 将指定的缓存文件从磁盘上移除<p>
	 * <strong>注意：文件名命名长度的限制,android下文件最长127个中文，255个英文字符</strong>
	 *
	 * @param cachePath 缓存文件的路径
	 * @param name      缓存文件的名字
	 */
	public void remove(String cachePath, String name) {
		DiskLruCache diskCache = diskCaches.get(cachePath);
		if (diskCache == null) {
			LogUtils.i(TAG, "还没有初始化key为 '" + cachePath + "' 的磁盘缓存");
			return;
		}
		try {
			diskCache.remove(StringUtils.encodeToMD5(name));
		} catch (Exception e) {
			LogUtils.e(TAG, "IOException", e);
		}
	}

	private final static class SingletonHolder {
		public final static DiskCacheManager INSTANCE = new DiskCacheManager();
	}

}
