/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aimedia.SysCameraManager;
import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.aiutils.image.ImageUtils;
import com.ai2020lab.aiutils.thread.ThreadUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.DataManager;
import com.ai2020lab.pigadopted.common.IntentExtra;
import com.ai2020lab.pigadopted.fragment.PhotoSourceSelectDialog;
import com.ai2020lab.pigadopted.model.hogpen.HogpenAddRequest;
import com.ai2020lab.pigadopted.model.hogpen.HogpenAddResponse;
import com.ai2020lab.pigadopted.model.hogpen.SellerHogpenInfo;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;
import com.ai2020lab.pigadopted.view.pickerview.DimensionPickerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import cz.msebera.android.httpclient.Header;

/**
 * 添加猪圈界面
 * Created by Justin Z on 2016/4/15.
 * 502953057@qq.com
 */
public class AddHogpenActivity extends AIBaseActivity {
	/**
	 * 日志标题
	 */
	private final static String TAG = BuyerMainActivity.class.getSimpleName();
	/**
	 * 打开系统相册选取图片requestCode
	 */
	public final static int RESULT_PICK_IMAGE = 0x0001;
	/**
	 * 打开系统相机拍照并返回图片的requestCode
	 */
	public final static int RESULT_TAKE_PICTURE = 0x0003;
	/**
	 * 选取照片来源对话框TAG
	 */
	private final static String TAG_DIALOG_SELECT_PHOTO_SOURCE = "tag_dialog_select_photo_source";

	private ImageView hogpenPhotoIv;
	private TextView hogpenPhotoTv;
	private TextView hogpenLengthTv;
	private DimensionPickerView hogpenLengthPv;
	private TextView hogpenWidthTv;
	private DimensionPickerView hogpenWidthPv;

	/**
	 * 猪圈照片保存路径
	 */
	private String hogpenPhotoPath;
	/**
	 * 返回的卖家猪圈对象数据
	 */
	private SellerHogpenInfo sellerHogpenInfo;

	/**
	 * 入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_hogpen);
		setToolbar();
		assignViews();
		setTextFonts();
		setPhotoIvClickListener();
	}

	private void setToolbar() {
		supportToolbar(true);
		setToolbarTitle(getString(R.string.activity_title_add_hogpen));
		setToolbarLeft(R.drawable.toolbar_back_selector);
		setToolbarRight(getString(R.string.ensure));
		setOnLeftClickListener(new OnLeftClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});
		setOnRightClickListener(new OnRightClickListener() {
			@Override
			public void onClick() {
				// 确定发起添加猪圈请求
				requestAddHogpen();
			}
		});
	}

	private void assignViews() {
		hogpenPhotoIv = (ImageView) findViewById(R.id.hogpen_photo_iv);
		hogpenPhotoTv = (TextView) findViewById(R.id.hogpen_photo_tv);
		hogpenLengthTv = (TextView) findViewById(R.id.hogpen_length_tv);
		hogpenLengthPv = (DimensionPickerView) findViewById(R.id.hogpen_length_pv);
		hogpenWidthTv = (TextView) findViewById(R.id.hogpen_width_tv);
		hogpenWidthPv = (DimensionPickerView) findViewById(R.id.hogpen_width_pv);
	}

	private void setTextFonts() {
		// 标题和按钮文字全部使用中文粗体
		hogpenLengthTv.getPaint().setFakeBoldText(true);
		hogpenWidthTv.getPaint().setFakeBoldText(true);
		hogpenPhotoTv.getPaint().setFakeBoldText(true);
	}

	/**
	 * 绑定照片点击选择监听
	 */
	private void setPhotoIvClickListener() {
		hogpenPhotoIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 弹出照片来源选择对话框
				showPhotoSourceSelectDialog();
			}
		});
	}

	/**
	 * 弹出照片来源选择对话框
	 */
	private void showPhotoSourceSelectDialog() {
		PhotoSourceSelectDialog photoSourceSelectDialog = PhotoSourceSelectDialog.newInstance(true,
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
	 * 添加猪圈请求
	 */
	private void requestAddHogpen() {
		LogUtils.i(TAG, "--添加猪圈请求--");
		sellerHogpenInfo = getHogpenInfo();
		if (sellerHogpenInfo == null) {
			return;
		}
		HogpenAddRequest data = new HogpenAddRequest();
		data.hogpenLength = sellerHogpenInfo.hogpenLength;
		data.hogpenWidth = sellerHogpenInfo.hogpenWidth;
		data.userID = DataManager.getInstance().getSellerInfo().userID;
		LogUtils.i(TAG, "卖家用户id-->" + data.userID);
		data.hogpenName = "";
		// 弹出提示
		showLoading(getString(R.string.prompt_add_loading));
		HttpManager.postFile(this, UrlName.ADD_HOGPEN.getUrl(),
				data, sellerHogpenInfo.hogpenPhoto,
				new JsonHttpResponseHandler<HogpenAddResponse>(this) {

					@Override
					public void onHandleSuccess(int statusCode, Header[] headers,
					                            final HogpenAddResponse jsonObj) {
						ThreadUtils.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								dismissLoading();
								LogUtils.i(TAG, "--添加猪圈成功--");
								// 给猪圈ID赋值并返回到卖家主页去
								sellerHogpenInfo.hogpenID = jsonObj.data.hogpenID;
								finishActivity(sellerHogpenInfo);
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
	 * 将添加的猪圈数据返回给卖家主页
	 */
	private void finishActivity(SellerHogpenInfo sellerHogpenInfo) {
		Intent intent = new Intent();
		intent.putExtra(IntentExtra.SELLER_HOGPEN_INFO, sellerHogpenInfo);
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 组合上传猪圈数据
	 */
	private SellerHogpenInfo getHogpenInfo() {
		SellerHogpenInfo sellerHogpenInfo = new SellerHogpenInfo();
		// 宽度
		sellerHogpenInfo.hogpenWidth = hogpenLengthPv.getSelectDimension();
		// 高度
		sellerHogpenInfo.hogpenLength = hogpenWidthPv.getSelectDimension();
//		selectHogpenInfo.pigInfos = new ArrayList<>();
		LogUtils.i(TAG, "选择的猪圈宽度-->" + sellerHogpenInfo.hogpenWidth);
		LogUtils.i(TAG, "选择的猪圈长度-->" + sellerHogpenInfo.hogpenLength);
		if (TextUtils.isEmpty(hogpenPhotoPath)) {
			ToastUtils.getInstance().showToast(getActivity(), R.string.prompt_select_hogpen_photo);
			return null;
		}
		sellerHogpenInfo.hogpenPhoto = hogpenPhotoPath;
		return sellerHogpenInfo;
	}

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
				setHogpenPhotoPath(photoPath);
			}
			// 处理拍照返回
			else if (requestCode == RESULT_TAKE_PICTURE) {
				setHogpenPhotoPath(SysCameraManager.getInstance().getPhotoPath());
			}
		}

	}

	/**
	 * 显示相册选择或者拍摄返回的猪圈照片
	 */
	public void setHogpenPhotoPath(String path) {
		LogUtils.i(TAG, "--设置猪圈图片");
		hogpenPhotoPath = path;
		hogpenPhotoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ImageLoader.getInstance().displayImage("file://" + path, hogpenPhotoIv);
	}


}
