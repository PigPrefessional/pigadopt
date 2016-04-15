package com.ai2020lab.pigadopted.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiviews.anim.AnimSimpleListener;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.biz.HttpPigDetailManager;
import com.ai2020lab.pigadopted.biz.HttpStatisticDataManager;
import com.ai2020lab.pigadopted.biz.PigDetailManager;
import com.ai2020lab.pigadopted.biz.StatisticsDataManager;
import com.ai2020lab.pigadopted.chart.StatisticsChartFragmentFactory;
import com.ai2020lab.pigadopted.common.DataManager;
import com.ai2020lab.pigadopted.model.order.PigPart;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrderResponse;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.model.statistic.BodyTemperatureResponse;
import com.ai2020lab.pigadopted.model.statistic.StepStaticResponse;
import com.ai2020lab.pigadopted.model.statistic.WeightStaticResponse;
import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Rocky on 16/3/7.
 */
public class PigDetailForSellerFragment extends Fragment {

	private static final String TAG = "PigDetailForSeller";
	public static final String KEY_PIG_DATA = "KEY_PIG_DATA";

	private FrameLayout mPigPartsContainer;
	protected RecyclerView mRecyclerView;
	private ImageView mWholePig;
	private Button mWeightChartBtn;
	private Button mStepsChartBtn;
	private Button mTemperatureBtn;
	private TextView mBuyerNumber;
	private TextView mIncreaseWeight;
	private TextView mPigTypeName;
	private TextView mPigAge;
	private TextView mPigWeight;
	private TextView mPigTemperature;
	private TextView mPigFatRate;
	private TextView mPigSteps;

	private Serializable mDataSet;


	protected PigInfo mPigInfo;


	public PigDetailForSellerFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPigInfo = (PigInfo) getArguments().getSerializable(KEY_PIG_DATA);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflateRootView(inflater, container, savedInstanceState);

		setupMainLayout(rootView);
		setupOtherViews(rootView);

		setChartsButtonListener();

		loadPigDetailData(mPigInfo);

