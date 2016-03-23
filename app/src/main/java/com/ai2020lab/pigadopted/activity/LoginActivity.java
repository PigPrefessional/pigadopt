/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiviews.anim.AnimSimpleListener;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.Constants;
import com.ai2020lab.pigadopted.common.DataManager;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.model.user.UserInfo;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 登录界面，只做简单的角色切换
 * Created by Justin Z on 2016/3/20.
 * 502953057@qq.com
 */
public class LoginActivity extends AIBaseActivity {
	/**
	 * 日志标题
	 */
	private final static String TAG = LoginActivity.class.getSimpleName();

	private RelativeLayout buyerInfoRl;
	private ImageView buyerIv;
	private CircleImageView buyerCiv;
	private ImageView buyerSelectIv;
	private TextView buyerTv;
	private RelativeLayout sellerInfoRl;
	private ImageView sellerIv;
	private CircleImageView sellerCiv;
	private ImageView sellerSelectIv;
	private TextView sellerTv;
	private CoordinatorLayout snackContainerCl;
	/**
	 * 当前选中的登录用户角色类型
	 */
	private int currentRoleType = -1;

	private boolean isLoadAnim = false;

	private Animation slideInLeft;
	private Animation slideInRight;
	private Animation scaleIn;
	private Animation selectIn;
	private Animation selectOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initAnims();
		setContentView(R.layout.activity_login);
		setToolbar();
		assignViews();
		setOnSelectRoleTypeListener(selectIn, selectOut);
	}

	// 初始化数据
	private void initData(){
		DataManager.getInstance().init();
	}

	private void setToolbar() {
		supportToolbar(true);
		setToolbarTitle(getString(R.string.activity_title_login));
	}

	private void assignViews() {
		buyerInfoRl = (RelativeLayout) findViewById(R.id.buyer_info_rl);
		buyerIv = (ImageView) findViewById(R.id.buyer_iv);
		buyerCiv = (CircleImageView) findViewById(R.id.buyer_civ);
		buyerSelectIv = (ImageView) findViewById(R.id.buyer_select_iv);
		buyerTv = (TextView) findViewById(R.id.buyer_tv);
		sellerInfoRl = (RelativeLayout) findViewById(R.id.seller_info_rl);
		sellerIv = (ImageView) findViewById(R.id.seller_iv);
		sellerCiv = (CircleImageView) findViewById(R.id.seller_civ);
		sellerSelectIv = (ImageView) findViewById(R.id.seller_select_iv);
		sellerTv = (TextView) findViewById(R.id.seller_tv);
		snackContainerCl = (CoordinatorLayout) findViewById(R.id.snack_container_cl);
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			loadAnim();
		} else {
			setViewsGone();
		}
	}

	private void setViewsGone() {
		buyerIv.setVisibility(View.GONE);
		buyerCiv.setVisibility(View.GONE);
		buyerSelectIv.setVisibility(View.GONE);
		buyerTv.setVisibility(View.GONE);
		sellerIv.setVisibility(View.GONE);
		sellerCiv.setVisibility(View.GONE);
		sellerSelectIv.setVisibility(View.GONE);
		sellerTv.setVisibility(View.GONE);
	}

	/**
	 * 初始化各个动画资源
	 */
	private void initAnims() {
		slideInLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
		slideInRight = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in);
		scaleIn = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_alpha_in);
		selectIn = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_alpha_in);
		selectOut = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_alpha_out);
		slideInLeft.setDuration(1000);
		slideInRight.setDuration(1000);
		scaleIn.setDuration(1000);
		selectIn.setDuration(500);
		selectOut.setDuration(500);
		scaleIn.setInterpolator(new BounceInterpolator());
		selectIn.setInterpolator(new BounceInterpolator());
		selectOut.setInterpolator(new BounceInterpolator());
		slideInLeft.setInterpolator(new AnticipateOvershootInterpolator());
		slideInRight.setInterpolator(new AnticipateOvershootInterpolator());
	}

	/**
	 * 进入界面加载动画
	 */
	private void loadAnim() {
		// 买家，买家底部图片从屏幕左边飞入
		buyerIv.startAnimation(slideInLeft);
		sellerIv.startAnimation(slideInRight);
		buyerIv.setVisibility(View.VISIBLE);
		sellerIv.setVisibility(View.VISIBLE);
		// 动画显示买家信息
		slideInLeft.setAnimationListener(new AnimSimpleListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				LogUtils.i(TAG, "左边滑入动画执行完毕");
				buyerCiv.startAnimation(scaleIn);
				buyerTv.startAnimation(scaleIn);
				buyerCiv.setVisibility(View.VISIBLE);
				buyerTv.setVisibility(View.VISIBLE);
			}
		});
		// 动画显示卖家信息
		slideInRight.setAnimationListener(new AnimSimpleListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				LogUtils.i(TAG, "右边滑入动画执行完毕");
				sellerCiv.startAnimation(scaleIn);
				sellerTv.startAnimation(scaleIn);
				sellerCiv.setVisibility(View.VISIBLE);
				sellerTv.setVisibility(View.VISIBLE);
			}
		});
		// 角色信息显示完毕再显示选择状态
		scaleIn.setAnimationListener(new AnimSimpleListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				setRoleType(currentRoleType, selectIn, selectOut, false);
			}
		});
	}

	private void setOnSelectRoleTypeListener(Animation animIn, Animation animOut) {
		buyerInfoRl.setOnClickListener(new OnSelectRoleTypeListener(Constants.ROLE_TYPE_BUYER,
				animIn, animOut));
		sellerInfoRl.setOnClickListener(new OnSelectRoleTypeListener(Constants.ROLE_TYPE_SELLER,
				animIn, animOut));
	}

	/**
	 * 设置买家，卖家选中状态，并执行动画
	 */
	private void setRoleType(int roleType, Animation animIn, Animation animOut,
	                         final boolean isClick) {
		LogUtils.i(TAG, "设置角色选中");
		LogUtils.i(TAG, "角色:" + roleType + " 当前角色:" + currentRoleType);
		// 设置买家
		if (roleType == Constants.ROLE_TYPE_BUYER &&
				buyerSelectIv.getVisibility() == View.GONE) {
			LogUtils.i(TAG, "设置买家选中");
			buyerSelectIv.setVisibility(View.VISIBLE);
			buyerSelectIv.startAnimation(animIn);
			if (sellerSelectIv.getVisibility() == View.VISIBLE) {
				sellerSelectIv.setVisibility(View.GONE);
				sellerSelectIv.startAnimation(animOut);
			}
			isLoadAnim = true;
			currentRoleType = Constants.ROLE_TYPE_BUYER;
		}
		// 设置卖家
		else if (roleType == Constants.ROLE_TYPE_SELLER &&
				sellerSelectIv.getVisibility() == View.GONE) {
			LogUtils.i(TAG, "设置卖家选中");
			sellerSelectIv.setVisibility(View.VISIBLE);
			sellerSelectIv.startAnimation(animIn);
			if (buyerSelectIv.getVisibility() == View.VISIBLE) {
				buyerSelectIv.setVisibility(View.GONE);
				buyerSelectIv.startAnimation(animOut);
			}
			isLoadAnim = true;
			currentRoleType = Constants.ROLE_TYPE_SELLER;
		}
		// 执行动画的时候才绑定监听,动画执行完毕，界面跳转
		if (isLoadAnim) {
			animIn.setAnimationListener(new AnimSimpleListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					if (isClick) {
						LogUtils.i(TAG, "选择角色动画执行完毕，界面跳转");
						skipToMainActivity(currentRoleType);
					}
					isLoadAnim = false;
				}
			});
		} else if (isClick) {
			LogUtils.i(TAG, "界面直接跳转");
			skipToMainActivity(currentRoleType);
		}
	}

	/**
	 * 选择角色类型监听
	 */
	private class OnSelectRoleTypeListener implements View.OnClickListener {

		private int roleType;
		private Animation animIn;
		private Animation animOut;

		public OnSelectRoleTypeListener(int roleType, Animation animIn, Animation animOut) {
			this.roleType = roleType;
			this.animIn = animIn;
			this.animOut = animOut;
		}

		@Override
		public void onClick(View v) {
			setRoleType(roleType, animIn, animOut, true);
		}
	}

	/**
	 * 跳转到买家或卖家主页
	 */
	private void skipToMainActivity(int currentRoleType) {
		UserInfo userInfo = getUserInfo(currentRoleType);
		// 跳转到买家主页
		if (currentRoleType == Constants.ROLE_TYPE_BUYER) {
			Intent intent = new Intent(this, BuyerMainActivity.class);
			intent.putExtra(IntentExtra.USER_INFO, userInfo);
			startActivity(intent);
		}
		// 跳转到卖家主页
		else if (currentRoleType == Constants.ROLE_TYPE_SELLER) {
			Intent intent = new Intent(this, SellerMainActivity.class);
			intent.putExtra(IntentExtra.USER_INFO, userInfo);
			startActivity(intent);
		} else {
			// 弹出角色选择提示
			Snackbar.make(snackContainerCl, getString(R.string.login_prompt_select_role),
					Snackbar.LENGTH_LONG).show();
		}
	}

	/**
	 * 获取UserInfo对象
	 */
	private UserInfo getUserInfo(int roleType) {
		TextView roleNameTv = null;
		String userID = null;
		if (roleType == Constants.ROLE_TYPE_BUYER) {
			roleNameTv = buyerTv;
			userID = "213";
		} else if (roleType == Constants.ROLE_TYPE_SELLER) {
			roleNameTv = sellerTv;
			userID = "123";
		}
		UserInfo userInfo = new UserInfo();
		userInfo.userName = roleNameTv != null ? roleNameTv.getText().toString() : "";
		userInfo.userID = userID;
		LogUtils.i(TAG, "userName:" + userInfo.userName);
		return userInfo;
	}

}