package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.model.hogpen.SellerHogpenInfo;
import com.ai2020lab.pigadopted.model.order.OrderInfoForSeller;
import com.ai2020lab.pigadopted.model.pig.GrowthInfo;
import com.ai2020lab.pigadopted.model.pig.HealthInfo;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoForSeller;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.model.pig.PigStatus;
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
	private TextView sellerInfoTv;
	/**
	 * 添加猪按钮
	 */
	private ImageView addPigIv;


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
		initAddPigBtn();
		initSellerInfoText();
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
				LogUtils.i(TAG, "添加鸟游标和猪圈");
				//
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
		hogpenViewPager.setOnPigClickListener(new HogpenViewPager.OnPigClickListener() {
			@Override
			public void onPigClick(SellerHogpenInfo hogpenInfo, PigDetailInfoForSeller pigInfo) {
				// 猪点击监听
				// TODO:跳转到猪详情界面
				skipToPigDetailActivity(hogpenInfo, pigInfo);
			}
		});
	}


	/**
	 * 初始化添加猪按钮
	 */
	private void initAddPigBtn() {
		addPigIv = (ImageView) findViewById(R.id.add_pig_iv);
		addPigIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.i(TAG, "--添加猪--");
				addPig();
			}
		});
	}

	/**
	 * 初始化卖家信息TextView
	 */
	private void initSellerInfoText() {
		sellerInfoTv = (TextView) findViewById(R.id.seller_info_tv);
		sellerInfoTv.setText("刘司机家");
	}

	/**
	 * 跳转到猪详情界面
	 */
	private void skipToPigDetailActivity(SellerHogpenInfo hogpenInfo,
	                                     PigDetailInfoForSeller pigInfo) {
		LogUtils.i(TAG, "跳转到猪详情界面");
	}

	/**
	 * 添加鸟和猪圈
	 */
	private void addHogpen() {
		birdIndicator.addIndicator();
		hogpenViewPager.addHogpen(new SellerHogpenInfo());
		// 让游标选中最后一项
		hogpenViewPager.setCurrentIndex(birdIndicator.getIndicatorNumber() - 1);
		// TODO:为何ViewPager的第一项刚添加的时候无法响应页面切换事件??
		if (birdIndicator.getIndicatorNumber() == 1) {
			birdIndicator.setCurrentIndex(0);
		}
	}

	/**
	 * 添加猪
	 */
	private void addPig() {
		hogpenViewPager.addPig(getPigTestData());
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
				// 让游标选中最后一项
				hogpenViewPager.setCurrentIndex(size - 1 < 0 ? 0 : size - 1);
			}
		}, 2000);
	}

	/**
	 * 返回测试猪数据
	 *
	 * @return
	 */
	private PigDetailInfoForSeller getPigTestData() {
		PigDetailInfoForSeller pigInfo = new PigDetailInfoForSeller();
		pigInfo.orderInfo = new OrderInfoForSeller();
		pigInfo.orderInfo.buyerNums = 3;
		pigInfo.growthInfo = new GrowthInfo();
		pigInfo.growthInfo.pigWeight = 150;
		pigInfo.healthInfo = new HealthInfo();
		pigInfo.healthInfo.temperature = 36;
		pigInfo.healthInfo.status = PigStatus.EATING;
		pigInfo.pigInfo = new PigInfo();
		return pigInfo;
	}


}
