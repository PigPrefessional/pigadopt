package com.ai2020lab.aiutils.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by Justin on 2016/2/2.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class AIHandler extends Handler {

	public final static int MSG_POST_RESULT = 0x10000;

	public final static int MSG_UPDATE_PROGRESS = 0x10001;

	private TaskRunnable runnable;

	public AIHandler() {
	}

	public AIHandler(Looper looper) {
		super(looper);
	}

	public AIHandler(TaskRunnable listener, Looper looper) {
		super(looper);
		runnable = listener;
	}

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == MSG_POST_RESULT && runnable != null) {
			// 执行任务结束回调
			runnable.onCompleted();
		}
		else if(msg.what == MSG_UPDATE_PROGRESS && runnable != null){
			runnable.onRefresh();
		}
	}

}
