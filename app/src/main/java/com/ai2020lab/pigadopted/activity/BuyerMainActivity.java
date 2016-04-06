package com.ai2020lab.pigadopted.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.BounceInterpolator;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.adapter.BuyerPigListRvAdapter;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.DataManager;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.model.pig.PigListRequest;
import com.ai2020lab.pigadopted.model.pig.PigListResponse;
import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;


/**
 * 买家主页
 * Created by Justin on 2016/3/4.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class BuyerMainActivity extends AIBaseActivity {
	/**
	 * 日志标题
	 */
	private final static String TAG = BuyerMainActivity.class.getSimpleName();
	/**
	 * 买家用户信息
	 */
	private UserInfo userInfo;
	/**
	 * 买家猪列表RecyclerView
	 */
	private RecyclerView buyerPigListRv;

	private BuyerPigListRvAdapter buyerPigListRvAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userInfo = DataManager.getInstance().getBuyerInfo();
		setContentView(R.layout.activity_main_buyer);
		setToolbar();
		assignViews();
		setBuyerPigListRv();
		// 请求买家猪列表
		queryCustomerPigList();
	}

	private void setToolbar() {
		supportToolbar(true);
		setToolbarTitle(String.format(getString(R.string.activity_title_buyer_main),
				userInfo.userName));
	}

	private void assignViews() {
		buyerPigListRv = (RecyclerView) findViewById(R.id.buyer_pig_list_rv);
	}

	/**
	 * 设置买家猪列表RecyclerView
	 */
	private void setBuyerPigListRv() {
		buyerPigListRvAdapter = new BuyerPigListRvAdapter(getActivity());
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		buyerPigListRv.setLayoutManager(layoutManager);
		buyerPigListRv.setAdapter(buyerPigListRvAdapter);
		// 加入item动画效果
		SlideInDownAnimator animator = new SlideInDownAnimator();
//		LandingAnimator animator = new LandingAnimator();
		animator.setAddDuration(500);
		animator.setInterpolator(new BounceInterpolator());
		buyerPigListRv.setItemAnimator(animator);
		buyerPigListRvAdapter.setOnClickCardListener(
				new BuyerPigListRvAdapter.OnClickBuyerPigListener() {
					@Override
					public void onClick(PigDetailInfoAndOrder pigDetailInfo) {
						skipToPigDetailActivity(pigDetailInfo.pigInfo);
					}
				});
	}

	/**
	 * 跳转到猪详情界面
	 */
	private void skipToPigDetailActivity(PigInfo pigInfo) {
		LogUtils.i(TAG, "跳转到猪详情界面");
		Intent intent = new Intent(this, PigDetailActivity.class);
		intent.putExtra(PigDetailActivity.KEY_DETAIL_TYPE, PigDetailActivity.TYPE_BUYER);
		Bundle bundle = new Bundle();
		bundle.putSerializable(IntentExtra.PIG_INFO, pigInfo);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 请求买家猪列表
	 */
	private void queryCustomerPigList() {
		LogUtils.i(TAG, "--查询买家猪列表--");
		// 弹出提示
		showLoading(getString(R.string.prompt_loading));
		PigListRequest data = new PigListRequest();
		data.userID = userInfo.userID;
		HttpManager.postJson(this, UrlName.PIG_LIST_FOR_BUYER.getUrl(), data,
				new JsonHttpResponseHandler<PigListResponse>(this) {
					/**
					 * 成功回调
					 *
					 * @param statusCode 状态码
					 * @param headers    Header
					 * @param jsonObj    服务端返回的对象
					 */
					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            final PigListResponse jsonObj) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								List<PigDetailInfoAndOrder> pigDetailInfos = jsonObj.data.pigInfos;
								int size = pigDetailInfos.size();
								for (int i = 0; i < size; i++) {
									PigDetailInfoAndOrder pigInfoAndOrder = pigDetailInfos.get(i);
									buyerPigListRvAdapter.add(i, pigInfoAndOrder);
								}
							}
						}, 1000);
					}

					@Override
					public void onCancel() {
						dismissLoading();
						// 没有网络的情况会终止请求
						ToastUtils.getInstance().showToast(getActivity(),
								R.string.prompt_loading_failure);
					}

					@Override
					public void onHandleFailure(String errorMsg) {
						dismissLoading();
						ToastUtils.getInstance().showToast(getActivity(),
								R.string.prompt_loading_failure);
					}

				});

	}

