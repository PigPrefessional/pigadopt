package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;

/**
 * 卖家主页
 * Created by Justin on 2016/3/4.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class SellerMainActivity extends AIBaseActivity {
	/**
	 * 日志标题
	 */
	private final static String TAG = SellerMainActivity.class.getSimpleName();
	/**
	 * 猪圈屋顶布局
	 */
	private RelativeLayout hogpenRoofLayout;
	/**
	 * 猪圈圈身布局
	 */
	private RelativeLayout hogpenBodyLayout;

	/**
	 * 程序入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();

//		test();
	}


	private void init() {
		// 不展示工具栏
		supportToolbar(false);
		setContentView(R.layout.activity_seller_main);
		hogpenRoofLayout = (RelativeLayout) findViewById(R.id.hogpen_roof_layout);
		hogpenBodyLayout = (RelativeLayout) findViewById(R.id.hogpen_body_layout);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) hogpenBodyLayout.getLayoutParams();
		lp.height = (int) (DisplayUtils.getScreenHeight(this) * 0.6);
		hogpenBodyLayout.setLayoutParams(lp);
	}


}
