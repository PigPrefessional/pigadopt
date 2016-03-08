package com.ai2020lab.pigadopted.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.common.SysCameraManager;
import com.ai2020lab.pigadopted.model.pig.PigPhotoUploadResponse;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;
import com.nostra13.universalimageloader.core.ImageLoader;

import cz.msebera.android.httpclient.Header;

public class PigPhotoActivity extends AppCompatActivity {

	private final static String TAG = PigPhotoActivity.class.getSimpleName();

	private Activity activity;

	private ImageView pigPhotoView;

	private String photoPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.activity_pig_photo);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		initPigPhotoImageView();
		initUploadPhotoButton();

	}

	// 初始化显示拍照返回照片的ImageView
	private void initPigPhotoImageView() {
		pigPhotoView = (ImageView) findViewById(R.id.pig_photo_view);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) pigPhotoView.getLayoutParams();
		lp.height = DisplayUtils.getScreenHeight(this) * 3 / 4;
		pigPhotoView.setLayoutParams(lp);
	}

	// 初始化上传按钮
	private void initUploadPhotoButton() {
		findViewById(R.id.pig_photo_upload_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				uploadPigPhoto(view);
			}
		});
	}

	// 上传照片
	private void uploadPigPhoto(View view) {
		final View v = view;
		if (TextUtils.isEmpty(photoPath)) {
			LogUtils.i(TAG, "还没有拍照");
			Snackbar.make(v, getString(R.string.pig_photo_not_take_pic), Snackbar.LENGTH_LONG)
					.setAction("Action", null).show();
			return;
		}
		HttpManager.postPhoto(this, UrlName.PIG_PHOTO_UPLOAD.getUrl(), photoPath,
				new JsonHttpResponseHandler<PigPhotoUploadResponse>(this) {

					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            PigPhotoUploadResponse jsonObj) {
						Snackbar.make(v, "返回图片地址:" + jsonObj.data.pigPhoto,
								Snackbar.LENGTH_LONG).setAction("Action", null).show();
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pig_photo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			// 调用系统相机拍照
			case R.id.action_take_photo:
				SysCameraManager.getInstance().openCamera(this);
				break;
			// 调用系统相册进行选择
			case R.id.action_photo_album:

				break;
		}

		return super.onOptionsItemSelected(item);
	}

	// 处理照片返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == SysCameraManager.REQUEST_CODE_OPEN_CAMERA) {
			SysCameraManager.getInstance().savePhoto();
			// 绑定照片保存监听
			SysCameraManager.getInstance().setOnPhotoSavedListener(
					new SysCameraManager.OnPhotoSavedListener() {
						@Override
						public void onPhotoSaved(String path) {
							LogUtils.i(TAG, "拍照成功");
							LogUtils.i(TAG, "照片路径-->" + path);
							photoPath = path;
							StringBuilder uri = new StringBuilder();
							uri.append("file://").append(path);
							ImageLoader.getInstance().displayImage(uri.toString(), pigPhotoView);
						}
					});
		}
	}
}
