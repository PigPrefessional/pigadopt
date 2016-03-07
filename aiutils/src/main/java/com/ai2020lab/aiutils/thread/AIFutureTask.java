package com.ai2020lab.aiutils.thread;

import android.os.Message;

import com.ai2020lab.aiutils.common.LogUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by Justin on 2016/2/2.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class AIFutureTask extends FutureTask<String> {

	private final static String TAG = AIFutureTask.class.getSimpleName();

	private TaskRunnable runnable;

	private AIHandler handler;

	public AIFutureTask(Callable<String> callable) {
		super(callable);
	}

	public AIFutureTask(Runnable runnable, String result) {
		super(runnable, result);
	}

	public AIFutureTask(AIHandler handler, Callable<String> callable, TaskRunnable runnable) {
		super(callable);
		this.handler = handler;
		this.runnable = runnable;
	}

	/**
	 * 任务完成回调
	 */
	@Override
	protected void done() {
		LogUtils.i(TAG, "任务执行完毕");
		Message msg = handler.obtainMessage(AIHandler.MSG_POST_RESULT);
		msg.sendToTarget();
	}
}
