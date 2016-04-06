package com.ai2020lab.pigadopted.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.model.pig.PigInfo;

public class PigDetailActivity extends AIBaseActivity {

	private final static String TAG = PigDetailActivity.class.getSimpleName();

	public static final String KEY_DETAIL_TYPE = "KeyDetailType";
	public static final int TYPE_SELLER = 1;
	public static final int TYPE_BUYER = 2;

	private PigInfo pigInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pigInfo = (PigInfo) getIntent().getSerializableExtra(IntentExtra.PIG_INFO);
		LogUtils.i(TAG, "入栏体重->" + pigInfo.attendedWeight);

		int type = getIntent().getExtras().getInt(KEY_DETAIL_TYPE, TYPE_BUYER);
		if (type == TYPE_SELLER) {
			setContentView(R.layout.activity_pig_detail_for_seller);
		} else {
			setContentView(R.layout.activity_pig_detail_for_buyer);
		}

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

	}

	/**
	 * 跳转到成长历程界面
	 */
	private void skipToGrowthInfosActivity() {
		Intent intent = new Intent(this, GrowthHistoryActivity.class);
		startActivity(intent);
	}


}
