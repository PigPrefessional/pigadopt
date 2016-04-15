package com.ai2020lab.pigadopted.activity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.DataManager;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.fragment.OnClickDialogBtnListener;
import com.ai2020lab.pigadopted.fragment.PigAddSuccessDialog;
import com.ai2020lab.pigadopted.model.hogpen.HogpenListRequest;
import com.ai2020lab.pigadopted.model.hogpen.HogpenListResponse;
import com.ai2020lab.pigadopted.model.hogpen.SellerHogpenInfo;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;
import com.ai2020lab.pigadopted.view.BirdIndicator;
import com.ai2020lab.pigadopted.view.HogpenViewPager;

import java.util.List;

import cz.msebera.android.httpclient.Header;

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
	 * 添加猪界面requestCode
	 */
	private final static int RESULT_ADD_PIG = 0x0004;
	/**
	 * 添加猪圈界面requestCode
	 */
	private final static int RESULT_ADD_HOGPEN = 0x0005;
	/**
	 * 添加猪成功提示对话框TAG
	 */
	private final static String TAG_DIALOG_ADD_PIG_SUCCESS = "tag_dialog_add_pig_success";

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
	/**
	 * 卖家用户数据
	 */
	private UserInfo userInfo;


	/**
	 * 程序入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userInfo = DataManager.getInstance().getSellerInfo();
		setContentView(R.layout.activity_main_seller);
		// 不展示工具栏
		supportToolbar(false);
		// 分配各个View
		assignViews();
		initHogpenViewPager();
		initBirdIndicator();
		initAddPigBtn();
		// 请求猪圈列表
		queryHogpensList();
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
				LogUtils.i(TAG, "跳转到添加猪圈界面");
				skipToAddHogpenActivity();
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
						< HogpenViewPager.PIG_LIMIT && hogpenVp.getHogpenNumber() > 0);
			}
		});
		hogpenVp.setOnPigClickListener(new HogpenViewPager.OnPigClickListener() {
			@Override
			public void onPigClick(SellerHogpenInfo hogpenInfo, PigDetailInfoAndOrder pigInfo) {
				// 点击猪跳转到详情界面
				skipToPigDetailActivity(pigInfo.pigInfo);
			}
		});
		// 猪添加动画执行完毕监听
		hogpenVp.setOnPigAddListener(new HogpenViewPager.OnPigAddListener() {
			@Override
			public void onEnd(PigDetailInfoAndOrder pigInfo) {
				// 猪添加动画执行完毕弹出提示对话框
				showAddPigSuccessDialog(pigInfo);
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
//				showAddPigDialog();
				// 跳转到添加猪界面
				skipToAddPigActivity();
			}
		});
	}

	/**
	 * 跳转到猪详情界面
	 */
	private void skipToPigDetailActivity(PigInfo pigInfo) {
		LogUtils.i(TAG, "跳转到猪详情界面");
		Intent intent = new Intent(this, PigDetailActivity.class);
		intent.putExtra(PigDetailActivity.KEY_DETAIL_TYPE, PigDetailActivity.TYPE_SELLER);
		Bundle bundle = new Bundle();
		bundle.putSerializable(IntentExtra.PIG_INFO, pigInfo);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 跳转到添加猪界面
	 */
	private void skipToAddPigActivity() {
		LogUtils.i(TAG, "跳转到添加猪界面");
		Intent intent = new Intent(this, AddPigActivity.class);
		intent.putExtra(IntentExtra.HOGPEN_ID,
				hogpenVp.getHogpen(hogpenVp.getCurrentIndex()).hogpenID);
		startActivityForResult(intent, RESULT_ADD_PIG);
	}

	/**
	 * 跳转到添加猪圈界面
	 */
	private void skipToAddHogpenActivity() {
		LogUtils.i(TAG, "跳转到添加猪圈界面");
		Intent intent = new Intent(this, AddHogpenActivity.class);
		startActivityForResult(intent, RESULT_ADD_HOGPEN);
	}

	/**
	 * 跳转到距离相机拍照界面
	 */
	private void skipToDistanceCameraActivity(PigDetailInfoAndOrder pigDetailInfoAndOrder) {
		Intent intent = new Intent(this, DistanceCameraActivity.class);
		intent.putExtra(IntentExtra.PIG_INFO, pigDetailInfoAndOrder.pigInfo);
		startActivity(intent);
	}

	/**
	 * 设置隐藏或者显示添加猪按钮
	 */
	//猪圈切换的时候监听猪圈中的猪是否达到上限，达到上限则隐藏添加猪按钮,否则显示添加猪按钮
	private void setAddPigBtnVisibility(boolean isShow) {
		Animation animIn = AnimationUtils.loadAnimation(this, R.anim.scale_bottom_in);
		Animation animOut = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
		animIn.setInterpolator(new BounceInterpolator());
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
		Animation animIn = AnimationUtils.loadAnimation(this, R.anim.scale_bottom_in);
		animIn.setInterpolator(new BounceInterpolator());
		// 中文字体加粗,xml中设置无效
		sellerInfoTv.getPaint().setFakeBoldText(true);
		sellerInfoTv.setText(String.format(getString(R.string.pig_seller_name), userInfo.userName));
		sellerInfoTv.setVisibility(View.VISIBLE);
		sellerInfoTv.startAnimation(animIn);
	}

	/**
	 * 弹出添加猪成功提示对话框
	 */
	private void showAddPigSuccessDialog(final PigDetailInfoAndOrder pigInfo) {
		PigAddSuccessDialog dialog = PigAddSuccessDialog.newInstance(true,
				new OnClickDialogBtnListener<Void>() {
					@Override
					public void onClickEnsure(DialogFragment df, Void aVoid) {
						df.dismiss();
						skipToDistanceCameraActivity(pigInfo);
					}

					@Override
					public void onClickCancel(DialogFragment df) {
						df.dismiss();
					}
				});
		Fragment fragment = getFragmentManager().findFragmentByTag(TAG_DIALOG_ADD_PIG_SUCCESS);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragment != null)
			ft.remove(fragment);
		ft.addToBackStack(null);
		dialog.show(ft, TAG_DIALOG_ADD_PIG_SUCCESS);
	}

	/**
	 * 处理相册选图和系统相机拍照返回图片逻辑
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			// 添加猪返回逻辑
			if (requestCode == RESULT_ADD_PIG && intent != null) {
				LogUtils.i(TAG, "添加猪成功，加入界面--");
				PigDetailInfoAndOrder pigInfo = (PigDetailInfoAndOrder) intent
						.getSerializableExtra(IntentExtra.PIG_INFO_AND_ORDER);
				hogpenVp.addPig(pigInfo);
				setAddPigBtnVisibility(hogpenVp.getPigNumber()
						< HogpenViewPager.PIG_LIMIT);

			}
			// 添加猪圈返回逻辑
			else if (requestCode == RESULT_ADD_HOGPEN) {

				SellerHogpenInfo sellerHogpenInfo = (SellerHogpenInfo) intent
						.getSerializableExtra(IntentExtra.SELLER_HOGPEN_INFO);
				addHogpen(sellerHogpenInfo);
			}

		}

	}

	/**
	 * 添加猪圈
	 */
	private void addHogpen(SellerHogpenInfo sellerHogpenInfo) {
//		sellerHogpenInfo.hogpenID = hogpenID;
		birdIndicator.addIndicator();
		hogpenVp.addHogpen(sellerHogpenInfo);
		// 选中新添加的猪圈
		hogpenVp.setCurrentIndex(birdIndicator.getIndicatorNumber() - 1);
		// TODO:为何ViewPager的第一项刚添加的时候无法响应页面切换事件??
		if (birdIndicator.getIndicatorNumber() == 1) {
			birdIndicator.setCurrentIndex(0);
			setAddPigBtnVisibility(hogpenVp.getPigNumber()
					< HogpenViewPager.PIG_LIMIT && hogpenVp.getHogpenNumber() > 0);
		}
	}

	/**
	 * 请求卖家猪圈列表
	 */
	private void queryHogpensList() {
		LogUtils.i(TAG, "--查询卖家猪圈列表--");
		// 弹出提示
		showLoading(getString(R.string.prompt_loading));
		HogpenListRequest data = new HogpenListRequest();
		data.userID = userInfo.userID;
		HttpManager.postJson(this, UrlName.HOGPEN_LIST_FOR_BUYER.getUrl(), data,
				new JsonHttpResponseHandler<HogpenListResponse>(this) {
					/**
					 * 成功回调
					 *
					 * @param statusCode 状态码
					 * @param headers    Header
					 * @param jsonObj    服务端返回的对象
					 */
					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            final HogpenListResponse jsonObj) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								sellerHogpenInfos = jsonObj.data.hogpenInfos;
								int size = sellerHogpenInfos.size();
								// 猪圈个数不能超过最大个数限制
								if (size <= HogpenViewPager.HOGPEN_LIMIT) {
									// 添加初始鸟
									birdIndicator.setIndicators(size);
									LogUtils.i(TAG, "当前鸟个数：" + birdIndicator.getIndicatorNumber());
									// 初始化猪圈数据
									hogpenVp.setHogpenTabs(sellerHogpenInfos);
									// 让游标选中第一项,初始化猪圈的时候，页面选择事件是无效的？
									birdIndicator.setCurrentIndex(0);
									// 判断添加猪按钮是否显示
									// 初始的时候猪圈可能为0，所以不能添加猪
									setAddPigBtnVisibility(hogpenVp.getPigNumber()
											< HogpenViewPager.PIG_LIMIT
											&& hogpenVp.getHogpenNumber() > 0);
									// 动画显示卖家信息
									loadSellerInfoAnim();
								} else {
									ToastUtils.getInstance().showToast(getActivity(),
											R.string.prompt_hogpen_exceed_limit);
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

}
