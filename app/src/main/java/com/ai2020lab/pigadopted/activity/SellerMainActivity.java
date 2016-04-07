package com.ai2020lab.pigadopted.activity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aimedia.SysCameraManager;
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.image.ImageUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.DataManager;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.fragment.HogpenAddDialog;
import com.ai2020lab.pigadopted.fragment.OnClickDialogBtnListener;
import com.ai2020lab.pigadopted.fragment.PhotoSourceSelectDialog;
import com.ai2020lab.pigadopted.fragment.PigAddDialog;
import com.ai2020lab.pigadopted.fragment.PigAddSuccessDialog;
import com.ai2020lab.pigadopted.model.hogpen.HogpenAddRequest;
import com.ai2020lab.pigadopted.model.hogpen.HogpenAddResponse;
import com.ai2020lab.pigadopted.model.hogpen.HogpenListRequest;
import com.ai2020lab.pigadopted.model.hogpen.HogpenListResponse;
import com.ai2020lab.pigadopted.model.hogpen.SellerHogpenInfo;
import com.ai2020lab.pigadopted.model.pig.GrowthInfo;
import com.ai2020lab.pigadopted.model.pig.PigAddRequest;
import com.ai2020lab.pigadopted.model.pig.PigAddResponse;
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
	 * 打开系统相册选取图片requestCode
	 */
	public static int RESULT_PICK_IMAGE = 0x0001;
	/**
	 * 打开系统相机拍照并返回图片的requestCode
	 */
	public static int RESULT_TAKE_PICTURE = 0x0003;
	/**
	 * 添加猪圈对话框TAG
	 */
	private final static String TAG_DIALOG_ADD_HOGPEN = "tag_dialog_add_hogpen";
	/**
	 * 添加猪对话框TAG
	 */
	private final static String TAG_DIALOG_ADD_PIG = "tag_dialog_add_pig";
	/**
	 * 添加猪成功提示对话框TAG
	 */
	private final static String TAG_DIALOG_ADD_PIG_SUCCESS = "tag_dialog_add_pig_success";
	/**
	 * 选取照片来源对话框TAG
	 */
	private final static String TAG_DIALOG_SELECT_PHOTO_SOURCE = "tag_dialog_select_photo_source";

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
				LogUtils.i(TAG, "添加鸟游标和猪圈");
				showAddHogpenDialog();
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
			public void onEnd() {
				// 猪添加动画执行完毕弹出提示对话框
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


	private HogpenAddDialog hogpenDialog;
	private PhotoSourceSelectDialog photoSourceSelectDialog;

	/**
	 * 弹出照片来源选择对话框
	 */
	private void showPhotoSourceSelectDialog() {
		photoSourceSelectDialog = PhotoSourceSelectDialog.newInstance(true,
				onClickPhotoSelectListener);
		Fragment fragment = getFragmentManager().findFragmentByTag(TAG_DIALOG_SELECT_PHOTO_SOURCE);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragment != null)
			ft.remove(fragment);
		ft.addToBackStack(null);// 加入回退栈
		photoSourceSelectDialog.show(ft, TAG_DIALOG_SELECT_PHOTO_SOURCE);
	}

	/**
	 * 照片来源选择对话框,选择相册，选择照相事件监听
	 */
	private PhotoSourceSelectDialog.OnClickPhotoSelectListener onClickPhotoSelectListener =
			new PhotoSourceSelectDialog.OnClickPhotoSelectListener() {
				@Override
				public void onSelectAlbum(DialogFragment df) {
					LogUtils.i(TAG, "打开系统相册");
					df.dismiss();
					ImageUtils.pickImageFromAlbum(getActivity(), RESULT_PICK_IMAGE);
				}

				@Override
				public void onSelectCamera(DialogFragment df) {
					LogUtils.i(TAG, "打开系统相机拍照");
					df.dismiss();
					SysCameraManager.getInstance().openCamera(getActivity(), RESULT_TAKE_PICTURE);
				}
			};

	/**
	 * 弹出添加猪圈对话框
	 */
	private void showAddHogpenDialog() {
		hogpenDialog = HogpenAddDialog.newInstance(true, onClickHogpenAddListener,
				onClickHogpenPhotoIvListener);
		Fragment fragment = getFragmentManager().findFragmentByTag(TAG_DIALOG_ADD_HOGPEN);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragment != null)
			ft.remove(fragment);
		ft.addToBackStack(null);// 加入回退栈
		hogpenDialog.show(ft, TAG_DIALOG_ADD_HOGPEN);
	}

	/**
	 * 添加猪圈对话框猪圈照片点击监听
	 */
	private HogpenAddDialog.OnClickHogpenPhotoIvListener onClickHogpenPhotoIvListener =
			new HogpenAddDialog.OnClickHogpenPhotoIvListener() {
				@Override
				public void onClickPhotoIv() {
					// 弹出照片来源选择，不关闭添加猪圈对话框
					showPhotoSourceSelectDialog();
				}
			};
	/**
	 * 添加猪圈对话框确定，取消按钮事件监听
	 */
	private OnClickDialogBtnListener<SellerHogpenInfo> onClickHogpenAddListener =
			new OnClickDialogBtnListener<SellerHogpenInfo>() {
				@Override
				public void onClickEnsure(DialogFragment df, SellerHogpenInfo hogpenInfo) {
					df.dismiss();
//					//发起添加猪圈请求
					requestAddHogpen(hogpenInfo);
				}

				@Override
				public void onClickCancel(DialogFragment df) {
					df.dismiss();
				}
			};

	/**
	 * 弹出添加猪对话框
	 */
	private void showAddPigDialog() {
		PigAddDialog pigDialog = PigAddDialog.newInstance(true, onClickPigAddListener);
		Fragment fragment = getFragmentManager().findFragmentByTag(TAG_DIALOG_ADD_PIG);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragment != null)
			ft.remove(fragment);
		ft.addToBackStack(null);
		pigDialog.show(ft, TAG_DIALOG_ADD_PIG);
	}

	// 添加猪对话框点击监听
	private OnClickDialogBtnListener<PigInfo> onClickPigAddListener =
			new OnClickDialogBtnListener<PigInfo>() {
				@Override
				public void onClickEnsure(DialogFragment df, PigInfo pigInfo) {
					df.dismiss();
					//添加猪
					requestAddPig(pigInfo);
				}

				@Override
				public void onClickCancel(DialogFragment df) {
					df.dismiss();
				}
			};

	/**
	 * 弹出添加猪成功提示对话框
	 */
	private void showAddPigSuccessDialog() {
		PigAddSuccessDialog dialog = PigAddSuccessDialog.newInstance(true, onClickPigAddSuccessListener);
		Fragment fragment = getFragmentManager().findFragmentByTag(TAG_DIALOG_ADD_PIG_SUCCESS);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragment != null)
			ft.remove(fragment);
		ft.addToBackStack(null);
		dialog.show(ft, TAG_DIALOG_ADD_PIG_SUCCESS);
	}

	// 添加猪成功对话框按钮点击监听
	private OnClickDialogBtnListener<Void> onClickPigAddSuccessListener =
			new OnClickDialogBtnListener<Void>() {
				@Override
				public void onClickEnsure(DialogFragment df, Void aVoid) {
					LogUtils.i(TAG, "跳转到猪拍照界面");
					df.dismiss();
				}

				@Override
				public void onClickCancel(DialogFragment df) {
					LogUtils.i(TAG, "残忍拒绝");
					df.dismiss();
				}
			};

	/**
	 * 处理相册选图和系统相机拍照返回图片逻辑
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			// 处理相册返回照片
			if (requestCode == RESULT_PICK_IMAGE && intent != null) {
				String photoPath = ImageUtils.getPickedImagePath(this, intent.getData());
				if (TextUtils.isEmpty(photoPath)) {
					ToastUtils.getInstance().showToast(getActivity(),
							R.string.prompt_select_hogpen_photo_again);
					return;
				}
				// 设置猪圈照片
				hogpenDialog.setHogpenPhotoPath(photoPath);
			}
			// 处理拍照返回
			else if (requestCode == RESULT_TAKE_PICTURE) {
				hogpenDialog.setHogpenPhotoPath(SysCameraManager.getInstance().getPhotoPath());
//				SysCameraManager.getInstance().savePhoto();
//				SysCameraManager.getInstance().setOnPhotoSavedListener(
//						new SysCameraManager.OnPhotoSavedListener() {
//							@Override
//							public void onPhotoSaved(String photoPath) {
//								LogUtils.i(TAG, "拍照照片路径->" + photoPath);
//								if (TextUtils.isEmpty(photoPath)) {
//									ToastUtils.getInstance().showToast(getActivity(),
//											R.string.prompt_take_hogpen_photo_again);
//									return;
//								}
//
//							}
//						});
			}
		}

	}

	/**
	 * 添加猪圈
	 */
	private void addHogpen(SellerHogpenInfo sellerHogpenInfo) {
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

	/**
	 * 添加猪圈
	 */
	private void requestAddHogpen(SellerHogpenInfo sellerHogpenInfo) {
		LogUtils.i(TAG, "--添加猪圈请求--");
		// 弹出提示
		showLoading(getString(R.string.prompt_add_loading));
		HogpenAddRequest data = new HogpenAddRequest();
		data.hogpenLength = sellerHogpenInfo.hogpenLength;
		data.hogpenWidth = sellerHogpenInfo.hogpenWidth;
		data.userID = DataManager.getInstance().getSellerInfo().userID;
		data.hogpenName = "";
		HttpManager.postFile(this, UrlName.ADD_HOGPEN.getUrl(),
				data, sellerHogpenInfo.hogpenPhoto,
				new JsonHttpResponseHandler<HogpenAddResponse>(this) {

					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            HogpenAddResponse jsonObj) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								LogUtils.i(TAG, "--添加猪圈成功--");
//								addHogpen(hogpenInfo);
							}
						}, 1000);
					}

					@Override
					public void onCancel() {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								// 没有网络的情况会终止请求
								ToastUtils.getInstance().showToast(getActivity(),
										R.string.prompt_add_hogpen_failure);
							}
						}, 1000);
					}

					@Override
					public void onHandleFailure(String errorMsg) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								ToastUtils.getInstance().showToast(getActivity(),
										R.string.prompt_add_hogpen_failure);
							}
						}, 1000);
					}

				});

	}

	/**
	 * 发起添加猪请求
	 */
	private void requestAddPig(final PigInfo pigInfo) {
		LogUtils.i(TAG, "--添加猪请求--");
		// 弹出提示
		showLoading(getString(R.string.prompt_add_loading));
		PigAddRequest data = new PigAddRequest();
		data.categoryID = pigInfo.pigCategory.categoryID;
		data.hogpenID = hogpenVp.getHogpen(hogpenVp.getCurrentIndex()).hogpenID;
		// 时间戳除以1000，服务端长度溢出
		data.attendedTime = pigInfo.attendedTime / 1000l;
		data.attendedWeight = pigInfo.attendedWeight;
		data.attendedAge = pigInfo.attendedAge;
		HttpManager.postJson(this, UrlName.ADD_PIG.getUrl(), data,
				new JsonHttpResponseHandler<PigAddResponse>(this) {
					/**
					 * 成功回调
					 *
					 * @param statusCode 状态码
					 * @param headers    Header
					 * @param jsonObj    服务端返回的对象
					 */
					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            final PigAddResponse jsonObj) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								hogpenVp.addPig(getAddPigInfoAndOrder(pigInfo));
								setAddPigBtnVisibility(hogpenVp.getPigNumber()
										< HogpenViewPager.PIG_LIMIT);
							}
						}, 1000);

					}

					@Override
					public void onCancel() {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								// 没有网络的情况会终止请求
								ToastUtils.getInstance().showToast(getActivity(),
										R.string.prompt_add_pig_failure);
							}
						}, 1000);
					}

					@Override
					public void onHandleFailure(String errorMsg) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								ToastUtils.getInstance().showToast(getActivity(),
										R.string.prompt_add_pig_failure);
							}
						}, 1000);
					}
				});
	}

	/**
	 * 返回添加猪测试数据
	 */
	private PigDetailInfoAndOrder getAddPigInfoAndOrder(PigInfo pigInfo) {
		PigDetailInfoAndOrder pigInfoAndOrder = new PigDetailInfoAndOrder();
//		pigInfoAndOrder.orderInfo = new OrderInfoForSeller();
//		pigInfoAndOrder.orderInfo.buyerNumber = 0;
//		pigInfoAndOrder.healthInfo = new HealthInfo();
//		pigInfoAndOrder.healthInfo.temperature = 36.5f;
//		pigInfoAndOrder.healthInfo.status = PigStatus.WALKING;
		pigInfoAndOrder.growthInfo = new GrowthInfo();
		pigInfoAndOrder.growthInfo.pigWeight = pigInfo.attendedWeight;
		pigInfoAndOrder.pigInfo = pigInfo;
		return pigInfoAndOrder;
	}

