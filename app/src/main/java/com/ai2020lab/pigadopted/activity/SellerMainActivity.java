package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.model.hogpen.SellerHogpenInfo;
import com.ai2020lab.pigadopted.view.BirdIndicator;
import com.ai2020lab.pigadopted.view.HogpenViewPager;

import java.util.ArrayList;
import java.util.List;

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
	 * 猪圈ViewPager
	 */
	private HogpenViewPager hogpenViewPager;
	/**
	 * 卖家信息TextView
	 */
	private TextView tvSellerInfo;
	/**
	 * 卖家猪圈列表数据
	 */
	private List<SellerHogpenInfo> sellerHogpenInfos;

	/**
	 * 程序入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();

	}


	private void init() {
		// 不展示工具栏
		supportToolbar(false);
		setContentView(R.layout.activity_seller_main);
		hogpenViewPager = (HogpenViewPager) findViewById(R.id.hogpen_viewPager);
		birdIndicator = (BirdIndicator) findViewById(R.id.hogpen_indicator);
		initHogpenViewPager();
		initBirdIndicator();
		// 载入测试数据
		loadTestData();


	}

	// 初始化鸟页卡指示器
	private void initBirdIndicator() {
		birdIndicator.setOnSelectListener(new BirdIndicator.OnSelectListener() {
			@Override
			public void onSelect(int index) {
				LogUtils.i(TAG, "当前选中的游标下标是:" + index);
				hogpenViewPager.setCurrentIndex(index);
			}
		});
		birdIndicator.setOnClickAddListener(new BirdIndicator.OnClickAddListener() {
			@Override
			public void onClickAdd() {
				LogUtils.i(TAG, "添加鸟和猪圈");
				addHogpen();
			}
		});
	}

	// 初始化猪圈
	private void initHogpenViewPager() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
				hogpenViewPager.getLayoutParams();
		lp.height = (int) (DisplayUtils.getScreenHeight(this) * 0.6);
		hogpenViewPager.setLayoutParams(lp);
		hogpenViewPager.setOnHogpenChangeListener(new HogpenViewPager.OnHogpenChangeListener() {
			@Override
			public void onHogpenSelected(int index) {
				// 猪圈切换，游标跟随移动
				birdIndicator.setCurrentIndex(index);
			}
		});
	}

	/**
	 * 添加鸟和猪圈
	 */
	private void addHogpen(){
		birdIndicator.addIndicator();
		hogpenViewPager.addHogpen(new SellerHogpenInfo());
		// 让游标选中最后一项
		hogpenViewPager.setCurrentIndex(sellerHogpenInfos.size());
	}

	// 加入测试数据
	private void loadTestData() {
		// TODO:这里模拟测试数据，初始为0个猪圈
		sellerHogpenInfos = new ArrayList<>();
		final int size = sellerHogpenInfos.size();
		// 初始化数据
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 添加初始鸟
				birdIndicator.setIndicators(size);
				LogUtils.i(TAG, "当前鸟个数：" + birdIndicator.getIndicatorNumber());
				// 初始化猪圈数据
				hogpenViewPager.setHogpenTabs(sellerHogpenInfos);
				// 初始化的时候选中第一个数据
				birdIndicator.setCurrentIndex(0);
			}
		}, 2000);
	}


}
