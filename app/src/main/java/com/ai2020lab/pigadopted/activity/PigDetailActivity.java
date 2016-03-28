package com.ai2020lab.pigadopted.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;

public class PigDetailActivity extends AIBaseActivity {

	public static final String KEY_DETAIL_TYPE = "KeyDetailType";
	public static final int TYPE_SELLER = 1;
	public static final int TYPE_BUYER = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
