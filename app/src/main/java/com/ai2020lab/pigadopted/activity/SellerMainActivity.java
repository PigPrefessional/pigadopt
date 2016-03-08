package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;

import com.ai2020lab.pigadopted.base.AIBaseActivity;

/**
 * 养猪人主页
 * Created by Justin on 2016/3/4.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class SellerMainActivity extends AIBaseActivity {
	private final static String TAG = SellerMainActivity.class.getSimpleName();

	/**
	 * 程序入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();

//		test();
	}


	private void init(){
		// 不展示工具栏
		supportToolbar(false);
	}

















}
