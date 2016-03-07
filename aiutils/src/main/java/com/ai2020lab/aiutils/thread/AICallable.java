package com.ai2020lab.aiutils.thread;

import com.ai2020lab.aiutils.common.LogUtils;

import java.util.concurrent.Callable;

/**
 * Created by Justin on 2016/2/2.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class AICallable implements Callable<String> {

	private final static String TAG = AICallable.class.getSimpleName();

	private TaskRunnable runnable;

	public AICallable(TaskRunnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public String call() throws Exception {
		LogUtils.i(TAG, "执行call---");
		String result = runnable.doInBackground();

		return result;
	}
}
