/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ai2020lab.aimedia.CameraManager;
import com.ai2020lab.aimedia.CameraSurfaceView;
import com.ai2020lab.aimedia.interfaces.PhotoSavedListener;
import com.ai2020lab.aimedia.interfaces.PhotoTakenCallback;
import com.ai2020lab.aimedia.model.FlashMode;
import com.ai2020lab.aimedia.utils.SavingPhotoTask;
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.common.TimeUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiutils.storage.FileUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.CommonUtils;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.fragment.OnClickDialogBtnListener;
import com.ai2020lab.pigadopted.fragment.PigPictureUploadDialog;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.model.pig.PigPhotoUploadRequest;
import com.ai2020lab.pigadopted.model.pig.PigPhotoUploadResponse;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;
import com.ai2020lab.pigadopted.view.GSensitiveView;

import java.io.File;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * 定距离拍照界面
 * Created by Justin Z on 2016/3/28.
 * 502953057@qq.com
 */
public class DistanceCameraActivity extends AIBaseActivity implements SensorEventListener {

	private final static String TAG = DistanceCameraActivity.class.getSimpleName();
	public final static String PIG_DISTANCE_PHOTO_PATH = "pig_distance_photo";
	private final static int HEIGHT_BASE = 100;
	/**
	 * 上传猪照片对话框TAG
	 */
	private final static String TAG_DIALOG_UPLOAD_PIG_PHOTO = "tag_dialog_UPLOAD_PIG_PHOTO";

	private PigInfo pigInfo;
	private PigPhotoUploadRequest pigPhotoData;

	/**
	 * 拍照预览容器
	 */
	private RelativeLayout takePhotoPreviewRl;
	/**
	 * 底部控制栏容器
	 */
	private RelativeLayout getTakePhotoBottomRl;
	/**
	 * 底部控制栏View
	 */
	private View controlPanel;
	/**
	 * 传感器计算展示View
	 */
	private View infoView;
	/**
	 * 拍照预览SurfaceView
	 */
	private CameraSurfaceView takePhotoSv;
	/**
	 * 拍照按钮
	 */
	private ImageView takePhotoIv;
	/**
	 * 闪光灯模式切换按钮
	 */
	private ImageView takePhotoFlashIv;
	/**
	 * 显示拍照垂直角度
	 */
	private TextView takePhotoAngleTv;
	/**
	 * 显示拍照距离
	 */
	private TextView takePhotoDistanceTv;
	/**
	 * 显示拍照高度
	 */
	private TextView takePhotoHeightTv;
	/**
	 * 水平仪
	 */
	private GSensitiveView takePhotoGsv;
	/**
	 * 调整拍照高度
	 */
	private SeekBar takePhotoHeightSb;
	/**************************************************************************/
	/**
	 * 地磁场传感器数据
	 */
	private final float[] magneticFieldData = new float[3];
	/**
	 * 加速度传感器数据
	 */
	private final float[] accelerationData = new float[3];

