/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ai2020lab.pigadopted.common.Constants;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.model.pig.PigPhotoUploadRequest;
import com.ai2020lab.pigadopted.view.CropperView;

import java.io.File;
import java.util.Date;

/**
 * 定距离拍照界面
 * Created by Justin Z on 2016/3/28.
 * 502953057@qq.com
 */
public class DistanceCameraActivity extends AIBaseActivity implements SensorEventListener {

	private final static String TAG = DistanceCameraActivity.class.getSimpleName();

	public final static String PIG_DISTANCE_PHOTO_PATH = "pig_distance_photo";
	private final static int HEIGHT_BASE = 100;

	private final static float TAKE_PHOTO_HEIGHT_DEFAULT = 1.55f;

	/**
	 * 拍摄猪照片上传数据
	 */
	private PigPhotoUploadRequest pigPhotoData;

	/**
	 * 截图Rect对象
	 */
	private Rect cropperRect;

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
	 * 照片取景，裁剪拖动框
	 */
	private CropperView takePhotoCv;
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
	 * 预览宽度
	 */
	private int previewWidth;
	/**
	 * 预览高度
	 */
	private int previewHeight;
	/**
	 * 是否能拍照标志位
	 */
	private boolean canTakePhoto = false;
	/**
	 * 是否能设置拍照数据
	 */
	private boolean canSetData = true;


