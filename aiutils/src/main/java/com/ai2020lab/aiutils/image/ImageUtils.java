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

package com.ai2020lab.aiutils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.storage.FileUtils;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片处理工具类<br>
 * <ul>
 * Bitmap, byte array, Drawable, Base64之间相互转换
 * <li>{@link #bitmapToByte(Bitmap)}</li>
 * <li>{@link #bitmapToDrawable(Bitmap)}</li>
 * <li>{@link #byteToBitmap(byte[])}</li>
 * <li>{@link #byteToDrawable(byte[])}</li>
 * <li>{@link #drawableToBitmap(Drawable)}</li>
 * <li>{@link #drawableToByte(Drawable)}</li>
 * <li>{@link #base64ToBitmap(String)}</li>
 * <li>{@link #bitmapToBase64(Bitmap, ImageType, int)}</li>
 * </ul>
 * <ul>
 * 从url地址，SD卡路径和assets目录获取Bitmap对象
 * <li>{@link #getBitmapFromUrl(String)}</li>
 * <li>{@link #getBitmapFromSDCard(String)}</li>
 * <li>{@link #getBitmapFromAssets(Context, String)}</li>
 * </ul>
 * <p/>
 * Created by Justin on 2015/7/14.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ImageUtils {
	private final static String TAG = ImageUtils.class.getSimpleName();
	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

	/**
	 * Bitmap转换成byte数组
	 *
	 * @param b Bitmap对象的引用
	 * @return 返回转换成功后的byte数组，否则返回null
	 */
	public static byte[] bitmapToByte(Bitmap b) {
		if (b == null) {
			LogUtils.i(TAG, "Bitmap对象为空");
			return null;
		}
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 100, o);
		byte[] result = o.toByteArray();
		try {
			o.close();
		} catch (Exception e) {
			LogUtils.e(TAG, "bitmapToByte:关闭流异常", e);
		}
		return result;
	}

	/**
	 * Bitmap转换成byte数组
	 *
	 * @param b           Bitmap对象的引用
	 * @param needRecycle 是否需要回收Bitmap对象以节约内存
	 * @return 返回转换成功后的byte数组，否则返回null
	 */
	public static byte[] bitmapToByte(Bitmap b, boolean needRecycle) {
		if (b == null) {
			LogUtils.i(TAG, "Bitmap对象为空");
			return null;
		}
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 100, o);
		// 将图片压入流之后回收Bitmap对象，节约内存
		if (needRecycle) {
			b.recycle();
		}
		byte[] result = o.toByteArray();
		try {
			o.close();
		} catch (Exception e) {
			LogUtils.e(TAG, "bitmapToByte:关闭流异常", e);
		}
		return result;
	}

	/**
	 * Bitmap转换成byte数组
	 *
	 * @param b            Bitmap对象的引用
	 * @param compressRate 压缩率 0-100,图片是png格式有效，数字越小，压缩率越高
	 * @return 返回转换成功后的byte数组，否则返回null
	 */
	public static byte[] bitmapToByte(Bitmap b, int compressRate) {
		if (b == null) {
			LogUtils.i(TAG, "Bitmap对象为空");
			return null;
		}

		ByteArrayOutputStream o = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, compressRate, o);
		byte[] result = o.toByteArray();
		try {
			o.close();
		} catch (Exception e) {
			LogUtils.e(TAG, "bitmapToByte:关闭流异常", e);
		}
		return result;
	}

	/**
	 * Bitmap转换成byte数组
	 *
	 * @param b            Bitmap对象的引用
	 * @param compressRate 压缩率 0-100,图片是png格式有效，数字越小，压缩率越高
	 * @param needRecycle  是否需要回收bitmap对象
	 * @return 返回转换成功后的byte数组，否则返回null
	 */
	public static byte[] bitmapToByte(Bitmap b, int compressRate, boolean needRecycle) {
		if (b == null) {
			LogUtils.i(TAG, "Bitmap对象为空");
			return null;
		}
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, compressRate, o);
		// 将图片压入流之后回收Bitmap对象，节约内存
		if (needRecycle) {
			b.recycle();
		}
		byte[] result = o.toByteArray();
		try {
			o.close();
		} catch (Exception e) {
			LogUtils.e(TAG, "bitmapToByte:关闭流异常", e);
		}
		return result;
	}

	/**
	 * byte数组转换成Bitmap对象
	 *
	 * @param b byte数组
	 * @return 返回转换成功后的Bitmap对象，否则返回null
	 */
	public static Bitmap byteToBitmap(byte[] b) {
		return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
	}

	/**
	 * Drawable转换成Bitmap
	 *
	 * @param d Drawable对象
	 * @return 返回转换成功后的Bitmap对象，否则返回null
	 */
	public static Bitmap drawableToBitmap(Drawable d) {
		return d == null ? null : ((BitmapDrawable) d).getBitmap();
	}

	/**
	 * Bitmap转换成Drawable
	 *
	 * @param b Bitmap对象
	 * @return 返回转换成功后的Drawable对象，否则返回null
	 */
	@SuppressWarnings("deprecation")
	public static Drawable bitmapToDrawable(Bitmap b) {
		return b == null ? null : new BitmapDrawable(b);
	}

	/**
	 * Drawable转换成byte数组
	 *
	 * @param d Drawable对象
	 * @return 返回转换成功后的byte数组，否则返回null
	 */
	public static byte[] drawableToByte(Drawable d) {
		return bitmapToByte(drawableToBitmap(d));
	}

	/**
	 * byte数组转换成Drawable对象
	 *
	 * @param b byte数组
	 * @return 返回转换成功后的Drawable对象，否则返回null
	 */
	public static Drawable byteToDrawable(byte[] b) {
		return bitmapToDrawable(byteToBitmap(b));
	}

	/**
	 * Base64字符窜转Bitmap对象
	 *
	 * @param base64 Base64格式的字符窜
	 * @return 转换成功返回Bitmap对象，否则返回null
	 */
	public static Bitmap base64ToBitmap(String base64) {
		byte[] bytes = Base64.decode(base64, Base64.NO_WRAP);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bitmap;
	}

	/**
	 * Bitmap对象转换成Base64格式字符窜
	 *
	 * @param bitmap  Bitmap对象
	 * @param imgType 图片转换类型，图片类型在{@link ImageType}中定义，只支持转换jpg,jpeg,png
	 * @param quality 图片转换时候的压缩比，0-100的整数，数字越大，质量越高，图片类型如果为PNG则忽略这个参数进行全质量转换
	 * @return 转换成功返回Base64格式的字符窜，否则返回null
	 */
	public static String bitmapToBase64(Bitmap bitmap, ImageType imgType, int quality) {
		if (bitmap == null) {
			LogUtils.i(TAG, "Bitmap对象不能为空，无法转换");
			return null;
		}
		if (imgType != ImageType.JPEG && imgType != ImageType.JPG && imgType != ImageType.PNG) {
			LogUtils.i(TAG, "只支持jpg,jpeg和png类型的转换");
			return null;
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			if (imgType == ImageType.JPG || imgType == ImageType.JPEG) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
			} else if (imgType == ImageType.PNG) {
				bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
			}
			out.flush();
			byte[] bytes = out.toByteArray();
			byte[] outBytes = Base64.encode(bytes, Base64.NO_WRAP);
			return new String(outBytes);
		} catch (Exception e) {
			LogUtils.e(TAG, "Bitmap转Base64异常", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LogUtils.e(TAG, "IOException", e);
				}
			}
		}
		return null;
	}

	/**
	 * 根据url地址获取图片的Bitmap对象
	 *
	 * @param url 网络图片的url地址
	 * @return 返回网络图片的Bitmap对象，失败则返回null
	 */
	public static Bitmap getBitmapFromUrl(String url) {
		InputStream is = FileUtils.getInputStreamFromUrl(url);
		if (is == null) {
			LogUtils.i(TAG, "getBitmapFromUrl:文件输入流对象为空，不能获取Bitmap对象");
			return null;
		}
		Bitmap b = BitmapFactory.decodeStream(is, null, getDefaultOptions(1));
		// 使用完毕要关闭输入流对象
		FileUtils.closeInputStream(is);
		return b;
	}

	/**
	 * 根据url地址获取图片的Bitmap对象
	 *
	 * @param url            网络图片的url地址
	 * @param compressedRate 压缩比例，大于等于1的整数，2为压缩到1/2,4为压缩到1/4
	 * @return 返回网络图片的Bitmap对象，失败则返回null
	 */
	public static Bitmap getBitmapFromUrl(String url, int compressedRate) {
		InputStream is = FileUtils.getInputStreamFromUrl(url);
		if (is == null) {
			LogUtils.i(TAG, "getBitmapFromUrl:文件输入流对象为空，不能获取Bitmap对象");
			return null;
		}
		Bitmap b = BitmapFactory.decodeStream(is, null, getDefaultOptions(compressedRate));
		// 使用完毕要关闭输入流对象
		FileUtils.closeInputStream(is);
		return b;
	}

	/**
	 * 根据SD卡上的路径获取图片的Bitmap对象
	 *
	 * @param path 图片文件在SD卡上的路径
	 * @return 返回SD卡上图片的Bitmap对象，失败则返回null
	 */
	public static Bitmap getBitmapFromSDCard(String path) {
		FileInputStream fis = FileUtils.getInputStreamFromSDCard(path);
		if (fis == null) {
			LogUtils.i(TAG, "getBitmapFromSDCard:文件输入流对象为空，不能获取Bitmap对象");
			return null;
		}
		Bitmap b = BitmapFactory.decodeStream(fis, null, getDefaultOptions(1));
		// 使用完毕要关闭输入流对象
		FileUtils.closeInputStream(fis);
		return b;
	}

	/**
	 * 根据assets目录下图片的相对路径获取Bitmap对象
	 *
	 * @param context 上下文引用
	 * @param path    图片文件在assets目录下的相对路径
	 * @return 返回assets目录下图片的Bitmap对象，失败则返回null
	 */
	public static Bitmap getBitmapFromAssets(Context context, String path) {
		InputStream is = FileUtils.getInputStreamFromAssets(context, path);
		if (is == null) {
			LogUtils.i(TAG, "getBitmapFromAssets:文件输入流对象为空，不能获取Bitmap对象");
			return null;
		}
		Bitmap b = BitmapFactory.decodeStream(is, null, getDefaultOptions(1));
		// 使用完毕要关闭输入流对象
		FileUtils.closeInputStream(is);
		return b;
	}

	/**
	 * 获取图片缩略图
	 *
	 * @param path   图片路径
	 * @param height 指定高度
	 * @param width  指定宽度
	 * @param crop   是否裁剪
	 * @return 返回缩略图对象
	 */
	public static Bitmap extractThumbNail(final String path, final int height, final int width,
	                                      final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			LogUtils.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			LogUtils.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			LogUtils.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight +
					", orig=" + options.outWidth + "x" + options.outHeight +
					", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				LogUtils.e(TAG, "bitmap decode failed");
				return null;
			}

			LogUtils.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1,
						(bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
				LogUtils.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			LogUtils.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}

	/**
	 * 缩放指定的Bitmap对象，只是尺寸改变，没有对图片进行压缩<br>
	 * 在实际使用的时候应该取图片较长边的缩放比进行等比缩放才不至于导致图片变形
	 *
	 * @param bitmap      Bitmap对象
	 * @param scaleWidth  要缩放到的宽度
	 * @param scaleHeight 要缩放到的高度
	 * @return 返回经过缩放后的Bitmap对象，失败则返回null
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, float scaleWidth, float scaleHeight) {
		if (bitmap == null) {
			LogUtils.i(TAG, "scaleBitmap:Bitmap对象不能为空，缩放Bitmap失败");
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * 缩放指定的Bitmap对象，只是尺寸改变，没有对图片进行压缩<br>
	 * 在实际使用的时候应该取图片较长边的缩放比进行等比缩放才不至于导致图片变形
	 *
	 * @param bitmap      Bitmap对象
	 * @param scaleWidth  要缩放到的宽度
	 * @param scaleHeight 要缩放到的高度
	 * @return 返回经过缩放后的Bitmap对象，失败则返回null
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, int scaleWidth, int scaleHeight) {
		return scaleBitmap(bitmap, (float) scaleWidth / bitmap.getWidth(),
				(float) scaleHeight / bitmap.getHeight());
	}

	/**
	 * 销毁bitmap对象,释放内存
	 *
	 * @param bitmap 要回收的Bitmap对象
	 */
	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}

	/**
	 * 获取默认的BitmapFactory.Options
	 *
	 * @return 返回BitmapFactory.Options对象
	 */
	@SuppressWarnings("deprecation")
	private static BitmapFactory.Options getDefaultOptions(int rate) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		if (rate > 1)
			opt.inSampleSize = rate;
		opt.inDither = false; // Disable Dithering mode
		opt.inPurgeable = true; // Tell to gc that whether it needs free
		// memory, the Bitmap can be cleared
		opt.inInputShareable = true; // Which kind of reference will be
		// used to recover the Bitmap data
		// after being clear, when it will
		// be used in the future
		opt.inTempStorage = new byte[32 * 1024];
		return opt;
	}

	/**
	 * 图片类型枚举类
	 */
	public enum ImageType {
		JPG, JPEG, PNG, GIF, BMP
	}


}
