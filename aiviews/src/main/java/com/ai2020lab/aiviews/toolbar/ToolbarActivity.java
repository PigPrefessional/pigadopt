package com.ai2020lab.aiviews.toolbar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiviews.R;

/**
 * 自定义ToolbarActivity,实现Toolbar的自定义封装
 * Created by Justin on 2015/12/10.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ToolbarActivity extends AppCompatActivity {

	private final static String TAG = ToolbarActivity.class.getSimpleName();

	private Activity activity;

	private Toolbar toolbar;

	private FrameLayout contentContainer;

	private AppBarLayout toolbarContainer;

	private TextView toolbarTitle;
	private ImageView toolbarLeftIv;
	private ImageView toolbarRightIv;
	private TextView toolbarRightTv;

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
		contentContainer = (FrameLayout) findViewById(R.id.activity_content);
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
	 *
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
			toolbar.setVisibility(View.VISIBLE);
			intToolbar();
		} else {
			toolbarContainer.setVisibility(View.GONE);
			toolbar.setVisibility(View.GONE);
		}
	}

	private void intToolbar() {
		toolbar.setTitle("");
		toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
		toolbarLeftIv = (ImageView) findViewById(R.id.toolbar_left_iv);
		toolbarRightIv = (ImageView) findViewById(R.id.toolbar_right_iv);
		toolbarRightTv = (TextView) findViewById(R.id.toolbar_right_tv);
		toolbarLeftIv.setVisibility(View.GONE);
		toolbarRightIv.setVisibility(View.GONE);
		toolbarRightTv.setVisibility(View.GONE);
		setSupportActionBar(toolbar);
		toolbarLeftIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onLeftClickListener != null)
					onLeftClickListener.onClick();
			}
		});
		toolbarRightIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onRightClickListener != null)
					onRightClickListener.onClick();
			}
		});
		toolbarRightTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
		toolbarTitle.getPaint().setFakeBoldText(true);
		toolbarTitle.setText(title);
	}

	/**
	 * 设置工具栏左边按钮图标
	 *
	 * @param drawableResID Drawable资源ID
	 */
	@SuppressWarnings("deprecation")
	public void setToolbarLeft(int drawableResID) {
		toolbarLeftIv.setVisibility(View.VISIBLE);
		Drawable drawable = ResourcesUtils.getDrawable(drawableResID);
		if (drawable == null) {
			throw new IllegalArgumentException("toolbar左边按钮Drawable资源文件找不到");
		}
		toolbarLeftIv.setImageDrawable(drawable);
	}

	/**
	 * 设置工具栏左边按钮的尺寸
	 *
	 * @param width  宽度
	 * @param height 高度
	 */
	public void setToolbarLeftDemension(int width, int height) {
		ViewGroup.LayoutParams lp = toolbarLeftIv.getLayoutParams();
		if (width > -2) {
			lp.width = width;
		}
		if (height > -2) {
			lp.height = height;
		}
		toolbarLeftIv.setLayoutParams(lp);
	}

	/**
	 * 设置工具栏右边按钮图标
	 *
	 * @param drawableResID Drawable资源ID
	 */
	public void setToolbarRight(int drawableResID) {
		toolbarRightTv.setVisibility(View.GONE);
		toolbarRightIv.setVisibility(View.VISIBLE);
		Drawable drawable = ResourcesUtils.getDrawable(drawableResID);
		if (drawable == null) {
			throw new IllegalArgumentException("toolbar右边按钮Drawable资源文件找不到");
		}
		toolbarRightIv.setImageDrawable(drawable);
	}

	/**
	 * 设置工具栏右边文字按钮的文字
	 *
	 * @param rightText 文字按钮的文字
	 */
	public void setToolbarRight(CharSequence rightText) {
		toolbarRightTv.setVisibility(View.VISIBLE);
		toolbarRightIv.setVisibility(View.GONE);
		toolbarRightTv.setText(rightText);
		toolbarRightTv.getPaint().setFakeBoldText(true);
	}

	/**
	 * 设置工具栏右边文字按钮的文字
	 *
	 * @param rightText 文字按钮的文字
	 * @param color     文字颜色
	 */
	public void setToolbarRight(CharSequence rightText, int color) {
		toolbarRightTv.setVisibility(View.VISIBLE);
		toolbarRightIv.setVisibility(View.GONE);
		toolbarRightTv.setText(rightText);
		toolbarRightTv.setTextColor(color);
		toolbarRightTv.getPaint().setFakeBoldText(true);
	}

	/**
	 * 设置工具栏右边按钮的尺寸
	 *
	 * @param width  宽度
	 * @param height 高度
	 */
	public void setToolbarRightDemension(int width, int height) {
		if (toolbarRightIv.getVisibility() == View.VISIBLE) {
			ViewGroup.LayoutParams lp = toolbarRightIv.getLayoutParams();
			if (width > -2) {
				lp.width = width;
			}
			if (height > -2) {
				lp.height = height;
			}
			toolbarRightIv.setLayoutParams(lp);
		} else if (toolbarRightTv.getVisibility() == View.VISIBLE) {
			ViewGroup.LayoutParams lp = toolbarRightTv.getLayoutParams();
			if (width > -2) {
				lp.width = width;
			}
			if (height > -2) {
				lp.height = height;
			}
			toolbarRightTv.setLayoutParams(lp);
		}
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