	/**
	 * 程序入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_distance_camera);
		setToolbar();
		// 检查权限
		checkCameraPermission();
	}

	/**
	 * 检查摄像头权限，android 6.0需要
	 */
	private void checkCameraPermission() {
		// 没有授权的情况下询问用户是否授权
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
				PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.CAMERA)) {
				LogUtils.i(TAG, "--向用户解释相机权限的作用--");
				// 向用户解释需要该权限的原因
				ToastUtils.getInstance().showToast(this, R.string.prompt_camera_permission,
						Toast.LENGTH_LONG);
			} else {
				// 请求相机权限
				LogUtils.i(TAG, "--请求相机权限--");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
						Constants.PERMISSION_CAMERA_REQUEST_CODE);
			}
		} else {
			LogUtils.i(TAG, "--已经授权相机，直接初始化拍照界面--");
			init();
		}
	}

	/**
	 * 授权操作回调
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		switch (requestCode) {
			case Constants.PERMISSION_CAMERA_REQUEST_CODE:
				LogUtils.i(TAG, "--请求相机权限回调--");
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					LogUtils.i(TAG, "--请求相机权限回调中授权成功--");
					init();
				} else {
					LogUtils.i(TAG, "--请求相机权限回调中授权失败");
					// 请求相机权限
					ToastUtils.getInstance().showToast(this, R.string.prompt_get_permission,
							Toast.LENGTH_LONG);
					ThreadUtils.runOnUIThread(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					}, 3000);
				}
				break;
		}
	}

	private void init() {
		setSensor();
		initViews();
		setLayoutParams();
		addViews();
		setTakePhotoSv();
		setTakePhotoHeightSb();
	}

	private void initData() {
		PigInfo pigInfo = (PigInfo) getIntent().getSerializableExtra(IntentExtra.PIG_INFO);
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
		takePhotoPreviewRl = (RelativeLayout) findViewById(R.id.take_photo_preview_fl);
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
		// 照片取景，裁剪拖动框
		takePhotoCv = (CropperView) ViewUtils.makeView(this, R.layout.content_distance_camera_crop);
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
		previewWidth = screenWidth;
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
		infoViewLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		infoViewLp.bottomMargin = 20;
		takePhotoPreviewRl.addView(infoView, infoViewLp);
		// 加入高度调节seekbar
		RelativeLayout.LayoutParams takePhotoHeightSbLp = new RelativeLayout.LayoutParams(
				previewHeight * 2 / 3, RelativeLayout.LayoutParams.WRAP_CONTENT);
		takePhotoHeightSbLp.leftMargin = (screenWidth / 2 - 150) * -1;
		takePhotoHeightSbLp.addRule(RelativeLayout.CENTER_VERTICAL);
		takePhotoPreviewRl.addView(takePhotoHeightSb, takePhotoHeightSbLp);
		// 加入取景，裁剪拖动框
		takePhotoPreviewRl.addView(takePhotoCv);
		takePhotoCv.setLayoutParams(new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
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
		currentHeight = TAKE_PHOTO_HEIGHT_DEFAULT;
		pigPhotoData.height = currentHeight;
		String heightShow = String.format(getString(R.string.pig_photo_height_value),
				currentHeight);
		takePhotoHeightTv.setText(heightShow);
		takePhotoHeightSb.setMax(100);
		takePhotoHeightSb.setProgress(parseToProgress(currentHeight));
		takePhotoHeightSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				currentHeight = parseToMeter(progress);
				String heightShow = String.format(getString(R.string.pig_photo_height_value),
						currentHeight);
				takePhotoHeightTv.setText(heightShow);
				pigPhotoData.height = currentHeight;
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
		if (!canTakePhoto) {
			ToastUtils.getInstance().showToast(this, R.string.prompt_turn_phone_azimuth);
			return;
		}
		canSetData = false;
		CameraManager.getInstance().takePicture(new PhotoTakenCallback() {
			@Override
			public void photoTakenSuccess(byte[] data, int orientation) {
				// 调用异步任务保存相片
				savePhoto(data, orientation);
			}

			@Override
			public void photoTakenFailure() {
				canSetData = true;
			}
		});
	}

	/**
	 * 调用异步任务保存照片
	 */
	private void savePhoto(byte[] data, int orientation) {
		// 生成照片截取数据
		if (!setPigPhotoCropData()) {
			ToastUtils.getInstance().showToast(this, R.string.pig_photo_prompt_saving_failure);
			canSetData = true;
			return;
		}
		SavingPhotoTask savingPhotoTask = new SavingPhotoTask(getActivity(), data,
				getPhotoFileName(), getPhotoPath(),
				orientation, true,
				cropperRect, new PhotoSavedListener() {

			@Override
			public void savedBefore() {
				showLoading(getString(R.string.pig_photo_prompt_saving));
			}

			@Override
			public void savedFailure() {
				dismissLoading();
				ToastUtils.getInstance().showToast(getActivity(),
						R.string.pig_photo_prompt_saving_failure);
				canSetData = true;
			}

			@Override
			public void savedSuccess(File photo, File cropperPhoto) {
				dismissLoading();
				LogUtils.i(TAG, "拍照成功，照片路径-->" + photo.getPath());
				String cropPath = null;
				if (cropperPhoto != null && FileUtils.isExist(cropperPhoto.getPath())) {
					cropPath = cropperPhoto.getPath();
				}
				LogUtils.i(TAG, "拍照成功，照片截图路径-->" + cropPath);
				setPigPhotoPath(photo.getPath(), cropPath);
//				showPigPictureUploadDialog();
				// 跳转到猪照片上传界面
				skipToUploadPigPhotoActivity();
				canSetData = true;
			}
		});
		savingPhotoTask.execute();
	}

	/**
	 * 获取猪照片文件路径，存放在应用缓存路径
	 */
	private String getPhotoPath() {
		File photoDir = FileUtils.getDiskCacheDir(getActivity(), PIG_DISTANCE_PHOTO_PATH);
		return photoDir.getPath();
	}

	/**
	 * 获取猪照片文件名
	 */
	private String getPhotoFileName() {
		// 以当前时间作为文件名
		String currentTimeStr = TimeUtils.formatTimeStamp(new Date().getTime(),
				TimeUtils.Template.YMD_HMS);
		return currentTimeStr + "." + FileUtils.ExtensionName.JPG;
	}

	/**
	 * 设置照片上传数据
	 */
	private void setPigPhotoData(double distance, double angle) {
		pigPhotoData.distance = distance;
		pigPhotoData.verticalAngle = angle;
	}

	/**
	 * 设置照片剪切数据
	 */
	private boolean setPigPhotoCropData() {
		Rect rect = takePhotoCv.getCropperRect();
		Camera.Size pictureSize = CameraManager.getInstance().getPictureSize();
		if (pictureSize == null) {
			return false;
		}
		float ratio = CameraManager.getInstance().getPicturePreviewRatio();
		if (ratio == -1) {
			return false;
		}
		// 这里注意照片的分辨率宽高要交换一下位置
		int pictureWidth = pictureSize.width;
		int pictureHeight = pictureSize.height;
		if (pictureWidth > pictureHeight) {
			pictureWidth = pictureSize.height;
			pictureHeight = pictureSize.width;
		}
		LogUtils.i(TAG, "picture size width-->" + pictureWidth);
		LogUtils.i(TAG, "picture size height-->" + pictureHeight);
		pigPhotoData.objectLeft = CommonUtils.roundInt(rect.left * ratio);
		pigPhotoData.objectTop = CommonUtils.roundInt(rect.top * ratio);
		pigPhotoData.objectRight = CommonUtils.roundInt(rect.right * ratio);
		pigPhotoData.objectDown = CommonUtils.roundInt(rect.bottom * ratio);
		pigPhotoData.picWidth = pictureWidth;
		pigPhotoData.picHeight = pictureHeight;
		// 得到照片截取矩形
		cropperRect = new Rect(pigPhotoData.objectLeft, pigPhotoData.objectTop,
				pigPhotoData.objectRight, pigPhotoData.objectDown);
		return true;
	}

	/**
	 * 设置照片路径
	 */
	private void setPigPhotoPath(String path, String cropPath) {
		pigPhotoData.pigPhoto = path;
		pigPhotoData.pigPhotoCrop = cropPath;
	}

	/**
	 * 跳转到猪照片上传界面
	 */
	private void skipToUploadPigPhotoActivity() {
		Intent intent = new Intent(this, UploadPigPhotoActivity.class);
		intent.putExtra(IntentExtra.PIG_PHOTO_UPLOAD, pigPhotoData);
		startActivity(intent);
	}

	/**
	 * 注册监听
	 */
	private void registerSensorListener() {
		if (sensorManager != null) {
			sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	private void unRegisterSensorListener() {
		// 界面回到后台解除传感器监听
		if (sensorManager != null)
			sensorManager.unregisterListener(this);
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
		unRegisterSensorListener();
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
		if (currentRotationValue == 0d) canTakePhoto = false;
		else canTakePhoto = true;
//		LogUtils.i(TAG, "角度-->" + currentRotationValue);
		double rotateAngle = CommonUtils.roundDouble(90d - currentRotationValue, 1);
		String rotateAngleShow = String.format(getString(R.string.pig_photo_angle_value), rotateAngle);
		// 设置显示当前角度
		if (takePhotoAngleTv != null && canSetData)
			takePhotoAngleTv.setText(rotateAngleShow);
		// 根据当前拍摄高度计算拍摄距离
		double distance = calculateDistance(currentHeight);
		String distanceShow = String.format(getString(R.string.pig_photo_distance_value), distance);
		if (takePhotoDistanceTv != null && canSetData)
			takePhotoDistanceTv.setText(distanceShow);
		// 设置上传数据
		if (canSetData)
			setPigPhotoData(distance, rotateAngle);

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
//			int orientationValue = 1;
			double tilt = Math.toDegrees(orientation[1]);
//			LogUtils.i(TAG, "手机同水平方向的夹角弧度-->" + orientation[1]);
//			LogUtils.i(TAG, "手机同水平方向的夹角是-->" + tilt);
			tilt = Math.abs(tilt);
			if (tilt > 90d) {
				tilt = 0d;
			}
			return tilt;
//			// the azimuth becomes negative when the tilt is > 90
//			if (orientation[0] < 0) {
//				tilt = -90d - (90d + tilt);
//			}
//			return Math.abs(tilt);
		}
		return 0d;
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
