/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.model.pig.PigPhotoUploadRequest;
import com.ai2020lab.pigadopted.model.pig.PigPhotoUploadResponse;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;
import com.nostra13.universalimageloader.core.ImageLoader;

import cz.msebera.android.httpclient.Header;

/**
 * 猪照片上传界面
 * Created by Justin Z on 2016/4/20.
 * 502953057@qq.com
 */
public class UploadPigPhotoActivity extends AIBaseActivity {

	/**
	 * 日志标题
	 */
	private final static String TAG = UploadPigPhotoActivity.class.getSimpleName();


	private ImageView pigPhotoIv;
	private ImageView pigPhotoCropIv;
	private TextView pigPhotoAngleTv;
	private TextView pigPhotoHeightTv;
	private TextView pigPhotoDistanceTv;

	/**
	 * 拍摄猪照片上传数据
	 */
	private PigPhotoUploadRequest pigPhotoData;

	/**
	 * 入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_pig_photo);
		pigPhotoData = (PigPhotoUploadRequest) getIntent()
				.getSerializableExtra(IntentExtra.PIG_PHOTO_UPLOAD);
		setToolbar();
		assignViews();
		setPigPhotoInfo();
		setPigPhotoIv();
	}

	private void setToolbar() {
		supportToolbar(true);
		setToolbarTitle(getString(R.string.activity_title_upload_pig_photo));
		setToolbarLeft(R.drawable.toolbar_back_selector);
		setToolbarRight(getString(R.string.ensure));
		setOnLeftClickListener(new OnLeftClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});
		setOnRightClickListener(new OnRightClickListener() {
			@Override
			public void onClick() {
				// 确定发起添加猪圈请求
				requestUploadPigPhoto();
			}
		});
	}

	private void assignViews() {
		pigPhotoIv = (ImageView) findViewById(R.id.pig_photo_iv);
		pigPhotoCropIv = (ImageView) findViewById(R.id.pig_photo_crop_iv);
		pigPhotoAngleTv = (TextView) findViewById(R.id.pig_photo_angle_tv);
		pigPhotoHeightTv = (TextView) findViewById(R.id.pig_photo_height_tv);
		pigPhotoDistanceTv = (TextView) findViewById(R.id.pig_photo_distance_tv);
	}

	/**
	 * 设置界面信息展示
	 */
	private void setPigPhotoInfo() {
		if (pigPhotoData == null) {
			return;
		}
		String distanceShow = String.format(getString(R.string.pig_photo_distance_value),
				pigPhotoData.distance);
		String angleShow = String.format(getString(R.string.pig_photo_angle_value),
				pigPhotoData.verticalAngle);
		String heightShow = String.format(getString(R.string.pig_photo_height_value),
				pigPhotoData.height);
		pigPhotoDistanceTv.setText(distanceShow);
		pigPhotoAngleTv.setText(angleShow);
		pigPhotoHeightTv.setText(heightShow);
	}

	/**
	 * 设置界面图片展示
	 */
	private void setPigPhotoIv() {
		if (pigPhotoData == null) {
			return;
		}
		ImageLoader.getInstance().displayImage("file://" + pigPhotoData.pigPhoto, pigPhotoIv);
		pigPhotoIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				skipToPhotoDetailActivity(pigPhotoData.pigPhoto);
			}
		});
		if (!TextUtils.isEmpty(pigPhotoData.pigPhotoCrop)) {
			ImageLoader.getInstance().displayImage("file://" + pigPhotoData.pigPhotoCrop,
					pigPhotoCropIv);
			pigPhotoCropIv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					skipToPhotoDetailActivity(pigPhotoData.pigPhotoCrop);
				}
			});
		}
	}

	/**
	 * 跳转到展示大图界面
	 */
	private void skipToPhotoDetailActivity(String photoPath) {
		Intent intent = new Intent(this, PhotoDetailActivity.class);
		intent.putExtra(IntentExtra.PHOTO_PATH, photoPath);
		startActivity(intent);
	}

	/**
	 * 上传猪照片请求
	 */
	private void requestUploadPigPhoto() {
		LogUtils.i(TAG, "--上传猪照片请求--");
		// 弹出提示
		showLoading(getString(R.string.prompt_uploading));
		HttpManager.postFile(this, UrlName.GROWTH_INFO_UPLOAD.getUrl(),
				pigPhotoData, pigPhotoData.pigPhoto,
				new JsonHttpResponseHandler<PigPhotoUploadResponse>(this) {

					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            final PigPhotoUploadResponse jsonObj) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								LogUtils.i(TAG, "--上传猪照片成功--");
								ToastUtils.getInstance().showToast(getApplicationContext(),
										R.string.prompt_uploading_success);
								// TODO:回到拍照界面
								finish();
							}
						}, 1000);
					}

					@Override
					public void onCancel() {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								// 没有网络的情况会终止请求
								ToastUtils.getInstance().showToast(getApplicationContext(),
										R.string.prompt_uploading_failure);
							}
						}, 1000);
					}

					@Override
					public void onHandleFailure(String errorMsg) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								ToastUtils.getInstance().showToast(getApplicationContext(),
										R.string.prompt_uploading_failure);
							}
						}, 1000);
					}

				});

	}


}
