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
import android.os.Environment;
import android.text.TextUtils;

import com.ai2020lab.aiutils.common.LogUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * 文件操作工具类
 * Created by Justin on 2015/7/14.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class FileUtils {


	private final static String TAG = FileUtils.class.getSimpleName();

	/**
	 * 在指定路径创建一个新的目录
	 *
	 * @param path 目录路径
	 * @return 成功创建则返回true, 创建失败则返回false
	 */
	public static boolean makeDir(String path) {
		if (TextUtils.isEmpty(path)) {
			LogUtils.d(TAG, "目录路径不能为空");
			return false;
		}
		File dir = new File(path);
		return (dir.exists() && dir.isDirectory()) || dir.mkdirs();
	}

	/**
	 * 在指定路径创建一个空文件
	 * <p>
	 * 文件所在的目录如果不存在会自动创建
	 *
	 * @param path 文件路径
	 * @return 成功创建则返回true,创建失败则返回false
	 */
	public static boolean createNewFile(String path) {
		if (TextUtils.isEmpty(path)) {
			LogUtils.i(TAG, "文件路径不能为空");
			return false;
		}
		if (path.endsWith(File.separator)) {
			LogUtils.i(TAG, "创建文件目标不能是目录！");
			return false;
		}
		File file = new File(path);
		File filePath = file.getParentFile();
		// 文件的父目录不存在就创建一个
		if (!filePath.exists()) {
			LogUtils.i(TAG, "目标文件的目录不存在，准备创建");
			makeDir(filePath.getPath());
		}
		try {
			return file.createNewFile();
		} catch (IOException e) {
			LogUtils.e(TAG, "创建文件异常", e);
		}
		return false;
	}

	/**
	 * 根据路径检查文件是否存在
	 *
	 * @param path 文件路径
	 * @return 存在返回true, 不存在返回false
	 */
	public static boolean isExist(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		File f = new File(path);
		return f.exists();
	}

	/**
	 * 根据文件全路径删除单个文件
	 *
	 * @param path 文件路径
	 * @return 删除文件成功返回true，删除失败返回false
	 */
	public static boolean deleteFile(String path) {
		File file = new File(path);
		if (file.isFile() && file.exists()) {
			return file.delete();
		} else {
			return false;
		}
	}

	/**
	 * 删除整个目录
	 *
	 * @param path 文件路径
	 * @return 删除成功返回true，删除失败返回false
	 */
	public static boolean deleteDirectory(String path) {
		File dirFile = new File(path);
		if (!dirFile.exists() || !dirFile.isDirectory()) {// 如果不是目录或者目录不存在
			return false;
		}
		boolean flag = true; // 标示删除成功还是失败
		File[] fileList = dirFile.listFiles();
		for (File file : fileList) {
			if (file != null && file.isFile()) { // 如果是文件
				flag = deleteFile(file.getAbsolutePath());
			} else { // 如果是目录
				flag = deleteDirectory(file.getAbsolutePath());
			}
			if (!flag) {
				break;
			}
		}
		flag = dirFile.delete();// 删除当前文件
		return flag;
	}

	/**
	 * 将对象序列化到指定路径的文件中
	 *
	 * @param filePath 指定文件的路径
	 * @param obj      需要序列化成文件的对象
	 */
	public static void serializeToFile(String filePath, Object obj) {
		if (TextUtils.isEmpty(filePath)) {
			LogUtils.i(TAG, "serializeToFile:文件路径不能为空，序列化操作失败");
			return;
		}
		if (obj == null) {
			LogUtils.i(TAG, "serializeToFile:对象不能为空，序列化操作失败");
			return;
		}
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(filePath));
			out.writeObject(obj);
			out.close();
		} catch (FileNotFoundException e) {
			LogUtils.e(TAG, "serializeToFile:FileNotFoundException", e);
		} catch (IOException e) {
			LogUtils.e(TAG, "serializeToFile:IOException", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LogUtils.e(TAG, "serializeToFile:IOException", e);
				}
			}
		}
	}

	/**
	 * 将指定路径的文件反序列化为对象
	 *
	 * @param filePath 指定文件的路径
	 * @return 返回反序列化成功的对象，否则返回null
	 */
	public static Object deserializeToObj(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			LogUtils.i(TAG, "deserializeToObj:文件路径不能为空，反序列化操作失败");
			return null;
		}
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(filePath));
			Object o = in.readObject();
			in.close();
			return o;
		} catch (FileNotFoundException e) {
			LogUtils.e(TAG, "deserializeToObj:FileNotFoundException", e);
		} catch (ClassNotFoundException e) {
			LogUtils.e(TAG, "deserializeToObj:ClassNotFoundException", e);
		} catch (IOException e) {
			LogUtils.e(TAG, "deserializeToObj:IOException", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtils.e(TAG, "deserializeToObj:IOException", e);
				}
			}
		}
		return null;
	}


	/**
	 * 根据提供的文件URL获取文件输入流，支持网络(http和https协议)和本地文件（sd卡和assets目录）
	 *
	 * @param url 文件url
	 * @return 返回网络文件的输入流，失败返回null
	 */
	public static InputStream getInputStreamFromUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			LogUtils.i(TAG, "getInputStreamFromUrl:文件url不能为空，获取输入流失败");
			return null;
		}
		try {
			return new URL(url).openStream();
		} catch (IOException e) {
			LogUtils.e(TAG, "getInputStreamFromUrl:IOException", e);
		}
		return null;
	}

	/**
	 * 根据SD卡上的文件路径获取文件输入流
	 *
	 * @param path SD卡上的文件路径
	 * @return 返回SD卡上的文件输入流，失败返回null
	 */
	public static FileInputStream getInputStreamFromSDCard(String path) {
		if (TextUtils.isEmpty(path)) {
			LogUtils.i(TAG, "getInputStreamFromSDCard:文件path不能为空，获取输入流失败");
			return null;
		}
		try {
			return new FileInputStream(new File(path));
		} catch (IOException e) {
			LogUtils.e(TAG, "getInputStreamFromSDCard:IOException", e);
		}
		return null;
	}

	/**
	 * 根据assets目录下文件的相对路径获取文件输入流
	 *
	 * @param context 上下文引用
	 * @param path    assets目录下的文件路径
	 * @return 返回assets目录下的文件输入流，失败返回null
	 */
	public static InputStream getInputStreamFromAssets(Context context, String path) {
		if (context == null) {
			LogUtils.i(TAG, "getInputStreamFromAssets:上下文引用不能为空，获取输入流失败");
			return null;
		}
		if (TextUtils.isEmpty(path)) {
			LogUtils.i(TAG, "getInputStreamFromAssets:文件path不能为空，获取输入流失败");
			return null;
		}
		try {
			return context.getAssets().open(path);
		} catch (IOException e) {
			LogUtils.e(TAG, "getInputStreamFromAssets:IOException", e);
		}
		return null;
	}

	/**
	 * 关闭指定输入流
	 *
	 * @param s 要关闭的输入流对象
	 */
	public static void closeInputStream(InputStream s) {
		if (s == null) {
			return;
		}
		try {
			s.close();
		} catch (IOException e) {
			LogUtils.e(TAG, "closeInputStream:IOException", e);
		}
	}

	/**
	 * 将字符窜写入到输出流中
	 *
	 * @param s            要写入输出流的字符窜
	 * @param charsetName  写入字符窜的编码格式
	 * @param outputStream 要写入字符窜的输出流对象的引用
	 * @return 写入成功返回true, 写入失败返回false
	 */
	public static boolean writeStringToStream(String s, String charsetName, OutputStream outputStream) {
		if (TextUtils.isEmpty(charsetName)) {
			LogUtils.i(TAG, "writeStringToStream:编码格式为空，将默认采用utf-8");
			charsetName = "utf-8";
		}
		if (TextUtils.isEmpty(s)) {
			LogUtils.i(TAG, "writeStringToStream:写入的字符窜不能为空");
			return false;
		}
		if (outputStream == null) {
			LogUtils.i(TAG, "writeStringToStream:要写入的输出流对象不能为空");
			return false;
		}
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(s.getBytes(charsetName));
			byte[] buffer = new byte[1024];
			int bytesRead;
			// 将输入字节流读入缓冲区，并写入到字节输出流中
			while ((bytesRead = bis.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
			return true;
		} catch (UnsupportedEncodingException e) {
			LogUtils.e(TAG, "UnsupportedEncodingException", e);
		} catch (IOException e) {
			LogUtils.e(TAG, "IOException", e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					LogUtils.e(TAG, "IOException", e);
				}
			}
		}
		return false;
	}

	/**
	 * 将输入流对象转换成字符窜
	 *
	 * @param is InputStream对象的引用
	 * @return 转换成功返回字符窜内容，否则返回null
	 */
	public static String readStringFromStream(InputStream is) {
		if (is == null) {
			LogUtils.i(TAG, "输入流对象不能为空");
			return null;
		}
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			return sb.toString();
		} catch (Exception e) {
			LogUtils.e(TAG, "Exception", e);
		}
		return null;
	}

	/**
	 * 将对象写入到输出流中
	 * @param obj 要写入的对象
	 * @param out 输出流对象
	 * @return 写入成功返回true，写入失败返回false
	 */
	public static boolean writeObjectToStream(Object obj, OutputStream out) {
		if (out == null) {
			LogUtils.i(TAG, "writeObjectToStream:要写入的输出流对象不能为空");
			return false;
		}
		if (obj == null) {
			LogUtils.i(TAG, "writeObjectToStream:要写入的对象不能为空");
			return false;
		}
		ObjectOutputStream objOut = null;
		try {
			objOut = new ObjectOutputStream(out);
			objOut.writeObject(obj);
			return true;
		} catch (IOException e) {
			LogUtils.e(TAG, "IOException", e);
		}
		return false;
	}

	/**
	 * 从输入流中读出对象
	 * @param is 输入流对象
	 * @return 读取成功返回之前写入的对象的拷贝，否则返回null
	 */
	public static Object readObjectFromStream(InputStream is){
		if (is == null) {
			LogUtils.i(TAG, "readObjectFromStream:输入流对象不能为空");
			return null;
		}
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(is);
			return ois.readObject();
		} catch (Exception e) {
			LogUtils.e(TAG, "Exception", e);
		}
		return null;
	}

	/**
	 * 获取磁盘缓存文件的路径<br>
	 * 如果有SD卡存储，则存放在sdcard/Android/data/application package/cache<br>
	 * 如果没有则存储在dada/dada/application package/cache<br>
	 *
	 * @param context    上下文引用
	 * @param uniqueName 区分数据缓存的文件夹路径名
	 * @return 返回磁盘缓存的文件路径File对象
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		if (context == null) {
			throw new IllegalArgumentException("getDiskCacheDir的参数context不能为空");
		}
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			File f = context.getExternalCacheDir();
			if (f != null && f.exists())
				cachePath = f.getPath();
			else
				cachePath = context.getCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 文件扩展名常量接口
	 */
	public interface ExtensionName {
		/**
		 * 音频视频文件统一扩展名
		 */
		String THREEGP = "3gp";
		/**
		 * 文本文件统一扩展名
		 */
		String TXT = "txt";
		/**
		 * 序列化对象文件统一扩展名
		 */
		String SER = "ser";
		/**
		 * png
		 */
		String PNG = "png";
		/**
		 * jpg
		 */
		String JPG = "jpg";
		/**
		 * bmp
		 */
		String BMP = "bmp";
		/**
		 * gif
		 */
		String GIF = "gif";
		/**
		 * mp3
		 */
		String MP3 = "mp3";
		/**
		 * mp4
		 */
		String MP4 = "mp4";
	}


}
