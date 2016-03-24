/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiviews.dialog.BaseDialog;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.model.hogpen.SellerHogpenInfo;
import com.ai2020lab.pigadopted.view.pickerview.DimensionPickerView;

import java.util.ArrayList;

/**
 * 添加猪圈对话框
 * Created by Justin Z on 2016/3/24.
 * 502953057@qq.com
 */
public class AddHogpenDialog extends DialogFragment {
	private final static String TAG = AddHogpenDialog.class.getSimpleName();

	private OnClickDialogBtnListener<SellerHogpenInfo> onClickDialogBtnListener;

	private boolean loadAnim = false;


	private ImageView addHogpenTitleIv;
	private TextView hogpenLengthTv;
	private DimensionPickerView hogpenLengthPv;
	private TextView hogpenWidthTv;
	private DimensionPickerView hogpenWidthPv;
	private TextView hogpenPhotoTv;
	private ImageView hogpenPhotoIv;
	private Button cancelBtn;
	private Button ensureBtn;

	/**
	 * 创建对话框方法
	 *
	 * @param loadAnim                 是否加载动画
	 * @param onClickDialogBtnListener 点击按钮事件监听
	 * @return AddHogpenDialog
	 */
	public static AddHogpenDialog newInstance(boolean loadAnim,
                          OnClickDialogBtnListener<SellerHogpenInfo> onClickDialogBtnListener) {
		AddHogpenDialog addHogpenFragment = new AddHogpenDialog();
		addHogpenFragment.onClickDialogBtnListener = onClickDialogBtnListener;
		addHogpenFragment.loadAnim = loadAnim;
		LogUtils.i(TAG, "创建对话框");
		return addHogpenFragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LogUtils.i(TAG, "onCreateDialog");
//		long startTime = System.currentTimeMillis();
		View contentView = ViewUtils.makeView(getActivity(), R.layout.dialog_add_hogpen);
		BaseDialog dialog = createDialog(contentView);
		assignViews(contentView);
		setTextFonts();
		setDialogBtnClickListener(dialog);
//		long endTime = System.currentTimeMillis();
//		LogUtils.i(TAG, "消耗时间：" + (endTime - startTime) / 1000 + "秒");

//		if (loadAnim) {
//			setVisible();
//			loadAnimation();
//		} else {
//			setVisible();
//		}
		return dialog;
	}

	/**
	 * 创建Dialog
	 */
	private BaseDialog createDialog(View contentView) {
		BaseDialog.Builder builder = new BaseDialog.Builder(getActivity(), contentView);
		builder.setWidth(DisplayUtils.getScreenWidth(getActivity()));
		builder.setHeight(DisplayUtils.getScreenHeight(getActivity()));
		builder.setGravity(Gravity.CENTER);
		builder.setStyle(R.style.BaseAlertDialog);
		builder.setAnimStyle(R.style.DialogWindowAnimation_Scale);
		BaseDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	private void assignViews(View contentView) {
		addHogpenTitleIv = (ImageView) contentView.findViewById(R.id.add_hogpen_title_iv);
		hogpenLengthTv = (TextView) contentView.findViewById(R.id.hogpen_length_tv);
		hogpenLengthPv = (DimensionPickerView) contentView.findViewById(R.id.hogpen_length_pv);
		hogpenWidthTv = (TextView) contentView.findViewById(R.id.hogpen_width_tv);
		hogpenWidthPv = (DimensionPickerView) contentView.findViewById(R.id.hogpen_width_pv);
		hogpenPhotoTv = (TextView) contentView.findViewById(R.id.hogpen_photo_tv);
		hogpenPhotoIv = (ImageView) contentView.findViewById(R.id.hogpen_photo_iv);
		cancelBtn = (Button) contentView.findViewById(R.id.cancel_btn);
		ensureBtn = (Button) contentView.findViewById(R.id.ensure_btn);
	}

	private void setTextFonts() {
		// 标题和按钮文字全部使用中文粗体
		hogpenLengthTv.getPaint().setFakeBoldText(true);
		hogpenWidthTv.getPaint().setFakeBoldText(true);
		hogpenPhotoTv.getPaint().setFakeBoldText(true);
		cancelBtn.getPaint().setFakeBoldText(true);
		ensureBtn.getPaint().setFakeBoldText(true);
	}


	private SellerHogpenInfo getSelectHogpenInfo() {
		SellerHogpenInfo hogpenInfo = new SellerHogpenInfo();
		// 宽度
		hogpenInfo.hogpenWidth = hogpenLengthPv.getSelectDimension();
		// 高度
		hogpenInfo.hogpenLength = hogpenWidthPv.getSelectDimension();
		hogpenInfo.pigInfos = new ArrayList<>();
		LogUtils.i(TAG, "猪圈宽度:" + hogpenInfo.hogpenWidth);
		LogUtils.i(TAG, "猪圈长度:" + hogpenInfo.hogpenLength);
//		LogUtils.i(TAG, "入栏时间:" + pigInfo.attendedDate);
		return hogpenInfo;
	}

	/**
	 * 绑定按钮事件
	 */
	private void setDialogBtnClickListener(final BaseDialog dialog) {
		ensureBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				getSelectHogpenInfo();
				if (onClickDialogBtnListener != null)
					onClickDialogBtnListener.onClickEnsure(AddHogpenDialog.this, getSelectHogpenInfo());
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddHogpenDialog.this.dismiss();
				if (onClickDialogBtnListener != null)
					onClickDialogBtnListener.onClickCancel(AddHogpenDialog.this);

			}
		});
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		LogUtils.i(TAG, "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.i(TAG, "onCreate");
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtils.i(TAG, "onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LogUtils.i(TAG, "onViewCreated");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtils.i(TAG, "onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		LogUtils.i(TAG, "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtils.i(TAG, "onResume");
	}

	// 销毁的时候
	@Override
	public void onPause() {
		super.onPause();
		LogUtils.i(TAG, "onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		LogUtils.i(TAG, "onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LogUtils.i(TAG, "onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtils.i(TAG, "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		LogUtils.i(TAG, "onDetach");
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		LogUtils.i(TAG, "onCancel");
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		LogUtils.i(TAG, "onDismiss");
	}


}
