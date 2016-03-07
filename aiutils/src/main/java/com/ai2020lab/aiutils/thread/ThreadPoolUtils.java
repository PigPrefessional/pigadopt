package com.ai2020lab.aiutils.thread;

import android.os.Looper;

import com.ai2020lab.aiutils.common.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工具类
 * Created by Justin on 2016/2/3.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ThreadPoolUtils {

	private final static String TAG = ThreadPoolUtils.class.getSimpleName();

	private static HashMap<String, AIFutureTask> tasks;
	private static ExecutorService executor;

	/**
	 * 初始化方法
	 */
	public static void init() {
		LogUtils.i(TAG, "线程池工具类默认初始化");
		if (executor == null)
			executor = Executors.newCachedThreadPool();
		if (tasks == null)
			tasks = new HashMap<>();
	}

	/**
	 * 初始化方法，传入一个ExecutorService的实现
	 *
	 * @param threadPool ExecutorService
	 */
	public static void init(ExecutorService threadPool) {
		if (executor == null)
			executor = threadPool;
		if (tasks == null)
			tasks = new HashMap<>();
	}

	/**
	 * 线程池执行任务，执行任务成功获得任务的唯一标示key<p>
	 * 可执行大量的耗时任务
	 *
	 * @param runnable TaskSimpleRunnable
	 * @return 执行成功返回key
	 */
	public static String execute(TaskSimpleRunnable runnable) {
		if (executor == null) {
			LogUtils.i(TAG, "请先调用初始化方法");
			return null;
		}
		if (tasks == null) {
			LogUtils.i(TAG, "请先调用初始化方法");
			return null;
		}
		if (runnable == null) {
			LogUtils.i(TAG, "TaskRunnable不能为空");
			return null;
		}
		AICallable callable = new AICallable(runnable);
		AIHandler handler = new AIHandler(runnable, Looper.myLooper());
		AIFutureTask task = new AIFutureTask(handler, callable, runnable);
		executor.execute(task);
		String key = Long.toHexString(System.nanoTime());
		tasks.put(key, task);
		return key;
	}

	/**
	 * 判断指定key的任务是否执行完毕
	 *
	 * @param key String
	 * @return true-执行完毕，false-没有执行完毕
	 */
	public static boolean isDone(String key) {
		if (tasks == null) {
			LogUtils.i(TAG, "请先调用初始化方法");
			return false;
		}
		AIFutureTask task = tasks.get(key);
		return task != null && task.isDone();
	}

	/**
	 * 取消执行指定key的任务
	 *
	 * @param key String
	 */
	public static void cancelTask(String key) {
		if (tasks == null) {
			LogUtils.i(TAG, "请先调用初始化方法");
			return;
		}
		AIFutureTask task = tasks.get(key);
		if (task == null) return;
		if (task.isDone()) return;
		task.cancel(true);
	}

	/**
	 * 取消任务栈中的所有任务
	 */
	public static void cancelTask() {
		if (tasks == null) {
			LogUtils.i(TAG, "cancelTask:还没有初始化");
			return;
		}
		for (Map.Entry<String, AIFutureTask> entry : tasks.entrySet()) {
			AIFutureTask task = entry.getValue();
			if (task == null) continue;
			if (task.isDone()) continue;
			task.cancel(true);
			tasks.remove(entry.getKey());
		}
	}

	/**
	 * 释放资源<p>
	 * 调用这个方法之后，线程池执行完所有正在执行的任务之后就会关闭，任务栈置空
	 */
	public static void release() {
		LogUtils.i(TAG, "线程池工具类释放资源");
		cancelTask();
		if (executor != null)
			executor.shutdownNow();
		executor = null;
		tasks = null;
	}


}
