/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.TimeUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.aiviews.dialog.BaseDialog;
import com.ai2020lab.aiviews.wheelview.WheelView;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.common.CommonUtils;
import com.ai2020lab.pigadopted.common.DataManager;
import com.ai2020lab.pigadopted.model.pig.PigCategory;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.view.pickerview.DatePickerView;
import com.ai2020lab.pigadopted.view.pickerview.PigAgePickerView;
import com.ai2020lab.pigadopted.view.pickerview.PigWeightPickerView;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;

import java.util.ArrayList;

/**
 * 添加猪对话框
 * Created by Justin Z on 2016/3/18.
 * 502953057@qq.com
 */
public class PigAddDialog extends DialogFragment {
	private final static String TAG = PigAddDialog.class.getSimpleName();

	private OnClickDialogBtnListener<PigInfo> onClickDialogBtnListener;

	private boolean loadAnim = false;

	private ImageView addPigTitleIv;
	private TextView pigAddCategoryTv;
	private TextView pigAddAgeTv;
	private TextView pigAddWeightTv;
	private TextView pigAddTimeTv;
	private Button cancelBtn;
	private Button ensureBtn;
	private WheelView pigAddCategoryWv;
	private PigAgePickerView pigAddAgePv;
	private PigWeightPickerView pigAddWeightPv;
	private ArrayWheelAdapter<PigCategory> pigCategoryWvAdapter;
	private DatePickerView pigAddDatePv;

	/**
	 * 创建对话框方法
	 *
	 * @param loadAnim                 是否加载动画
	 * @param onClickDialogBtnListener 点击按钮事件监听
	 * @return PigAddDialog
	 */
	public static PigAddDialog newInstance(boolean loadAnim,
	                                       OnClickDialogBtnListener<PigInfo> onClickDialogBtnListener) {
		PigAddDialog addPigFragment = new PigAddDialog();
		addPigFragment.onClickDialogBtnListener = onClickDialogBtnListener;
		addPigFragment.loadAnim = loadAnim;
		return addPigFragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		long startTime = System.currentTimeMillis();
		View contentView = ViewUtils.makeView(getActivity(), R.layout.dialog_add_pig);
		BaseDialog dialog = createDialog(contentView);
		assignViews(contentView);
		setTextFonts();
		setPigAddCategoryWv();
		setPigAddDatePv();
		setDialogBtnClickListener();
		long endTime = System.currentTimeMillis();
		LogUtils.i(TAG, "消耗时间：" + (endTime - startTime) / 1000 + "秒");
		return dialog;
	}

