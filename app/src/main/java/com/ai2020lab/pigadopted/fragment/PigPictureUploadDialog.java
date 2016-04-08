/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiviews.dialog.BaseDialog;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.model.pig.PigPhotoUploadRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 猪照片上传确认对话框
 * Created by Justin Z on 2016/4/8.
 * 502953057@qq.com
 */
public class PigPictureUploadDialog extends DialogFragment {
	private final static String TAG = PigPictureUploadDialog.class.getSimpleName();
	private boolean loadAnim;

	private ImageView pigPhotoTitleIv;
	private ImageView pigPhotoIv;
	private TextView pigPhotoDistanceTv;
	private TextView pigPhotoAngleTv;
	private Button cancelBtn;
	private Button ensureBtn;

	private PigPhotoUploadRequest pigPhotoData;
	private OnClickDialogBtnListener<Void> onClickDialogBtnListener;

	/**
	 * 创建对话框方法
	 *
	 * @param loadAnim                 是否加载窗口动画
	 * @param onClickDialogBtnListener 点击对话框按钮事件监听
	 * @return HogpenAddDialog
	 */
	public static PigPictureUploadDialog newInstance(boolean loadAnim, PigPhotoUploadRequest pigPhotoData,
	                                                 OnClickDialogBtnListener<Void> onClickDialogBtnListener) {
		LogUtils.i(TAG, "创建对话框");
		PigPictureUploadDialog pigDf = new PigPictureUploadDialog();
		pigDf.loadAnim = loadAnim;
		pigDf.pigPhotoData = pigPhotoData;
		pigDf.onClickDialogBtnListener = onClickDialogBtnListener;
		return pigDf;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LogUtils.i(TAG, "onCreateDialog");
		View contentView = ViewUtils.makeView(getActivity(), R.layout.dialog_upload_pig_photo);
		BaseDialog dialog = createDialog(contentView);
		assignViews(contentView);
		setTextFonts();
		setDialogBtnClickListener();
		setPigPhoto();
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
		if (loadAnim)
			builder.setAnimStyle(R.style.windowAnimScale);
		BaseDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	private void assignViews(View contentView) {
		pigPhotoTitleIv = (ImageView) contentView.findViewById(R.id.pig_photo_title_iv);
		pigPhotoIv = (ImageView) contentView.findViewById(R.id.pig_photo_iv);
		pigPhotoDistanceTv = (TextView) contentView.findViewById(R.id.pig_photo_distance_tv);
		pigPhotoAngleTv = (TextView) contentView.findViewById(R.id.pig_photo_angle_tv);
		cancelBtn = (Button) contentView.findViewById(R.id.cancel_btn);
		ensureBtn = (Button) contentView.findViewById(R.id.ensure_btn);
	}

	private void setTextFonts() {
		// 标题和按钮文字全部使用中文粗体
		pigPhotoDistanceTv.getPaint().setFakeBoldText(true);
		pigPhotoAngleTv.getPaint().setFakeBoldText(true);
		cancelBtn.getPaint().setFakeBoldText(true);
		ensureBtn.getPaint().setFakeBoldText(true);
	}

	private void setPigPhoto(){
		LogUtils.i(TAG, "设置猪圈图片");
		pigPhotoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//		hogpenPhotoIv.setImageBitmap(ImageUtils.getBitmapFromSDCard(path, 4));
		ImageLoader.getInstance().displayImage("file://" + pigPhotoData.pigPhoto, pigPhotoIv);
	}

	/**
	 * 绑定按钮事件
	 */
	private void setDialogBtnClickListener() {
		ensureBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onClickDialogBtnListener != null)
					onClickDialogBtnListener.onClickEnsure(PigPictureUploadDialog.this, null);
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onClickDialogBtnListener != null)
					onClickDialogBtnListener.onClickCancel(PigPictureUploadDialog.this);

			}
		});
	}


}
