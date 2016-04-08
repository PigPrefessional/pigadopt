/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

import com.ai2020lab.aimedia.CameraManager;
import com.ai2020lab.aimedia.CameraSurfaceView;
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.TimeUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.storage.FileUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.fragment.OnClickDialogBtnListener;
import com.ai2020lab.pigadopted.fragment.PigPictureUploadDialog;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.model.pig.PigPhotoUploadRequest;
import com.ai2020lab.pigadopted.model.pig.PigPhotoUploadResponse;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;

import java.io.File;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * 定距离拍照界面
 * Created by Justin Z on 2016/3/28.
 * 502953057@qq.com
 */
public class DistanceCameraActivity extends AIBaseActivity {

	private final static String TAG = DistanceCameraActivity.class.getSimpleName();
	public final static String PIG_DISTANCE_PHOTO_PATH = "pig_distance_photo";
	/**
	 * 上传猪照片对话框TAG
	 */
	private final static String TAG_DIALOG_UPLOAD_PIG_PHOTO = "tag_dialog_UPLOAD_PIG_PHOTO";

	private CameraSurfaceView takePhotoSv;
	private ImageView takePhotoIv;

	private PigInfo pigInfo;
	private PigPhotoUploadRequest pigPhotoData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pigInfo = (PigInfo) getIntent().getSerializableExtra(IntentExtra.PIG_INFO);
		pigPhotoData = new PigPhotoUploadRequest();
		pigPhotoData.pigID = pigInfo.pigID;
		// TODO：暂时设定为固定值
		setPigPhotoData(1.5f, 60);
		setContentView(R.layout.activity_distance_camera);
		setToolbar();
		assignViews();
		setTakePhotoSv();
		setTakePhotoIv();
	}

	private void setToolbar() {
		supportToolbar(true);
		setToolbarTitle(getString(R.string.activity_distance_camera));
		setToolbarLeft(R.drawable.toolbar_back_selector);
		// 返回按钮监听
		setOnLeftClickListener(new OnLeftClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});
	}

	/**
	 * 分配各个View
	 */
	private void assignViews() {
		takePhotoSv = (CameraSurfaceView) findViewById(R.id.take_photo_sv);
		takePhotoIv = (ImageView) findViewById(R.id.take_photo_iv);
	}

	/**
	 * 设置相机图像预览surfaceView
	 */
	private void setTakePhotoSv() {
		takePhotoSv.setOnSurfaceStateChangeListener(
				new CameraSurfaceView.OnSurfaceStateChangeListener() {
					@Override
					public void surfaceCreated(SurfaceHolder holder) {
						LogUtils.i(TAG, "--surfaceCreated--");
						// surfaceView创建的时候打开相机
						CameraManager.getInstance(getActivity()).openDriver();
						// 开始预览
						CameraManager.getInstance(getActivity()).startPreview(takePhotoSv.getHolder());
					}

					@Override
					public void surfaceChanged(SurfaceHolder holder, int format,
					                           int width, int height) {
						LogUtils.i(TAG, "--surfaceChanged--");
					}

					@Override
					public void surfaceDestroyed(SurfaceHolder holder) {
						LogUtils.i(TAG, "--surfaceDestroyed--");
						// surfaceView销毁的时候停止预览并释放相机资源
						CameraManager.getInstance(getActivity()).stopPreview();
						CameraManager.getInstance(getActivity()).closeDriver();
					}
				});
	}

	/**
	 * 设置快门按钮
	 */
	private void setTakePhotoIv() {
		takePhotoIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePhoto();
			}
		});

	}

	/**
	 * 自动对焦并拍照
	 */
	private void takePhoto() {
		Camera camera = CameraManager.getInstance(this).getCamera();
		camera.getParameters().setPictureFormat(PixelFormat.JPEG);
		camera.autoFocus(new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera c) {
				LogUtils.i(TAG, "--对焦成功--");
				// 对焦成功后拍照
				if (success) {
					CameraManager.getInstance(getActivity())
							.takePicture(getPhotoPath(), getPhotoFileName(),
									new CameraManager.TakePictureCallback() {
										@Override
										public void onPictureSaved(String path) {
											LogUtils.i(TAG, "拍照成功，照片路径-->" + path);
											setPigPhotoPath(path);
											//TODO:弹出上传图片对话框
											showPigPictureUploadDialog();
										}
									});
				}
			}
		});
	}

	private String getPhotoPath() {
		File photoDir = FileUtils.getDiskCacheDir(getActivity(), PIG_DISTANCE_PHOTO_PATH);
		LogUtils.i(TAG, "--猪距离照片的磁盘路径->" + photoDir.getPath());
		return photoDir.getPath();
	}

	private String getPhotoFileName(){
		// 以当前时间作为文件名
		String currentTimeStr = TimeUtils.formatTimeStamp(new Date().getTime(),
				TimeUtils.Template.YMD_HMS);
		String fileName = currentTimeStr + "." + FileUtils.ExtensionName.JPG;
		LogUtils.i(TAG, "--猪照片文件名-->" + fileName);
		return currentTimeStr + "." + FileUtils.ExtensionName.JPG;
	}

	/**
	 * 设置照片上传数据
	 */
	//TODO:拍摄距离和角度需要调用传感器来计算并设置
	private void setPigPhotoData(float distance, float angle) {
		pigPhotoData.distance = distance;
		pigPhotoData.verticalAngle = angle;
	}

	private void setPigPhotoPath(String path) {
		pigPhotoData.pigPhoto = path;
	}

	/**
	 * 弹出拍照成功提示对话框
	 */
	private void showPigPictureUploadDialog() {
		PigPictureUploadDialog dialog = PigPictureUploadDialog.newInstance(true,
				pigPhotoData, onClickPigPhotoUploadListener);
		Fragment fragment = getFragmentManager().findFragmentByTag(TAG_DIALOG_UPLOAD_PIG_PHOTO);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragment != null)
			ft.remove(fragment);
		ft.addToBackStack(null);
		dialog.show(ft, TAG_DIALOG_UPLOAD_PIG_PHOTO);
	}

	// 添加猪成功对话框按钮点击监听
	private OnClickDialogBtnListener<Void> onClickPigPhotoUploadListener =
			new OnClickDialogBtnListener<Void>() {
				@Override
				public void onClickEnsure(DialogFragment df, Void aVoid) {
					LogUtils.i(TAG, "上传照片");
					df.dismiss();
					requestUploadPigPhoto();
				}

				@Override
				public void onClickCancel(DialogFragment df) {
					LogUtils.i(TAG, "不上传");
					df.dismiss();
				}
			};

	/**
	 * 添加猪圈
	 */
	private void requestUploadPigPhoto() {
		LogUtils.i(TAG, "--上传猪圈照片请求--");
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
								ToastUtils.getInstance().showToast(getActivity(),
										R.string.prompt_uploading_success);
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
								ToastUtils.getInstance().showToast(getActivity(),
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
								ToastUtils.getInstance().showToast(getActivity(),
										R.string.prompt_uploading_failure);
							}
						}, 1000);
					}

				});

	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtils.i(TAG, "--onStop--");
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtils.i(TAG, "--onDestroy--");
	}
}
