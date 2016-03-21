/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiviews.dialog.BaseDialog;
import com.ai2020lab.pigadopted.R;

/**
 * 添加猪成功对话框
 * Created by Justin Z on 2016/3/17.
 * 502953057@qq.com
 */
public class AddPigSuccessDialog extends DialogFragment {

	private final static String TAG = AddPigSuccessDialog.class.getSimpleName();

	private ImageView dialogTitleTopIv;
	private ImageView dialogTitleIv;
	private TextView dialogContentTv;
	private ImageView dialogCancelIv;
	private ImageView dialogEnsureIv;
	private ImageView pigLogo;

	private boolean loadAnim = false;

	private OnClickDialogBtnListener<Void> onClickDialogBtnListener;


	public static AddPigSuccessDialog newInstance(boolean loadAnim,
	                                              OnClickDialogBtnListener<Void> onClickDialogBtnListener) {
		AddPigSuccessDialog addPigFragment = new AddPigSuccessDialog();
		addPigFragment.onClickDialogBtnListener = onClickDialogBtnListener;
		addPigFragment.loadAnim = loadAnim;
		return addPigFragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// 警告对话框的内容
		View contentView = ViewUtils.makeView(getActivity(), R.layout.dialog_add_pig_success);
		BaseDialog.Builder builder = new BaseDialog.Builder(getActivity(), contentView);
		builder.setWidth(DisplayUtils.getScreenWidth(getActivity()));
		builder.setHeight(DisplayUtils.getScreenHeight(getActivity()));
		builder.setGravity(Gravity.CENTER);
		builder.setStyle(R.style.BaseAlertDialog);
		builder.setAnimStyle(R.style.DialogWindowAnimation_Scale);
		BaseDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		setDialogContent(dialog, contentView);
		if (loadAnim) {
			setVisible();
			loadAnimation();
		} else {
			setVisible();
		}
		return dialog;
	}

	/**
	 * 初始化各个界面元素，并绑定按钮事件
	 */
	private void setDialogContent(final Dialog dialog, final View contentView) {
		// 对号
		dialogTitleTopIv = (ImageView) contentView.findViewById(R.id.dialog_title_top_iv);
		// 标题提示
		dialogTitleIv = (ImageView) contentView.findViewById(R.id.dialog_title_iv);
		// 内容提示
		dialogContentTv = (TextView) contentView.findViewById(R.id.dialog_content_tv);
		// 取消按钮
		dialogCancelIv = (ImageView) contentView.findViewById(R.id.dialog_cancel_iv);
		// 确定按钮
		dialogEnsureIv = (ImageView) contentView.findViewById(R.id.dialog_ensure_iv);
		// 猪logo
		pigLogo = (ImageView) contentView.findViewById(R.id.pig_logo);
		dialogTitleTopIv.setImageDrawable(ResourcesUtils.getDrawable(R.mipmap.symbol_right));
		dialogTitleIv.setImageDrawable(ResourcesUtils.getDrawable(R.mipmap.add_pig_success));
		dialogContentTv.setText(getString(R.string.record_pig_growth_info));
		dialogEnsureIv.setImageDrawable(ResourcesUtils.getDrawable(R.drawable.take_pic_btn_selector));
		dialogCancelIv.setImageDrawable(ResourcesUtils.getDrawable(R.drawable.refuse_btn_selector));


		dialogEnsureIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onClickDialogBtnListener != null)
					onClickDialogBtnListener.onClickEnsure(dialog, null);
			}
		});
		dialogCancelIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onClickDialogBtnListener != null)
					onClickDialogBtnListener.onClickCancel(dialog);

			}
		});
	}

	private void setVisible() {
		dialogTitleTopIv.setVisibility(View.VISIBLE);
		dialogTitleIv.setVisibility(View.VISIBLE);
		dialogContentTv.setVisibility(View.VISIBLE);
		dialogCancelIv.setVisibility(View.VISIBLE);
		dialogEnsureIv.setVisibility(View.VISIBLE);
		pigLogo.setVisibility(View.VISIBLE);
	}

	/**
	 * 为各个界面元素加入动画效果
	 */
	private void loadAnimation() {
		Animation slideInLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
		Animation slideInRight = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in);
		Animation scaleIn = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_alpha_in);
		slideInLeft.setDuration(1000);
		slideInRight.setDuration(1000);
		scaleIn.setDuration(1000);
		scaleIn.setInterpolator(new BounceInterpolator());
		slideInLeft.setInterpolator(new AnticipateOvershootInterpolator());
		slideInRight.setInterpolator(new AnticipateOvershootInterpolator());
		dialogTitleTopIv.startAnimation(scaleIn);
		dialogTitleIv.startAnimation(scaleIn);
		dialogContentTv.startAnimation(scaleIn);
		pigLogo.startAnimation(scaleIn);
		dialogCancelIv.startAnimation(slideInLeft);
		dialogEnsureIv.startAnimation(slideInRight);
	}


}