//	/**
//	 * 返回初始化猪圈测试数据
//	 */
//	private List<SellerHogpenInfo> getHogpenInfos() {
//		List<SellerHogpenInfo> hogpenInfos = new ArrayList<>();
//		SellerHogpenInfo hogpenInfo;
//		hogpenInfo = new SellerHogpenInfo();
//		hogpenInfo.hogpenID = 1;
//		hogpenInfo.hogpenWidth = 3;
//		hogpenInfo.hogpenLength = 4;
//		hogpenInfo.pigInfos = new ArrayList<>();
//		// 构造猪数据
//		PigDetailInfoAndOrder pigDetail;
//		pigDetail = new PigDetailInfoAndOrder();
//		pigDetail.growthInfo = new GrowthInfo();
//		pigDetail.growthInfo.pigWeight = 180;
//		pigDetail.healthInfo = new HealthInfo();
//		pigDetail.healthInfo.status = PigStatus.EATING;
//		pigDetail.healthInfo.temperature = 37.5f;
//		pigDetail.orderInfo = new OrderInfoForSeller();
//		pigDetail.orderInfo.buyerNumber = 8;
//		hogpenInfo.pigInfos.add(pigDetail);
//		//
//		pigDetail = new PigDetailInfoAndOrder();
//		pigDetail.growthInfo = new GrowthInfo();
//		pigDetail.growthInfo.pigWeight = 180;
//		pigDetail.healthInfo = new HealthInfo();
//		pigDetail.healthInfo.status = PigStatus.EATING;
//		pigDetail.healthInfo.temperature = 37.5f;
//		pigDetail.orderInfo = new OrderInfoForSeller();
//		pigDetail.orderInfo.buyerNumber = 8;
//		hogpenInfo.pigInfos.add(pigDetail);
//
//		hogpenInfos.add(hogpenInfo);
//
//
//		return hogpenInfos;
//	}


}
