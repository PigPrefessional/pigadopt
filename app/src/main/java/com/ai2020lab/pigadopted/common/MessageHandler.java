/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;


/**
 * 全局消息处理类
 */
public class MessageHandler extends Handler {

	private final static String TAG = MessageHandler.class.getSimpleName();


	private static MessageHandler instance;

	private Context mContext;

	private ProgressDialog mProgressDialog;

	private MessageHandler() {

	}

	public static synchronized MessageHandler getInstance() {
		if (instance == null) {
			instance = new MessageHandler();
		}
		return instance;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
			// 打开添加猪对话框
			case Constants.OPEN_DIALOG_ADD_PIG: // 关闭提示框信息
//				dismissDialog();
				break;
			// 打开添加猪成功提示对话框
			case Constants.OPEN_DIALOG_ADD_PIG_SUCCESS:

				break;
			default:
				break;
		}
	}


}
