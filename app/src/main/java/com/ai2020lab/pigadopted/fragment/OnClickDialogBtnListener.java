/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.fragment;

import android.app.DialogFragment;

/**
 * 点击对话框按钮事件监听
 * Created by Justin Z on 2016/3/18.
 * 502953057@qq.com
 */
public interface OnClickDialogBtnListener<T> {
	/**
	 * 点击确认按钮
	 *
	 * @param dialog Dialog
	 */
	void onClickEnsure(DialogFragment df, T t);

	/**
	 * 点击取消按钮
	 *
	 * @param dialog Dialog
	 */
	void onClickCancel(DialogFragment df);
}
