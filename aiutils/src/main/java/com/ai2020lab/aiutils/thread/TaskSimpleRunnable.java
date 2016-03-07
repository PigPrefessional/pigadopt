package com.ai2020lab.aiutils.thread;

/**
 * Created by Justin on 2016/2/2.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public abstract class TaskSimpleRunnable implements TaskRunnable {
	/**
	 * 子线程执行回调方法，由子类实现
	 */
	@Override
	public abstract String doInBackground();

	/**
	 * 子线程执行完毕回调方法，由子类实现
	 */
	@Override
	public void onCompleted() {

	}

	@Override
	public void onRefresh() {

	}
}
