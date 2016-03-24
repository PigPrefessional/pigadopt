package com.ai2020lab.pigadopted.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.fragment.AddPigDialog;
import com.ai2020lab.pigadopted.fragment.AddPigSuccessDialog;
import com.ai2020lab.pigadopted.fragment.OnClickDialogBtnListener;
import com.ai2020lab.pigadopted.model.hogpen.SellerHogpenInfo;
import com.ai2020lab.pigadopted.model.order.OrderInfoForSeller;
import com.ai2020lab.pigadopted.model.pig.GrowthInfo;
import com.ai2020lab.pigadopted.model.pig.HealthInfo;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.model.pig.PigStatus;
import com.ai2020lab.pigadopted.model.user.UserInfo;
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
	private HogpenViewPager hogpenVp;
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
	private UserInfo userInfo;


	/**
	 * 程序入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userInfo = (UserInfo) getIntent().getExtras().get(IntentExtra.USER_INFO);
		setContentView(R.layout.activity_main_seller);
		// 不展示工具栏
		supportToolbar(false);
		// 分配各个View
		assignViews();
		initHogpenViewPager();
		initBirdIndicator();
		initAddPigBtn();
		// 载入测试数据
		loadTestData();
	}

	private void assignViews() {
		hogpenVp = (HogpenViewPager) findViewById(R.id.hogpen_viewPager);
		birdIndicator = (BirdIndicator) findViewById(R.id.hogpen_indicator);
		sellerInfoTv = (TextView) findViewById(R.id.seller_info_tv);
		addPigIv = (ImageView) findViewById(R.id.add_pig_iv);
	}

	// 初始化鸟页卡指示器
	private void initBirdIndicator() {
		birdIndicator.setOnSelectListener(new BirdIndicator.OnSelectListener() {
			@Override
			public void onSelect(int index) {
				LogUtils.i(TAG, "当前选中的游标下标是:" + index);
				hogpenVp.setCurrentIndex(index);
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
				hogpenVp.getLayoutParams();
		lp.height = (int) (DisplayUtils.getScreenHeight(this) * 0.62);
		hogpenVp.setLayoutParams(lp);
		hogpenVp.setOnHogpenChangeListener(new HogpenViewPager.OnHogpenChangeListener() {
			@Override
			public void onHogpenSelected(int index) {
				// 猪圈切换，游标跟随移动
				birdIndicator.setCurrentIndex(index);
				// 根据当前猪圈中的猪决定是否隐藏添加猪按钮
				setAddPigBtnVisibility(hogpenVp.getPigNumber(index)
						< HogpenViewPager.PIG_LIMIT);
			}
		});
		hogpenVp.setOnPigClickListener(new HogpenViewPager.OnPigClickListener() {
			@Override
			public void onPigClick(SellerHogpenInfo hogpenInfo, PigDetailInfoAndOrder pigInfo) {
				// 猪点击监听
				// TODO:跳转到猪详情界面
				skipToPigDetailActivity(hogpenInfo, pigInfo);
			}
		});
		// 猪添加动画执行完毕监听
		hogpenVp.setOnPigAddListener(new HogpenViewPager.OnPigAddListener() {
			@Override
			public void onEnd() {

				showAddPigSuccessDialog();

			}
		});
	}

	/**
	 * 初始化添加猪按钮
	 */
	private void initAddPigBtn() {
		addPigIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.i(TAG, "--添加猪--");
				// 弹出添加猪对话框
				showAddPigDialog();
			}
		});
	}

	/**
	 * 跳转到猪详情界面
	 */
	private void skipToPigDetailActivity(SellerHogpenInfo hogpenInfo,
	                                     PigDetailInfoAndOrder pigInfo) {
		LogUtils.i(TAG, "跳转到猪详情界面");
		// TODO:还需要将猪的基本数据传递过去
		Intent intent = new Intent(this, PigDetailActivity.class);
		intent.putExtra(PigDetailActivity.KEY_DETAIL_TYPE, PigDetailActivity.TYPE_SELLER);
		startActivity(intent);
	}

	/**
	 * 设置隐藏或者显示添加猪按钮
	 */
	//猪圈切换的时候监听猪圈中的猪是否达到上限，达到上限则隐藏添加猪按钮,否则显示添加猪按钮
	private void setAddPigBtnVisibility(boolean isShow) {
		Animation animIn = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
		Animation animOut = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);
		animIn.setInterpolator(new BounceInterpolator());
