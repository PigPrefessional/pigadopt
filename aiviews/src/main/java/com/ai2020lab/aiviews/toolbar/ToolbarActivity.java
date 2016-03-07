package com.ai2020lab.aiviews.toolbar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.R;
import com.ai2020lab.aiviews.rippleview.RippleView;

/**
 * 自定义ToolbarActivity,实现Toolbar的自定义封装
 * Created by Justin on 2015/12/10.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ToolbarActivity extends AppCompatActivity {

	private final static String TAG = ToolbarActivity.class.getSimpleName();

	private Activity activity;

	private Toolbar toolbar;

	private RelativeLayout contentContainer;

	private AppBarLayout toolbarContainer;

	private RippleView toolbarRightRipple;

	private RippleView toolbarLeftRipple;

	private OnLeftClickListener onLeftClickListener;

	private OnRightClickListener onRightClickListener;

	/**
	 * 程序入口
	 *
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * 获取Activity对象的引用
	 *
	 * @return 返回Activity对象的引用
	 */
	public Activity getActivity() {
		return activity;
	}

	// 初始化数据和界面布局
	private void init() {
		activity = this;
		super.setContentView(R.layout.activity_toolbar);
		contentContainer = (RelativeLayout) findViewById(R.id.activity_content);
		toolbarContainer = (AppBarLayout) findViewById(R.id.toolbar_layout);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
	}

	/**
	 * 获取工具栏View
	 *
	 * @return 返回工具栏View
	 */
	public Toolbar getToolbar() {
		return toolbar;
	}

	/**
	 * 获取工具栏是否显示
	 * @return true-显示，false-不显示
	 */
	public boolean getToolbarVisibility() {
		return contentContainer.getVisibility() == View.VISIBLE;
	}

	/**
	 * 设置contentView
	 *
	 * @param layoutResID contentView的布局文件
	 */
	public void setContentView(int layoutResID) {
		View contentView = ViewUtils.makeView(this, layoutResID);
		if (contentView == null) {
			throw new IllegalArgumentException("布局资源文件找不到");
		}
		contentContainer.addView(contentView);
	}

	/**
	 * 设置contentView
	 *
	 * @param view contentView对象
	 */
	public void setContentView(View view) {
		if (view == null) {
			throw new IllegalArgumentException("布局资源文件找不到");
		}
		contentContainer.addView(view);
	}

	/**
	 * 是否支持toolbar
	 *
	 * @param isSupport true-toolbar显示，false-toolbar不显示
	 */
	public void supportToolbar(boolean isSupport) {
		if (isSupport) {
			toolbarContainer.setVisibility(View.VISIBLE);
			intToolbar();
		} else {
			toolbarContainer.setVisibility(View.GONE);
		}
	}

	private void intToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		toolbarLeftRipple = (RippleView) findViewById(R.id.toolbar_left_ripple);
		toolbarRightRipple = (RippleView) findViewById(R.id.toolbar_right_ripple);
		toolbarLeftRipple.setVisibility(View.INVISIBLE);
		toolbarRightRipple.setVisibility(View.INVISIBLE);
		setSupportActionBar(toolbar);
		toolbarLeftRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
			@Override
			public void onComplete(RippleView rippleView) {
				if (onLeftClickListener != null)
					onLeftClickListener.onClick();
			}
		});
		toolbarRightRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
			@Override
			public void onComplete(RippleView rippleView) {
				if (onRightClickListener != null)
					onRightClickListener.onClick();
			}
		});
	}

	/**
	 * 设置toolbar标题
	 *
	 * @param title 标题的文字显示
	 */
	public void setToolbarTitle(String title) {
		TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
		toolbarTitle.setText(title);
	}

	/**
	 * 设置工具栏左边按钮图标
	 *
	 * @param drawableResID Drawable资源ID
	 */
	@SuppressWarnings("deprecation")
	public void setToolbarLeft(int drawableResID) {
		toolbarLeftRipple.setVisibility(View.VISIBLE);
		Drawable drawable = getResources().getDrawable(drawableResID);
		if (drawable == null) {
			throw new IllegalArgumentException("toolbar左边按钮Drawable资源文件找不到");
		}
		ImageView leftBtn = (ImageView) findViewById(R.id.toolbar_left_btn);
		leftBtn.setImageDrawable(drawable);
	}

	/**
	 * 设置工具栏右边按钮图标
	 *
	 * @param drawableResID Drawable资源ID
	 */
	@SuppressWarnings("deprecation")
	public void setToolbarRight(int drawableResID) {
		toolbarRightRipple.setVisibility(View.VISIBLE);
		Drawable drawable = getResources().getDrawable(drawableResID);
		if (drawable == null) {
			throw new IllegalArgumentException("toolbar右边按钮Drawable资源文件找不到");
		}
		ImageView rightBtn = (ImageView) findViewById(R.id.toolbar_right_btn);
		rightBtn.setImageDrawable(drawable);
	}

	/**
	 * Toolbar左边按钮点击监听
	 *
	 * @param onLeftClickListener OnLeftClickListener
	 */
	public void setOnLeftClickListener(final OnLeftClickListener onLeftClickListener) {
		this.onLeftClickListener = onLeftClickListener;
	}

	/**
	 * Toolbar右边按钮点击监听
	 *
	 * @param onRightClickListener OnRightClickListener
	 */
	public void setOnRightClickListener(final OnRightClickListener onRightClickListener) {
		this.onRightClickListener = onRightClickListener;
	}

	/**
	 * 设置工具栏标题
	 *
	 * @param title 标题文字
	 */
	public void setTitle(String title) {
		if (TextUtils.isEmpty(title))
			return;
		TextView titleView = (TextView) findViewById(R.id.toolbar_title);
		titleView.setText(title);
	}

	/**
	 * 导航栏左边按钮点击监听
	 */
	public interface OnLeftClickListener {
		void onClick();
	}

	/**
	 * 导航栏右边按钮点击监听
	 */
	public interface OnRightClickListener {
		void onClick();
	}


}
