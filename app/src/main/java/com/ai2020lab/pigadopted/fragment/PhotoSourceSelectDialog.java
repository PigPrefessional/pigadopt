/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.dialog.BaseDialog;
import com.ai2020lab.pigadopted.R;

/**
 * 选择照片来源对话框
 * Created by Justin Z on 2016/3/24.
 * 502953057@qq.com
 */
public class PhotoSourceSelectDialog extends DialogFragment {
	private final static String TAG = HogpenAddDialog.class.getSimpleName();

	private boolean loadAnim = false;

	private TextView fromAlbumTv;
	private TextView fromCameraTv;
	private OnClickPhotoSelectListener onClickPhotoSelectListener;

	/**
	 * 创建对话框方法
	 *
	 * @param loadAnim 是否加载动画
	 * @return HogpenAddDialog
	 */
	public static PhotoSourceSelectDialog newInstance(boolean loadAnim,
                              OnClickPhotoSelectListener onClickPhotoSelectListener) {
		PhotoSourceSelectDialog photoSourceSelectDialog = new PhotoSourceSelectDialog();
		photoSourceSelectDialog.loadAnim = loadAnim;
		photoSourceSelectDialog.onClickPhotoSelectListener = onClickPhotoSelectListener;
		return photoSourceSelectDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LogUtils.i(TAG, "onCreateDialog");
		View contentView = ViewUtils.makeView(getActivity(), R.layout.dialog_photo_source_select);
		BaseDialog dialog = createDialog(contentView);
		assignViews(contentView);
		setTextFonts();
		setOnClickDialogListener();
		return dialog;
	}

	/**
	 * 创建Dialog
	 */
	private BaseDialog createDialog(View contentView) {
		BaseDialog.Builder builder = new BaseDialog.Builder(getActivity(), contentView);
		builder.setGravity(Gravity.BOTTOM);
		builder.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		builder.setStyle(R.style.BaseAlertDialog);
		if (loadAnim)
			builder.setAnimStyle(R.style.windowAnimSlideBottom);
		BaseDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	private void assignViews(View contentView) {
		fromAlbumTv = (TextView) contentView.findViewById(R.id.from_album_tv);
		fromCameraTv = (TextView) contentView.findViewById(R.id.from_camera_tv);
	}

	private void setTextFonts() {
		// 标题和按钮文字全部使用中文粗体
		fromAlbumTv.getPaint().setFakeBoldText(true);
		fromCameraTv.getPaint().setFakeBoldText(true);
	}

	private void setOnClickDialogListener() {
		fromAlbumTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.i(TAG, "选择系统相册");
				if(onClickPhotoSelectListener != null)
					onClickPhotoSelectListener.onSelectAlbum(PhotoSourceSelectDialog.this);
			}
		});
		fromCameraTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.i(TAG, "打开相机");
				if(onClickPhotoSelectListener != null)
					onClickPhotoSelectListener.onSelectCamera(PhotoSourceSelectDialog.this);
			}
		});
	}

	public interface OnClickPhotoSelectListener{
		void onSelectAlbum(DialogFragment df);
		void onSelectCamera(DialogFragment df);
	}

}
