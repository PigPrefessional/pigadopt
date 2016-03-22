package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.model.user.UserInfo;

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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userInfo = (UserInfo) getIntent().getExtras().get(IntentExtra.USER_INFO);
		setContentView(R.layout.activity_main_buyer);
		setToolbar();
		assignViews();
	}

	private void setToolbar() {
		supportToolbar(true);
		setToolbarTitle(String.format(getString(R.string.activity_title_buyer_main), userInfo.userName));
	}


	private void assignViews() {
		buyerPigListRv = (RecyclerView) findViewById(R.id.buyer_pig_list_rv);
	}





}
