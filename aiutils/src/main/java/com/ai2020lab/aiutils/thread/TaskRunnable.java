package com.ai2020lab.aiutils.thread;

/**
 * Created by Justin on 2016/2/2.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public interface TaskRunnable {
	/**
	 * 子线程执行回调方法，由子类实现
	 */
	String doInBackground();

	/**
	 * 子线程执行完毕回调方法，由子类实现
	 */
	void onCompleted();

	/**
	 * 子线程中刷新界面回调，由子类实现
	 */
	void onRefresh();
}