	/**
	 * 传感器管理类
	 */
	private SensorManager sensorManager;
	/**
	 * 加速度传感器
	 */
	private Sensor accSensor;
	/**
	 * 地磁场传感器
	 */
	private Sensor magSensor;
	/**
	 * 当前旋转角度
	 */
	private double currentRotationValue;
	/**
	 * 当前拍照高度
	 */
	private float currentHeight;
	/**
	 * 屏幕宽度
	 */
	private int screenWidth;
	/**
	 * 屏幕高度
	 */
	private int screenHeight;
	/**
	 * 预览高度
	 */
	private int previewHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance_camera);
		initData();
		setSensor();
		setToolbar();
		initViews();
		setLayoutParams();
		addViews();
		setTakePhotoSv();
		setTakePhotoHeightSb();
	}

	private void initData() {
		pigInfo = (PigInfo) getIntent().getSerializableExtra(IntentExtra.PIG_INFO);
		pigPhotoData = new PigPhotoUploadRequest();
		if (pigInfo != null)
			pigPhotoData.pigID = pigInfo.pigID;
	}

	/**
	 * 设置传感器
	 */
	private void setSensor() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accSensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		magSensor = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
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
	 * 初始化各个View
	 */
	private void initViews() {
		// 拍照预览区域
		takePhotoPreviewRl = (RelativeLayout) findViewById(R.id.take_photo_preview_rl);
		// 拍照底部控制区域
		getTakePhotoBottomRl = (RelativeLayout) findViewById(R.id.take_photo_bottom_rl);
		// 控制栏 拍照按钮，闪光灯调节
		controlPanel = ViewUtils.makeView(this, R.layout.content_distance_camera_control_panel);
		takePhotoIv = (ImageView) controlPanel.findViewById(R.id.take_photo_take_iv);
		takePhotoFlashIv = (ImageView) controlPanel.findViewById(R.id.take_photo_flash_iv);
		// 预览surfaceView
		takePhotoSv = (CameraSurfaceView) ViewUtils.makeView(this,
				R.layout.content_distance_camera_preview);
		// 传感器计算展示信息
		infoView = ViewUtils.makeView(this, R.layout.content_distance_camera_info);
		takePhotoAngleTv = (TextView) infoView.findViewById(R.id.take_photo_angle_tv);
		takePhotoDistanceTv = (TextView) infoView.findViewById(R.id.take_photo_distance_tv);
		takePhotoHeightTv = (TextView) infoView.findViewById(R.id.take_photo_height_tv);
		// 水平仪
		takePhotoGsv = (GSensitiveView) ViewUtils.makeView(this,
				R.layout.content_distance_camera_gradienter);
		// 高度调节Seekbar
		takePhotoHeightSb = (SeekBar) ViewUtils.makeView(this, R.layout
				.content_distance_camera_height_seekbar);
	}

	/**
	 * 设置view布局参数
	 */
	private void setLayoutParams() {
		screenWidth = DisplayUtils.getScreenWidth(this);
		screenHeight = DisplayUtils.getScreenHeight(this);
		int statusBarHeight = CommonUtils.getPixelSizeByName(this, "status_bar_height");
		int toolbarHeight = CommonUtils.getPixelSizeByName(this, "navigation_bar_height");
		LogUtils.i(TAG, "屏幕高度-->" + screenHeight);
		LogUtils.i(TAG, "屏幕宽度-->" + screenWidth);
		LogUtils.i(TAG, "状态栏高度-->" + statusBarHeight);
		LogUtils.i(TAG, "工具栏高度-->" + toolbarHeight);
		// 按屏幕4:3的比例计算相机预览区域高度
		previewHeight = screenWidth * 4 / 3;
		LogUtils.i(TAG, "计算得到的预览区域高度为-->" + previewHeight);
		int bottomHeight = screenHeight - previewHeight - statusBarHeight - toolbarHeight;
		LogUtils.i(TAG, "除去状态栏和工具栏之后剩下的高度为-->" + bottomHeight);
		// 设置预览区域的高度
		RelativeLayout.LayoutParams preViewLp = (RelativeLayout.LayoutParams)
				takePhotoPreviewRl.getLayoutParams();
		preViewLp.height = previewHeight;
		takePhotoPreviewRl.setLayoutParams(preViewLp);
		// 设置底部控制区域高度
		RelativeLayout.LayoutParams bottomLp = (RelativeLayout.LayoutParams)
				getTakePhotoBottomRl.getLayoutParams();
		bottomLp.height = bottomHeight;
		getTakePhotoBottomRl.setLayoutParams(bottomLp);

	}

	/**
	 * 加入各个view
	 */
	private void addViews() {
		// 加入底部拍照，闪光灯切换按钮
		getTakePhotoBottomRl.addView(controlPanel, RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		// 加入预览区域
		takePhotoPreviewRl.addView(takePhotoSv, RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		// 加入传感器采集信息展示View
		RelativeLayout.LayoutParams infoViewLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		infoViewLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		infoViewLp.topMargin = 20;
		takePhotoPreviewRl.addView(infoView, infoViewLp);
		// 加入水平仪
		RelativeLayout.LayoutParams takePhotoGsvLp = new RelativeLayout.LayoutParams(
				300, 300);
		takePhotoGsvLp.addRule(RelativeLayout.CENTER_IN_PARENT);
		takePhotoPreviewRl.addView(takePhotoGsv, takePhotoGsvLp);
		// 加入高度调节seekbar
		RelativeLayout.LayoutParams takePhotoHeightSbLp = new RelativeLayout.LayoutParams(
				previewHeight * 2 / 3, RelativeLayout.LayoutParams.WRAP_CONTENT);
		takePhotoHeightSbLp.leftMargin = (screenWidth / 2 - 150) * -1;
		takePhotoHeightSbLp.addRule(RelativeLayout.CENTER_VERTICAL);
		takePhotoPreviewRl.addView(takePhotoHeightSb, takePhotoHeightSbLp);
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
						int result = CameraManager.getInstance().openDriver(getActivity(), false);
						if (result != -1) {
							// 开始预览
							CameraManager.getInstance().startPreview(takePhotoSv.getHolder());
							// 打开摄像头后再设置拍照和闪光灯按钮
							setTakePhotoIv();
							setTakePhotoFlashIv();
						} else {
							ToastUtils.getInstance().showToast(getActivity(),
									R.string.prompt_open_camera_failure);
						}
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
						CameraManager.getInstance().stopPreview();
						CameraManager.getInstance().closeDriver();
					}
				});
	}

	/**
	 * 设置闪光灯切换按钮
	 */
	private void setTakePhotoFlashIv() {
		boolean isSupportFlash = CameraManager.getInstance().isSupportedFlash();
		if (!isSupportFlash) {
			LogUtils.i(TAG, "-->隐藏闪光灯切换按钮");
			takePhotoFlashIv.setVisibility(View.GONE);
		} else {
			LogUtils.i(TAG, "-->显示闪光灯切换按钮");
			takePhotoFlashIv.setVisibility(View.VISIBLE);
			// 获取当前的闪光灯模式
			FlashMode flashMode = CameraManager.getInstance().getFlashMode();
			LogUtils.i(TAG, "当前闪光灯模式-->" + flashMode.toString());
			setFlashModeImage(flashMode);
			takePhotoFlashIv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switchFlashMode();
				}
			});
		}
	}

	/**
	 * 切换闪光灯模式
	 */
	private void switchFlashMode() {
		FlashMode flashMode = CameraManager.getInstance().getFlashMode();
		switch (flashMode) {
			case AUTO:
				flashMode = FlashMode.ON;
				break;
			case ON:
				flashMode = FlashMode.OFF;
				break;
			case OFF:
				flashMode = FlashMode.AUTO;
				break;
		}
		CameraManager.getInstance().setFlashMode(flashMode);
		setFlashModeImage(flashMode);
	}

	/**
	 * 获取不同模式下闪光灯按钮的Drawable
	 */
	private void setFlashModeImage(FlashMode flashMode) {
		Drawable drawable;
		switch (flashMode) {
			case AUTO:
				drawable = ResourcesUtils.getDrawable(R.mipmap.flash_auto);
				break;
			case OFF:
				drawable = ResourcesUtils.getDrawable(R.mipmap.flash_off);
				break;
			case ON:
				drawable = ResourcesUtils.getDrawable(R.mipmap.flash_on);
				break;
			default:
				drawable = ResourcesUtils.getDrawable(R.mipmap.flash_auto);
		}
		takePhotoFlashIv.setImageDrawable(drawable);
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
	 * 设置拍摄高度调节SeekBar
	 */
	private void setTakePhotoHeightSb() {
		// 设置拍摄高度初始值
		currentHeight = 1.6f;
		String heightShow = String.format(getString(R.string.pig_photo_height_value),
				currentHeight);
		takePhotoHeightTv.setText(heightShow);
		takePhotoHeightSb.setMax(100);
		takePhotoHeightSb.setProgress(parseToProgress(currentHeight));
		takePhotoHeightSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				currentHeight = parseToMeter(progress);
				LogUtils.i(TAG, "当前拍摄高度-->" + currentHeight);
				String heightShow = String.format(getString(R.string.pig_photo_height_value),
						currentHeight);
				takePhotoHeightTv.setText(heightShow);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	private float parseToMeter(int progress) {
		LogUtils.i(TAG, "当前进度-->" + progress);
		float meter = CommonUtils.roundFloat((float) (progress + HEIGHT_BASE) / 100f, 2);
		LogUtils.i(TAG, "转换为米数-->" + meter);
		return meter;
	}

	private int parseToProgress(float meter) {
		LogUtils.i(TAG, "当前米数-->" + meter);
		int progress = (int) (meter * 100) - HEIGHT_BASE;
		LogUtils.i(TAG, "转换为进度-->" + progress);
		return progress;
	}

	/**
	 * 自动对焦并拍照
	 */
	private void takePhoto() {
		CameraManager.getInstance().takePicture(new PhotoTakenCallback() {
			@Override
			public void photoTaken(byte[] data, int orientation) {
				// 调用异步任务保存相片
				savePhoto(data, orientation);
			}
		});
	}

	/**
	 * 调用异步任务保存照片
	 */
	private void savePhoto(byte[] data, int orientation) {
		SavingPhotoTask savingPhotoTask = new SavingPhotoTask(data, getPhotoFileName(),
				getPhotoPath(), orientation, new PhotoSavedListener() {

			@Override
			public void savedBefore() {
				showLoading(getString(R.string.pig_photo_prompt_saving));
			}

			@Override
			public void savedFailure() {
				dismissLoading();
				ToastUtils.getInstance().showToast(getActivity(),
						R.string.pig_photo_prompt_saving_failure);
			}

			@Override
			public void savedSuccess(File file) {
				dismissLoading();
				LogUtils.i(TAG, "拍照成功，照片路径-->" + file.getPath());
				setPigPhotoPath(file.getPath());
				showPigPictureUploadDialog();
			}
		});
		savingPhotoTask.execute();
	}

	/**
	 * 获取猪照片文件路径，存放在应用缓存路径
	 */
	private String getPhotoPath() {
		File photoDir = FileUtils.getDiskCacheDir(getActivity(), PIG_DISTANCE_PHOTO_PATH);
		LogUtils.i(TAG, "--猪距离照片的磁盘路径->" + photoDir.getPath());
		return photoDir.getPath();
	}

	/**
	 * 获取猪照片文件名
	 */
	private String getPhotoFileName() {
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
	private void setPigPhotoData(float distance, float angle) {
		pigPhotoData.distance = distance;
		pigPhotoData.verticalAngle = angle;
	}

	/**
	 * 设置照片路径
	 */
	private void setPigPhotoPath(String path) {
		pigPhotoData.pigPhoto = path;
	}

	/**
	 * 弹出拍照成功提示对话框
	 */
	private void showPigPictureUploadDialog() {
		// 解除传感器监听
		sensorManager.unregisterListener(this);
		PigPictureUploadDialog dialog = PigPictureUploadDialog.newInstance(true,
				pigPhotoData, onClickPigPhotoUploadListener);
		Fragment fragment = getFragmentManager().findFragmentByTag(TAG_DIALOG_UPLOAD_PIG_PHOTO);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragment != null)
			ft.remove(fragment);
		ft.addToBackStack(null);
		dialog.show(ft, TAG_DIALOG_UPLOAD_PIG_PHOTO);
	}

	// 拍照成功对话框按钮监听
	private OnClickDialogBtnListener<Void> onClickPigPhotoUploadListener =
			new OnClickDialogBtnListener<Void>() {
				@Override
				public void onClickEnsure(DialogFragment df, Void aVoid) {
					LogUtils.i(TAG, "上传照片");
					df.dismiss();
					registerSensorListener();
					requestUploadPigPhoto();
				}

				@Override
				public void onClickCancel(DialogFragment df) {
					LogUtils.i(TAG, "不上传");
					df.dismiss();
					registerSensorListener();
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

	/**
	 * 注册监听
	 */
	private void registerSensorListener() {
		sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "--onResume--");
		// 进入界面前注册传感器监听
		registerSensorListener();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "--onPause--");
		// 界面回到后台解除传感器监听
		sensorManager.unregisterListener(this);
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

	/**
	 * 传感器改变监听
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			System.arraycopy(event.values, 0, accelerationData, 0, 3);
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			System.arraycopy(event.values, 0, magneticFieldData, 0, 3);
		}
		// 获取当前旋转角度，保留2位小数
		currentRotationValue = getCurrentRotationValue();
//		LogUtils.i(TAG, "角度-->" + currentRotationValue);
		double rotateAngle = CommonUtils.roundDouble(currentRotationValue, 1);
		String rotateAngleShow = String.format(getString(R.string.pig_photo_angle_value), rotateAngle);
		// 设置显示当前角度
		takePhotoAngleTv.setText(rotateAngleShow);
		// 根据当前拍摄高度计算拍摄距离
		double distance = calculateDistance(currentHeight);
		String distanceShow = String.format(getString(R.string.pig_photo_distance_value), distance);
		takePhotoDistanceTv.setText(distanceShow);
		setPigPhotoData((float) distance, (float) rotateAngle);

	}

	/**
	 * 传感器监听
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	/**
	 * 计算夹角
	 */
	private double getCurrentRotationValue() {
		float[] rotationMatrix = new float[16];
		if (SensorManager.getRotationMatrix(rotationMatrix, null,
				accelerationData, magneticFieldData)) {
			float[] orientation = new float[3];
			SensorManager.getOrientation(rotationMatrix, orientation);
			/**orientationValue
			 * 1 = portrait
			 * 2 = landscape
			 **/
			int orientationValue = 1;
			double tilt = Math.toDegrees(orientation[orientationValue]);
			// the azimuth becomes negative when the tilt is > 90
			if (orientation[0] < 0) {
				tilt = -90d - (90d + tilt);
			}
			return Math.abs(tilt);
		}
		return 0;
	}

	/**
	 * 根据角度和拍摄高度计算拍摄距离
	 */
	private double calculateDistance(double height) {
		// 有时候tan为负数??
		// tan函数要先转换为弧度
		double radians = Math.toRadians(currentRotationValue);
		double tan = Math.tan(radians);
//		LogUtils.i(TAG, "正切值-->" + tan);
		double distance = Math.abs(CommonUtils.roundDouble(height * tan, 2));
//		LogUtils.i(TAG, "拍摄距离-->" + distance);
		return distance;
	}

	/**
	 * 计算屏幕的水平转动弧度
	 */
	private double getCurrentUIRotation(float[] values) {
		float ax = values[0];
		float ay = values[2];

		double g = Math.sqrt(ax * ax + ay * ay);
		double cos = ay / g;
		if (cos > 1) {
			cos = 1;
		} else if (cos < -1) {
			cos = -1;
		}
		double rad = Math.acos(cos);
		if (ax < 0) {
			rad = 2 * Math.PI - rad;
		}

		int uiRot = getWindowManager().getDefaultDisplay().getRotation();
		double uiRad = Math.PI / 2 * uiRot;
		rad -= uiRad;
		LogUtils.i(TAG, "计算得到旋转的弧度-->" + rad);
		return rad;
	}


}
