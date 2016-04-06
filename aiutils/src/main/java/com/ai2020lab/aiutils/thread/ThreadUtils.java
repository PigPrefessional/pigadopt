package com.ai2020lab.aiutils.thread;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.ai2020lab.aiutils.common.LogUtils;

/**
 * 线程操作工具类
 * Created by Justin on 2016/1/29.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ThreadUtils {

	private final static String TAG = ThreadUtils.class.getSimpleName();

	/**
	 * 主线程执行指定任务
	 *
	 * @param activity 上下文引用
	 * @param runnable Runnable引用
	 */
	public static void runOnUIThread(Activity activity, Runnable runnable) {
		if (activity == null) {
			LogUtils.i(TAG, "runOnUIThread:上下文引用为空");
			return;
		}
		activity.runOnUiThread(runnable);
	}

	/**
	 * 主线程执行指定任务
	 *
	 * @param runnable  Runnable引用
	 * @param delayTime 延迟时间
	 */
	public static void runOnUIThread(Runnable runnable, long delayTime) {
		new Handler().postDelayed(runnable, delayTime);
	}

	/**
	 * 子线程执行任务<p>
	 *
	 * @param runnable Runnable对象的引用
	 */
	public static void runOnSubThread(Runnable runnable) {
		HandlerThread thread = new HandlerThread(Long.toHexString(System.nanoTime()));
		thread.start();
		new Handler(thread.getLooper()).post(runnable);
	}

	/**
	 * 子线程执行任务<p>
	 * 不适合大量的耗时任务，可以将文件读取操作的执行放在这里，以提高UI线程的效率
	 *
	 * @param runnable TaskSimpleRunnable
	 */
	public static void runOnSubThread(TaskSimpleRunnable runnable) {
		final String key = Long.toHexString(System.nanoTime());
		HandlerThread thread = new HandlerThread(key);
		thread.start();
		final TaskRunnable run = runnable;
		final AIHandler handler = new AIHandler(runnable,
				thread.getLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				// 执行后台任务
				run.doInBackground();
				// 发送消息告知后台任务执行完毕
				Message msg = handler.obtainMessage(AIHandler.MSG_POST_RESULT);
				msg.sendToTarget();
			}
		});
	}

	/**
	 * 子线程执行任务<p>
	 * 不适合大量的耗时任务，可以将文件读取操作的执行放在这里，以提高UI线程的效率
	 *
	 * @param runnable  TaskSimpleRunnable
	 * @param delayTime 延迟时间
	 */
	public static void runOnSubThread(TaskSimpleRunnable runnable, long delayTime) {
		final String key = Long.toHexString(System.nanoTime());
		HandlerThread thread = new HandlerThread(key);
		thread.start();
		final TaskRunnable run = runnable;
		final AIHandler handler = new AIHandler(runnable,
				thread.getLooper());
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 执行后台任务
				run.doInBackground();
				// 发送消息告知后台任务执行完毕
				Message msg = handler.obtainMessage(AIHandler.MSG_POST_RESULT);
				msg.sendToTarget();
			}
		}, delayTime);
	}


}
