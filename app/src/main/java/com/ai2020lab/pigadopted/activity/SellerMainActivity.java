package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.view.BirdIndicator;

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
	 * 猪圈屋顶鸟页卡指示器
	 */
	private BirdIndicator birdIndicator;
	/**
	 * 猪圈圈身布局
	 */
	private RelativeLayout hogpenBodyLayout;

	private TextView sellerInfoText;

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
		initBirdIndicator();
		initHogpen();


	}

	// 初始化鸟页卡指示器
	private void initBirdIndicator() {
		birdIndicator = (BirdIndicator) findViewById(R.id.hogpen_indicator);
//		birdIndicator.addIndicators(1);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 添加初始鸟
				birdIndicator.setIndicators(0);
				LogUtils.i(TAG, "当前鸟个数：" + birdIndicator.getIndicatorNumber());
			}
		}, 2000);
		birdIndicator.setOnSelectListener(new BirdIndicator.OnSelectListener() {
			@Override
			public void onSelect(int index) {
				LogUtils.i(TAG, "当前选中的游标下标是:" + index);
			}
		});
		birdIndicator.setOnClickAddListener(new BirdIndicator.OnClickAddListener() {
			@Override
			public void onClickAdd() {
				LogUtils.i(TAG, "添加鸟");
				birdIndicator.addIndicator();
			}
		});
	}

	// 初始化猪圈
	private void initHogpen() {
		hogpenBodyLayout = (RelativeLayout) findViewById(R.id.hogpen_body_layout);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) hogpenBodyLayout.getLayoutParams();
		lp.height = (int) (DisplayUtils.getScreenHeight(this) * 0.6);
		hogpenBodyLayout.setLayoutParams(lp);
	}


}