//	private List<PigDetailInfoAndOrder> getTestListData() {
//		List<PigDetailInfoAndOrder> pigDetailInfos = new ArrayList<>();
//		pigDetailInfos.clear();
//		PigDetailInfoAndOrder pigDetailForBuyer;
//
//		pigDetailForBuyer = new PigDetailInfoAndOrder();
//		pigDetailForBuyer.pigInfo = new PigInfo();
//		// 初始化卖家名字
//		pigDetailForBuyer.pigInfo.hogpenInfo = new HogpenInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo = new UserInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo.userName = "刘司机";
//		// 初始化入栏时间，入栏体重，入栏年龄
//		pigDetailForBuyer.pigInfo.attendedWeight = 40;
//		pigDetailForBuyer.pigInfo.attendedAge = 5.34f;
//		pigDetailForBuyer.pigInfo.attendedTime = 1445508052553l;
//		// 初始化猪状态
//		pigDetailForBuyer.healthInfo = new HealthInfo();
//		pigDetailForBuyer.healthInfo.status = PigStatus.EATING;
//		// 初始化猪成长历程
//		pigDetailForBuyer.growthInfo = new GrowthInfo();
//		pigDetailForBuyer.growthInfo.pigWeight = 140;
//		pigDetailInfos.add(pigDetailForBuyer);
//		///////////////////////////////////////////////////////////
//		pigDetailForBuyer = new PigDetailInfoAndOrder();
//		pigDetailForBuyer.pigInfo = new PigInfo();
//		// 初始化卖家名字
//		pigDetailForBuyer.pigInfo.hogpenInfo = new HogpenInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo = new UserInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo.userName = "乔帮主";
//		// 初始化入栏时间，入栏体重，入栏年龄
//		pigDetailForBuyer.pigInfo.attendedWeight = 80;
//		pigDetailForBuyer.pigInfo.attendedAge = 8.25f;
//		pigDetailForBuyer.pigInfo.attendedTime = 1446372052553l;
//		// 初始化猪状态
//		pigDetailForBuyer.healthInfo = new HealthInfo();
//		pigDetailForBuyer.healthInfo.status = PigStatus.SLEEPING;
//		// 初始化猪成长历程
//		pigDetailForBuyer.growthInfo = new GrowthInfo();
//		pigDetailForBuyer.growthInfo.pigWeight = 100;
//		pigDetailInfos.add(pigDetailForBuyer);
//		///////////////////////////////////////////////////////////
//		pigDetailForBuyer = new PigDetailInfoAndOrder();
//		pigDetailForBuyer.pigInfo = new PigInfo();
//		// 初始化卖家名字
//		pigDetailForBuyer.pigInfo.hogpenInfo = new HogpenInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo = new UserInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo.userName = "小马哥";
//		// 初始化入栏时间，入栏体重，入栏年龄
//		pigDetailForBuyer.pigInfo.attendedWeight = 100;
//		pigDetailForBuyer.pigInfo.attendedAge = 9.35f;
//		pigDetailForBuyer.pigInfo.attendedTime = 1451642452553l;
//		// 初始化猪状态
//		pigDetailForBuyer.healthInfo = new HealthInfo();
//		pigDetailForBuyer.healthInfo.status = PigStatus.WALKING;
//		// 初始化猪成长历程
//		pigDetailForBuyer.growthInfo = new GrowthInfo();
//		pigDetailForBuyer.growthInfo.pigWeight = 110;
//		pigDetailInfos.add(pigDetailForBuyer);
//		///////////////////////////////////////////////////////////
//		pigDetailForBuyer = new PigDetailInfoAndOrder();
//		pigDetailForBuyer.pigInfo = new PigInfo();
//		// 初始化卖家名字
//		pigDetailForBuyer.pigInfo.hogpenInfo = new HogpenInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo = new UserInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo.userName = "杰爷";
//		// 初始化入栏时间，入栏体重，入栏年龄
//		pigDetailForBuyer.pigInfo.attendedWeight = 40;
//		pigDetailForBuyer.pigInfo.attendedAge = 4.35f;
//		pigDetailForBuyer.pigInfo.attendedTime = 1451642452553l;
//		// 初始化猪状态
//		pigDetailForBuyer.healthInfo = new HealthInfo();
//		pigDetailForBuyer.healthInfo.status = PigStatus.EATING;
//		// 初始化猪成长历程
//		pigDetailForBuyer.growthInfo = new GrowthInfo();
//		pigDetailForBuyer.growthInfo.pigWeight = 80;
//		pigDetailInfos.add(pigDetailForBuyer);
//		///////////////////////////////////////////////////////////
//		pigDetailForBuyer = new PigDetailInfoAndOrder();
//		pigDetailForBuyer.pigInfo = new PigInfo();
//		// 初始化卖家名字
//		pigDetailForBuyer.pigInfo.hogpenInfo = new HogpenInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo = new UserInfo();
//		pigDetailForBuyer.pigInfo.hogpenInfo.userInfo.userName = "先爷";
//		// 初始化入栏时间，入栏体重，入栏年龄
//		pigDetailForBuyer.pigInfo.attendedWeight = 20;
//		pigDetailForBuyer.pigInfo.attendedAge = 3.35f;
//		pigDetailForBuyer.pigInfo.attendedTime = 1451642452553l;
//		// 初始化猪状态
//		pigDetailForBuyer.healthInfo = new HealthInfo();
//		pigDetailForBuyer.healthInfo.status = PigStatus.SLEEPING;
//		// 初始化猪成长历程
//		pigDetailForBuyer.growthInfo = new GrowthInfo();
//		pigDetailForBuyer.growthInfo.pigWeight = 60;
//		pigDetailInfos.add(pigDetailForBuyer);
//		return pigDetailInfos;
//
//	}


}
