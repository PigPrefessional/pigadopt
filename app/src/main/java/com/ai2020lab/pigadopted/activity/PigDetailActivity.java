package com.ai2020lab.pigadopted.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.biz.HttpPigDetailManager;
import com.ai2020lab.pigadopted.biz.PigDetailManager;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.fragment.PigDetailForBuyerFragment;
import com.ai2020lab.pigadopted.fragment.PigDetailForSellerFragment;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrderResponse;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class PigDetailActivity extends AIBaseActivity {

	private final static String TAG = PigDetailActivity.class.getSimpleName();

	public static final String KEY_DETAIL_TYPE = "KeyDetailType";
	public static final int TYPE_SELLER = 1;
	public static final int TYPE_BUYER = 2;

    private PigInfo mPigInfo;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pig_detail);

		supportToolbar(true);
		setToolbarTitle(getText(R.string.pig_detail_title).toString());
		setToolbarLeft(R.drawable.toolbar_back_selector);
		setToolbarRight(R.drawable.pig_detail_grow_history_selector);
		setOnLeftClickListener(new OnLeftClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

		setOnRightClickListener(new OnRightClickListener() {
            @Override
            public void onClick() {
                skipToGrowthInfosActivity();
            }
        });

        final int type = getIntent().getExtras().getInt(KEY_DETAIL_TYPE, TYPE_BUYER);

        mPigInfo = (PigInfo) getIntent().getSerializableExtra(IntentExtra.PIG_INFO);

        setupDetailFragment(type, mPigInfo);

	//	loadPigDetailData();
	}

	/**
	 * 跳转到成长历程界面
	 */
	private void skipToGrowthInfosActivity() {
		Intent intent = new Intent(this, GrowthHistoryActivity.class);
        intent.putExtra(IntentExtra.PIG_INFO, mPigInfo);
		startActivity(intent);
	}

	private void loadPigDetailData() {
		PigDetailManager pigDetailManager = new HttpPigDetailManager(this);

        mPigInfo = (PigInfo) getIntent().getSerializableExtra(IntentExtra.PIG_INFO);

		final int type = getIntent().getExtras().getInt(KEY_DETAIL_TYPE, TYPE_BUYER);

		showLoading(getString(R.string.prompt_loading));

		if (type == TYPE_SELLER) {
			pigDetailManager.findSellerPigDetailInfo(mPigInfo.pigID, new JsonHttpResponseHandler<PigDetailInfoAndOrderResponse>(this) {
				@Override
				public void onHandleFailure(String errorMsg) {
					Log.i(TAG, errorMsg);
					dismissLoading();
                    ToastUtils.getInstance().showToast(PigDetailActivity.this, errorMsg);
				}

				@Override
				public void onHandleSuccess(int statusCode, Header[] headers, PigDetailInfoAndOrderResponse jsonObj) {
					setupDetailFragment(type, jsonObj);
					dismissLoading();
				}

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    dismissLoading();
                    ToastUtils.getInstance().showToast(PigDetailActivity.this, R.string.prompt_loading_failure);
                }
            });
		} else {
			pigDetailManager.findSellerPigDetailInfo(mPigInfo.pigID, new JsonHttpResponseHandler<PigDetailInfoAndOrderResponse>(this) {
				@Override
				public void onHandleFailure(String errorMsg) {
					Log.i(TAG, errorMsg);
					dismissLoading();
                    ToastUtils.getInstance().showToast(PigDetailActivity.this, errorMsg);
				}

				@Override
				public void onHandleSuccess(int statusCode, Header[] headers, PigDetailInfoAndOrderResponse jsonObj) {
					setupDetailFragment(type, jsonObj);
					dismissLoading();
				}

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    dismissLoading();
                    ToastUtils.getInstance().showToast(PigDetailActivity.this, R.string.prompt_loading_failure);
                }
			});
		}
	}

	private void setupDetailFragment(int type, PigDetailInfoAndOrderResponse data) {
		Fragment pigDetailFragment;

		if (type == TYPE_SELLER) {
			pigDetailFragment = new PigDetailForSellerFragment();
		} else {
			pigDetailFragment = new PigDetailForBuyerFragment();
		}

		Bundle args = new Bundle();
        data.data.pigInfo = mPigInfo;
		args.putSerializable(PigDetailForSellerFragment.KEY_PIG_DATA, data);
		pigDetailFragment.setArguments(args);


		FragmentManager fragmentManager = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.add(R.id.fragment_container, pigDetailFragment);

		fragmentTransaction.commit();
	}

    private void setupDetailFragment(int type, PigInfo data) {
        Fragment pigDetailFragment;

        if (type == TYPE_SELLER) {
            pigDetailFragment = new PigDetailForSellerFragment();
        } else {
            pigDetailFragment = new PigDetailForBuyerFragment();
        }

        Bundle args = new Bundle();
        args.putSerializable(PigDetailForSellerFragment.KEY_PIG_DATA, data);
        pigDetailFragment.setArguments(args);


        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, pigDetailFragment);

        fragmentTransaction.commit();
    }

}