		return rootView;
	}

	protected View inflateRootView(LayoutInflater inflater, ViewGroup container,
	                               Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_pig_detail_for_seller, container, false);
	}

	protected void setupMainLayout(View rootView) {
		mPigPartsContainer = (FrameLayout) rootView.findViewById(R.id.pig_parts_container);
		mWholePig = (ImageView) rootView.findViewById(R.id.whole_pig);
		mWeightChartBtn = (Button) rootView.findViewById(R.id.weight_chart);
		mTemperatureBtn = (Button) rootView.findViewById(R.id.temperture_chart);
		mStepsChartBtn = (Button) rootView.findViewById(R.id.steps_chart);

		mIncreaseWeight = (TextView) rootView.findViewById(R.id.pig_detail_increase_weight);
		mPigTypeName = (TextView) rootView.findViewById(R.id.pig_type_name);
		mPigAge = (TextView) rootView.findViewById(R.id.pig_age);
		mPigWeight = (TextView) rootView.findViewById(R.id.pig_weight);
		mPigTemperature = (TextView) rootView.findViewById(R.id.pig_temperature);
		mPigFatRate = (TextView) rootView.findViewById(R.id.pig_fat_rate);
		mPigSteps = (TextView) rootView.findViewById(R.id.pig_steps);
	}

	protected void setupOtherViews(View rootView) {
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.buyer_list);
		mBuyerNumber = (TextView) rootView.findViewById(R.id.buyer_number);
	}

	protected void loadPigDetailData(PigInfo pigInfo) {
		PigDetailManager pigDetailManager = new HttpPigDetailManager(getContext());

		final AIBaseActivity activity = (AIBaseActivity) getActivity();

		activity.showLoading(getString(R.string.prompt_loading));

		pigDetailManager.findSellerPigDetailInfo("" + pigInfo.pigID, new JsonHttpResponseHandler<PigDetailInfoAndOrderResponse>(activity) {
            @Override
            public void onHandleFailure(String errorMsg) {
                Log.i(TAG, errorMsg);
                activity.dismissLoading();
                ToastUtils.getInstance().showToast(activity, errorMsg);
            }

            @Override
            public void onHandleSuccess(int statusCode, Header[] headers, PigDetailInfoAndOrderResponse jsonObj) {
                activity.dismissLoading();

                final PigDetailInfoAndOrderResponse response = jsonObj;
                final PigDetailInfoAndOrder result = response.data;
                result.pigInfo = mPigInfo;

                setupPigInfoUI(result);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                activity.dismissLoading();
                ToastUtils.getInstance().showToast(activity, R.string.prompt_loading_failure);
            }
        });
	}

	protected void setupPigInfoUI(PigDetailInfoAndOrder result) {
		setupPigInfo(result);
		displayPig(result.orderInfo.pigParts);
		loadBuyersData(result.orderInfo.pigParts);
	}

	private void setupPigInfo(PigDetailInfoAndOrder result) {
		mIncreaseWeight.setVisibility(View.INVISIBLE);
		mIncreaseWeight.setText(String.format(getResources().getString(R.string.pig_detail_weight_increase), result.growthInfo.increasedWeight / 2));
		mPigTypeName.setText(DataManager.getInstance().getPigCategory(result.pigInfo.pigCategory.categoryID).categoryName);
        mPigWeight.setText(String.format(getResources().getString(R.string.pig_detail_weight), result.growthInfo.pigWeight));
		mPigSteps.setText(String.format(getResources().getString(R.string.pig_detail_steps), result.healthInfo.steps));
		mPigFatRate.setText(result.healthInfo.fatRate + "%");
		mPigTemperature.setText(result.healthInfo.temperature + "℃");
		mPigAge.setText(String.format(getResources().getString(R.string.pig_detail_age), result.pigInfo.attendedAge));
	}

	private void startPigIncreaseWeightAnim() {
		Animation anim;
		anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_left_down_in);
		anim.setInterpolator(new AnticipateOvershootInterpolator());
		mIncreaseWeight.setVisibility(View.VISIBLE);
		mIncreaseWeight.startAnimation(anim);
	}

	private void displayPig(List<PigPart> partList) {

		if (partList == null || partList.size() == 0) {
			return;
		}

		for (int i = 0; i < partList.size(); i++) {
			final PigPart part = partList.get(i);
			final int partImageId = getPigPartImageResID(part);

			ImageView image = new ImageView(getContext());
			image.setLayoutParams(mWholePig.getLayoutParams());
			image.setScaleType(mWholePig.getScaleType());

			image.setImageResource(partImageId);

			mPigPartsContainer.addView(image);
			Animation animation = startPigPartAnim(image);

			if (i == partList.size() - 1) {
				animation.setAnimationListener(new AnimSimpleListener() {
					@Override
					public void onAnimationEnd(Animation animation) {
						startPigIncreaseWeightAnim();
					}
				});
			}
		}


	}

	protected int getPigPartImageResID(PigPart pigPart) {
		return DataManager.getInstance().getPigPartImageResID(pigPart);
	}

	protected void loadBuyersData(List<PigPart> pigParts) {

		List<UserInfo> buyers = new ArrayList<>();
		Map<Integer, UserInfo> userMap = new HashMap<>();

		for (PigPart pigPart : pigParts) {
			final UserInfo user = pigPart.userInfo;

			if (!userMap.containsKey(user.userID)) {
				userMap.put(user.userID, user);
			}
		}


		final Set<Integer> keys = userMap.keySet();

		for (int key : keys) {
			buyers.add(userMap.get(key));
		}

		mBuyerNumber.setText(String.format(getResources().getString(R.string.pig_detail_buyer_number), buyers.size()));

		mRecyclerView.setHasFixedSize(true);

		// use a linear layout manager
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setAdapter(new BuyerAdapter(getContext(), buyers));
	}


	protected Animation startPigPartAnim(View animatedView) {
		Random random = new Random();

		int animationId = 0;

		switch (random.nextInt(4)) {
			case 0:
				animationId = R.anim.push_left_in;
				break;
			case 1:
				animationId = R.anim.push_top_in;
				break;
			case 2:
				animationId = R.anim.push_right_in;
				break;
			case 3:
				animationId = R.anim.slide_bottom_in;
				break;
			default:
				animationId = R.anim.push_left_in;
		}

		Animation anim = AnimationUtils.loadAnimation(getContext(), animationId);
		anim.setDuration(1000);
		animatedView.startAnimation(anim);

		return anim;
	}

	private void setChartsButtonListener() {
		mWeightChartBtn.setOnClickListener(createChartButtonListener(StatisticsChartFragment.CHART_TYPE_WEIGHT));
		mTemperatureBtn.setOnClickListener(createChartButtonListener(StatisticsChartFragment.CHART_TYPE_TEMPERATURE));
		mStepsChartBtn.setOnClickListener(createChartButtonListener(StatisticsChartFragment.CHART_TYPE_STEPS));
	}

	private View.OnClickListener createChartButtonListener(final int chartType) {

		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setupStatisticChart(chartType);
			}
		};

		return listener;
	}


	private void setupStatisticChart(final int chartType) {

		HttpStatisticDataManager statisticsDataManager = new HttpStatisticDataManager(getContext());

		final AIBaseActivity activity = (AIBaseActivity) getActivity();

		activity.showLoading(getString(R.string.prompt_loading));


		if (chartType == StatisticsChartFragment.CHART_TYPE_WEIGHT) {

			statisticsDataManager.queryWeightList("" + mPigInfo.pigID, StatisticsDataManager.DataType.DAY,
					null, null, new JsonHttpResponseHandler<WeightStaticResponse>(getContext()) {
						@Override
						public void onHandleSuccess(int statusCode, Header[] headers, WeightStaticResponse jsonObj) {
							mDataSet = (Serializable) jsonObj.data.dataList;
							activity.dismissLoading();
							addChartFragment(StatisticsChartFragmentFactory.ChartType.WEIGHT);
						}

						@Override
						public void onHandleFailure(String errorMsg) {
							activity.dismissLoading();
							ToastUtils.getInstance().showToast(activity, errorMsg);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
							activity.dismissLoading();
							ToastUtils.getInstance().showToast(activity, R.string.prompt_loading_failure);
						}

					});
		} else if (chartType == StatisticsChartFragment.CHART_TYPE_STEPS) {
			statisticsDataManager.queryStepList("" + mPigInfo.pigID, StatisticsDataManager.DataType.DAY,
					null, null, new JsonHttpResponseHandler<StepStaticResponse>(getContext()) {
						@Override
						public void onHandleSuccess(int statusCode, Header[] headers, StepStaticResponse jsonObj) {
							mDataSet = (Serializable) jsonObj.data.dataList;
							activity.dismissLoading();
							addChartFragment(StatisticsChartFragmentFactory.ChartType.STEPS);
						}

						@Override
						public void onHandleFailure(String errorMsg) {
							activity.dismissLoading();
							ToastUtils.getInstance().showToast(activity, errorMsg);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
							activity.dismissLoading();
							ToastUtils.getInstance().showToast(activity, R.string.prompt_loading_failure);
						}

					});
		} else if (chartType == StatisticsChartFragment.CHART_TYPE_TEMPERATURE) {

			statisticsDataManager.queryTemperatureList("" + mPigInfo.pigID, StatisticsDataManager.DataType.DAY,
					null, null, new JsonHttpResponseHandler<BodyTemperatureResponse>(getContext()) {
						@Override
						public void onHandleSuccess(int statusCode, Header[] headers, BodyTemperatureResponse jsonObj) {
							mDataSet = (Serializable) jsonObj.data.dataList;
							activity.dismissLoading();
							addChartFragment(StatisticsChartFragmentFactory.ChartType.TEMPERATURE);
						}

						@Override
						public void onHandleFailure(String errorMsg) {
							activity.dismissLoading();
							ToastUtils.getInstance().showToast(activity, errorMsg);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
							activity.dismissLoading();
							ToastUtils.getInstance().showToast(activity, R.string.prompt_loading_failure);
						}

					});
		}
	}


	private void addChartFragment(final StatisticsChartFragmentFactory.ChartType chartType) {
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;  // 屏幕宽度（像素）
		int height = metric.heightPixels;  // 屏幕高度（像素


		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");

		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		final float scale = metric.density;
		// dp to px
		int marginWidth = (int) (15 * scale + 0.5f);
		int marginHeight = (int) (300 * scale + 0.5f);

        DialogFragment newFragment = StatisticsChartFragmentFactory
                .createFragment(width - marginWidth, height - marginHeight, chartType, mDataSet);

		newFragment.show(ft, "dialog");
	}

	private class BuyerAdapter extends
			RecyclerView.Adapter<BuyerAdapter.ViewHolder> {

		private LayoutInflater mInflater;
		private List<UserInfo> mBuyers;

		public BuyerAdapter(Context context, List<UserInfo> buyers) {
			mInflater = LayoutInflater.from(context);
			mBuyers = buyers;
		}

		class ViewHolder extends RecyclerView.ViewHolder {
			public ViewHolder(View view) {
				super(view);
			}

			ImageView mPortrait;
			TextView mName;
		}

		@Override
		public int getItemCount() {
			return mBuyers.size();
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			View view = mInflater.inflate(R.layout.item_buyer,
					viewGroup, false);
			ViewHolder viewHolder = new ViewHolder(view);

			viewHolder.mPortrait = (ImageView) view
                    .findViewById(R.id.buyer_portrait);
			viewHolder.mName = (TextView) view.findViewById(R.id.buyer_name);

			return viewHolder;
		}


		@Override
		public void onBindViewHolder(final ViewHolder viewHolder, final int index) {
			final ImageLoader imageLoader = ImageLoader.getInstance();

			final UserInfo buyer = mBuyers.get(index);

			viewHolder.mName.setText(buyer.userName);
			imageLoader.displayImage(buyer.userPortrait, viewHolder.mPortrait);
		}

	}



}
