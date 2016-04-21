/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 展示照片大图界面
 * Created by Justin Z on 2016/4/21.
 * 502953057@qq.com
 */
public class PhotoDetailActivity extends AIBaseActivity {

	private final static String TAG = PhotoDetailActivity.class.getSimpleName();

	private ImageView photoDetailIv;

	private String photoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setToolbar();
		assignViews();
		setPhotoIv();
	}

	private void init() {
		photoPath = getIntent().getStringExtra(IntentExtra.PHOTO_PATH);
		setContentView(R.layout.activity_photo_detail);
	}

	private void setToolbar() {
		supportToolbar(true);
		setToolbarLeft(R.drawable.toolbar_back_selector);
		setOnLeftClickListener(new OnLeftClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});
	}

	private void assignViews() {
		photoDetailIv = (ImageView) findViewById(R.id.photo_detail_iv);
	}


	private void setPhotoIv() {
		ImageLoader.getInstance().displayImage("file://" + photoPath, photoDetailIv);
	}


}