	/**
	 * 创建Dialog
	 */
	private BaseDialog createDialog(View contentView) {
		BaseDialog.Builder builder = new BaseDialog.Builder(getActivity(), contentView);
		builder.setWidth(DisplayUtils.getScreenWidth(getActivity()));
		builder.setHeight(DisplayUtils.getScreenHeight(getActivity()));
		builder.setGravity(Gravity.CENTER);
		builder.setStyle(R.style.BaseAlertDialog);
		if (loadAnim)
			builder.setAnimStyle(R.style.windowAnimScale);
		BaseDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	/**
	 * 分配各个View
	 */
	private void assignViews(View contentView) {
		addPigTitleIv = (ImageView) contentView.findViewById(R.id.add_pig_title_iv);
		pigAddCategoryTv = (TextView) contentView.findViewById(R.id.pig_add_category_tv);
		pigAddAgeTv = (TextView) contentView.findViewById(R.id.pig_add_age_tv);
		pigAddWeightTv = (TextView) contentView.findViewById(R.id.pig_add_weight_tv);
		pigAddCategoryWv = (WheelView) contentView.findViewById(R.id.pig_add_category_wv);
		pigAddAgePv = (PigAgePickerView) contentView.findViewById(R.id.pig_add_age_pv);
		pigAddWeightPv = (PigWeightPickerView) contentView.findViewById(R.id.pig_add_weight_pv);
		pigAddTimeTv = (TextView) contentView.findViewById(R.id.pig_add_time_tv);
		pigAddDatePv = (DatePickerView) contentView.findViewById(R.id.pig_add_date_pv);
		cancelBtn = (Button) contentView.findViewById(R.id.cancel_btn);
		ensureBtn = (Button) contentView.findViewById(R.id.ensure_btn);
	}

	/**
	 * 设置字体
	 */
	private void setTextFonts() {
		// 标题和按钮文字全部使用中文粗体
		pigAddCategoryTv.getPaint().setFakeBoldText(true);
		pigAddAgeTv.getPaint().setFakeBoldText(true);
		pigAddWeightTv.getPaint().setFakeBoldText(true);
		pigAddTimeTv.getPaint().setFakeBoldText(true);
		cancelBtn.getPaint().setFakeBoldText(true);
		ensureBtn.getPaint().setFakeBoldText(true);
	}

	/**
	 * 设置猪品种选择滚轮
	 */
	private void setPigAddCategoryWv() {
		ArrayList<PigCategory> pigCategories = DataManager.getInstance().getPigCategories();
		pigCategoryWvAdapter = new ArrayWheelAdapter<>(pigCategories, 2);
		pigAddCategoryWv.setAdapter(pigCategoryWvAdapter);
		// 设置选中第一项
		pigAddCategoryWv.setCurrentItem(0);
//		pigAddCategoryWv.setCyclic(false);
	}

	/**
	 * 设置时间选择为当前时间
	 */
	private void setPigAddDatePv() {
		int year = CommonUtils.getCurrentYear();
		int month = CommonUtils.getCurrentMonth();
		int day = CommonUtils.getCurrentDay();
		LogUtils.i(TAG, "年份:" + year);
		LogUtils.i(TAG, "月份:" + month);
		LogUtils.i(TAG, "日期:" + day);
		pigAddDatePv.setPickerView(year, month, day);
	}

	/**
	 * 返回添加的猪信息对象
	 */
	private PigInfo getSelectPigInfo() {
		PigInfo pigInfo = new PigInfo();
		// 猪品种
		pigInfo.pigCategory = (PigCategory) pigCategoryWvAdapter
				.getItem(pigAddCategoryWv.getCurrentItem());
		// 入栏猪龄
		pigInfo.attendedAge = pigAddAgePv.getSelectPigAge();
		// 入栏体重
		pigInfo.attendedWeight = pigAddWeightPv.getSelectPigWeight();
		// 入栏时间
		pigInfo.attendedDate = pigAddDatePv.getSelectTime();
		pigInfo.attendedTime = TimeUtils.dateToTimeStamp(pigInfo.attendedDate,
				TimeUtils.Template.YMD);
//		LogUtils.i(TAG, "入栏品种:" + pigInfo.pigCategory.toString());
//		LogUtils.i(TAG, "入栏猪龄:" + pigInfo.attendedAge);
//		LogUtils.i(TAG, "入栏体重:" + pigInfo.attendedWeight);
//		LogUtils.i(TAG, "入栏时间:" + pigInfo.attendedDate);
//		LogUtils.i(TAG, "入栏时间戳:" + pigInfo.attendedTime);
//		LogUtils.i(TAG, "转换回来:" + TimeUtils.formatTimeStamp(pigInfo.attendedTime,
//				TimeUtils.Template.YMD));
		return pigInfo;
	}

	/**
	 * 绑定按钮事件
	 */
	private void setDialogBtnClickListener() {
		ensureBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onClickDialogBtnListener != null)
					onClickDialogBtnListener.onClickEnsure(PigAddDialog.this, getSelectPigInfo());
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onClickDialogBtnListener != null)
					onClickDialogBtnListener.onClickCancel(PigAddDialog.this);

			}
		});
	}


}
