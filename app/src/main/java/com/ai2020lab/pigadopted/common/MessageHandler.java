/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.common;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ai2020lab.pigadopted.activity.SellerMainActivity;
import com.ai2020lab.pigadopted.fragment.AddPigDialog;
import com.ai2020lab.pigadopted.fragment.OnClickDialogBtnListener;
import com.ai2020lab.pigadopted.model.pig.PigInfo;


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

	private void showAddPigDialog(SellerMainActivity activity){
		AddPigDialog addPigDialog = AddPigDialog.newInstance(true,
				new OnClickDialogBtnListener<PigInfo>() {
					@Override
					public void onClickEnsure(Dialog dialog, PigInfo pigInfo) {
						dialog.dismiss();
						//添加猪
//						addPig();
					}

					@Override
					public void onClickCancel(Dialog dialog) {
						dialog.dismiss();
					}
				});
		addPigDialog.show(activity.getSupportFragmentManager(), null);
	}

	public void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setIndeterminate(true);
			// 不能点击对话框窗体外部来终止对话框
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setMessage("请稍候.....");
		}
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
	}

	public void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

}
