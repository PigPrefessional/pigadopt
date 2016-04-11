/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
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
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.TimeUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
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

	private CameraSurfaceView takePhotoSv;
	private ImageView takePhotoIv;
	private TextView takePhotoAngleTv;
	private TextView takePhotoDistanceTv;
	private TextView takePhotoHeightTv;
	private GSensitiveView takePhotoGsv;
	private SeekBar takePhotoHeightSb;

	private PigInfo pigInfo;
	private PigPhotoUploadRequest pigPhotoData;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance_camera);
		pigInfo = (PigInfo) getIntent().getSerializableExtra(IntentExtra.PIG_INFO);
		pigPhotoData = new PigPhotoUploadRequest();
		pigPhotoData.pigID = pigInfo.pigID;
		setSensor();
		setToolbar();
		assignViews();
		setTakePhotoSv();
		setTakePhotoIv();
		setTakePhotoHeightSb();
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
	 * 分配各个View
	 */
	private void assignViews() {
		takePhotoSv = (CameraSurfaceView) findViewById(R.id.take_photo_sv);
		takePhotoIv = (ImageView) findViewById(R.id.take_photo_iv);
		takePhotoAngleTv = (TextView) findViewById(R.id.take_photo_angle_tv);
		takePhotoDistanceTv = (TextView) findViewById(R.id.take_photo_distance_tv);
		takePhotoHeightTv = (TextView) findViewById(R.id.take_photo_height_tv);
		takePhotoGsv = (GSensitiveView) findViewById(R.id.take_photo_gsv);
		takePhotoHeightSb = (SeekBar) findViewById(R.id.take_photo_height_sb);
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
	 * 设置拍摄高度调节SeekBar
	 */
	private void setTakePhotoHeightSb() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) takePhotoHeightSb
				.getLayoutParams();
		lp.width = DisplayUtils.getScreenHeight(this) * 2 / 3;
		lp.leftMargin = (DisplayUtils.getScreenWidth(this) / 2 - 15) * -1;
		lp.bottomMargin = 80;
		takePhotoHeightSb.setLayoutParams(lp);
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
											// 弹出上传图片对话框
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

	// 添加猪成功对话框按钮点击监听
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
		LogUtils.i(TAG, "角度-->" + currentRotationValue);
		double rotateAngle = CommonUtils.roundDouble(currentRotationValue, 1);
		String rotateAngleShow = String.format(getString(R.string.pig_photo_angle_value), rotateAngle);
		// 设置显示当前角度
		takePhotoAngleTv.setText(rotateAngleShow);
		// 根据当前拍摄高度计算拍摄距离
		double distance = calculateDistance(currentHeight);
		String distanceShow = String.format(getString(R.string.pig_photo_distance_value), distance);
		takePhotoDistanceTv.setText(distanceShow);
		setPigPhotoData((float) distance, (float) rotateAngle);
//		getCurrentUIRotation(event.values);
		// 测试
//		takePhotoGsv.setRotation(getCurrentUIRotation(event.values));
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
		LogUtils.i(TAG, "正切值-->" + tan);
		double distance = Math.abs(CommonUtils.roundDouble(height * tan, 2));
		LogUtils.i(TAG, "拍摄距离-->" + distance);
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