//		animOut.setInterpolator(new BounceInterpolator());
		if (isShow && addPigIv.getVisibility() == View.GONE) {
			addPigIv.setVisibility(View.VISIBLE);
			addPigIv.startAnimation(animIn);
		} else if (!isShow && addPigIv.getVisibility() == View.VISIBLE) {
			addPigIv.setVisibility(View.GONE);
			addPigIv.startAnimation(animOut);
		}
	}

	/**
	 * 动画显示卖家用户名
	 */
	private void loadSellerInfoAnim() {
		Animation animIn = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
		animIn.setInterpolator(new BounceInterpolator());
		// 中文字体加粗,xml中设置无效
		sellerInfoTv.getPaint().setFakeBoldText(true);
		sellerInfoTv.setText(String.format(getString(R.string.pig_seller_name), userInfo.userName));
		sellerInfoTv.setVisibility(View.VISIBLE);
		sellerInfoTv.startAnimation(animIn);
	}

	/**
	 * 弹出添加猪对话框
	 */
	private void showAddPigDialog() {
		AddPigDialog.newInstance(true, onClickDialogBtnListener)
				.show(getSupportFragmentManager(), null);
	}

	// 添加猪对话框点击监听
	private OnClickDialogBtnListener<PigInfo> onClickDialogBtnListener =
			new OnClickDialogBtnListener<PigInfo>() {
				@Override
				public void onClickEnsure(Dialog dialog, PigInfo pigInfo) {
					dialog.dismiss();
					//添加猪
					addPig();
				}

				@Override
				public void onClickCancel(Dialog dialog) {
					dialog.dismiss();
				}
			};

	/**
	 * 弹出添加猪成功提示对话框
	 */
	private void showAddPigSuccessDialog() {
		AddPigSuccessDialog addPigSuccessDialog = AddPigSuccessDialog.newInstance(true,
				new OnClickDialogBtnListener<Void>() {
					@Override
					public void onClickEnsure(Dialog dialog, Void aVoid) {
						LogUtils.i(TAG, "跳转到猪拍照界面");
					}

					@Override
					public void onClickCancel(Dialog dialog) {
						LogUtils.i(TAG, "残忍拒绝");
						dialog.dismiss();

					}
				});
		addPigSuccessDialog.show(getSupportFragmentManager(), null);
	}

	/**
	 * 添加测试鸟和猪圈
	 */
	private void addHogpen() {
		birdIndicator.addIndicator();
		hogpenVp.addHogpen(new SellerHogpenInfo());
		// 选中新添加的猪圈
		hogpenVp.setCurrentIndex(birdIndicator.getIndicatorNumber() - 1);
		// TODO:为何ViewPager的第一项刚添加的时候无法响应页面切换事件??
		if (birdIndicator.getIndicatorNumber() == 1) {
			birdIndicator.setCurrentIndex(0);
			setAddPigBtnVisibility(hogpenVp.getPigNumber()
					< HogpenViewPager.PIG_LIMIT);
		}
	}

	/**
	 * 添加测试猪
	 */
	private void addPig() {
		hogpenVp.addPig(getPigTestData());
		setAddPigBtnVisibility(hogpenVp.getPigNumber()
				< HogpenViewPager.PIG_LIMIT);
	}

	// 加入测试数据，这里模拟测试数据，初始为0个猪圈
	private void loadTestData() {
		sellerHogpenInfos = getHogpenInfos();
		final int size = sellerHogpenInfos.size();
		// 初始化数据
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 添加初始鸟
				birdIndicator.setIndicators(size);
				LogUtils.i(TAG, "当前鸟个数：" + birdIndicator.getIndicatorNumber());
				// 初始化猪圈数据
				hogpenVp.setHogpenTabs(sellerHogpenInfos);
				// 让游标选中第一项,初始化猪圈的时候，页面选择事件是无效的？
				birdIndicator.setCurrentIndex(0);
				// 判断添加猪按钮是否显示
				setAddPigBtnVisibility(hogpenVp.getPigNumber()
						< HogpenViewPager.PIG_LIMIT);
				// 动画显示卖家信息
				loadSellerInfoAnim();
			}
		}, 1000);
	}

	/**
	 * 返回添加猪测试数据
	 */
	private PigDetailInfoAndOrder getPigTestData() {
		PigDetailInfoAndOrder pigInfo = new PigDetailInfoAndOrder();
		pigInfo.orderInfo = new OrderInfoForSeller();
		pigInfo.orderInfo.buyerNumber = 30;
		pigInfo.growthInfo = new GrowthInfo();
		pigInfo.growthInfo.pigWeight = 150;
		pigInfo.healthInfo = new HealthInfo();
		pigInfo.healthInfo.temperature = 36;
		pigInfo.healthInfo.status = PigStatus.SLEEPING;
		pigInfo.pigInfo = new PigInfo();
		return pigInfo;
	}

	/**
	 * 返回初始化猪圈测试数据
	 */
	private List<SellerHogpenInfo> getHogpenInfos() {
		List<SellerHogpenInfo> hogpenInfos = new ArrayList<>();
		SellerHogpenInfo hogpenInfo;
		hogpenInfo = new SellerHogpenInfo();
		hogpenInfo.hogpenID = "厕所边";
		hogpenInfo.hogpenWidth = 3;
		hogpenInfo.hogpenLength = 4;
		hogpenInfo.pigInfos = new ArrayList<>();
		// 构造猪数据
		PigDetailInfoAndOrder pigDetail;
		pigDetail = new PigDetailInfoAndOrder();
		pigDetail.growthInfo = new GrowthInfo();
		pigDetail.growthInfo.pigWeight = 180;
		pigDetail.healthInfo = new HealthInfo();
		pigDetail.healthInfo.status = PigStatus.EATING;
		pigDetail.healthInfo.temperature = 37.5f;
		pigDetail.orderInfo = new OrderInfoForSeller();
		pigDetail.orderInfo.buyerNumber = 8;
		hogpenInfo.pigInfos.add(pigDetail);
		//
		pigDetail = new PigDetailInfoAndOrder();
		pigDetail.growthInfo = new GrowthInfo();
		pigDetail.growthInfo.pigWeight = 180;
		pigDetail.healthInfo = new HealthInfo();
		pigDetail.healthInfo.status = PigStatus.EATING;
		pigDetail.healthInfo.temperature = 37.5f;
		pigDetail.orderInfo = new OrderInfoForSeller();
		pigDetail.orderInfo.buyerNumber = 8;
		hogpenInfo.pigInfos.add(pigDetail);

		hogpenInfos.add(hogpenInfo);


		return hogpenInfos;
	}


}
