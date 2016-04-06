/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.dialog.BaseDialog;
import com.ai2020lab.pigadopted.R;

/**
 * Created by Justin Z on 2016/4/5.
 * 502953057@qq.com
 */
public class LoadingDialog extends DialogFragment {
	private final static String TAG = LoadingDialog.class.getSimpleName();
	/**
	 * 加载动画ImageView
	 */
	private ImageView loadingIv;
	/**
	 * 加载提示TextView
	 */
	private TextView loadingTv;
	/**
	 * 加载提示
	 */
	private String msg;

	private AnimationDrawable loadingAnim;

	private DialogInterface.OnCancelListener onCancelListener;

	/**
	 * 创建对话框方法
	 *
	 * @param msg              加载提示
	 * @param onCancelListener 取消监听
	 * @return LoadingDialog
	 */
	public static LoadingDialog newInstance(String msg,
	                                        DialogInterface.OnCancelListener
			                                        onCancelListener) {
		LogUtils.i(TAG, "创建对话框");
		LoadingDialog loadingDialog = new LoadingDialog();
		loadingDialog.msg = msg;
		loadingDialog.onCancelListener = onCancelListener;
		return loadingDialog;
	}

	public static LoadingDialog newInstance(String msg) {
		LogUtils.i(TAG, "创建对话框");
		LoadingDialog loadingDialog = new LoadingDialog();
		loadingDialog.msg = msg;
		return loadingDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LogUtils.i(TAG, "onCreateDialog");
		View contentView = ViewUtils.makeView(getActivity(), R.layout.dialog_loading);
		BaseDialog dialog = createDialog(contentView);
		assignViews(contentView);
		setLoadingTv();
		loadAnimation();
		return dialog;
	}

	/**
	 * 创建Dialog
	 */
	private BaseDialog createDialog(View contentView) {
		BaseDialog.Builder builder = new BaseDialog.Builder(getActivity(), contentView);
		builder.setGravity(Gravity.CENTER);
		builder.setStyle(R.style.BaseAlertDialog);
		builder.setAnimStyle(R.style.windowAnimScale);
		BaseDialog dialog = builder.create();
//		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				if (loadingAnim != null) loadingAnim.stop();
//			}
//		});
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	private void assignViews(View view) {
		loadingIv = (ImageView) view.findViewById(R.id.loadingIv);
		loadingTv = (TextView) view.findViewById(R.id.loadingTv);
	}

	private void setLoadingTv() {
		loadingTv.setText(msg);
		loadingTv.getPaint().setFakeBoldText(true);
	}

	private void loadAnimation() {
		loadingIv.setImageDrawable(ResourcesUtils.getDrawable(R.drawable.loading_anim));
		loadingAnim = (AnimationDrawable) loadingIv.getDrawable();
		loadingAnim.start();
	}


}
