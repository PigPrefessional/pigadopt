package com.ai2020lab.pigadopted.view;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.aiutils.system.DisplayUtils;
import com.ai2020lab.pigadopted.R;

/**
 * 自定义鸟游标
 * Created by Justin on 2016/3/10.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class BirdIndicator extends LinearLayout {
	/**
	 * 日志标题
	 */
	private final static String TAG = BirdIndicator.class.getSimpleName();
	/**
	 * 游标总个数
	 */
	private final static int INDICATOR_LIMIT = 3;
	/**
	 * 游标TAG的前缀
	 */
	private final static String INDICATOR_TAG_PREFIX = "indicator_tag_";
	/**
	 * 游标选中，反选动画执行时间
	 */
	private final static int INDICATOR_SELECTED_ANIM_DURATION = 500;
	private final static int INDICATOR_ADD_ANIM_DURATION = 500;
	/**
	 * 上下文引用
	 */
	private Context context;
	/**
	 * 鸟加 drawable
	 */
	private Drawable birdAddDrawable;
	/**
	 * 鸟1 drawable
	 */
	private Drawable birdOneDrawable;
	/**
	 * 鸟2 drawable
	 */
	private Drawable birdTwoDrawable;
	/**
	 * 鸟3 drawable
	 */
	private Drawable birdThreeDrawable;
	/**
	 * 游标容器
	 */
	private LinearLayout indicatorLayout;
	/**
	 * 添加鸟事件监听
	 */
	private OnClickAddListener onClickAddListener;
	/**
	 * 选择鸟事件监听
	 */
	private OnSelectListener onSelectListener;

	/**
	 * 当前的鸟总数，初始为0
	 */
	private int indicatorNumber;
	/**
	 * 当前游标的位置
	 */
	private int currentIndex = -2;

	public BirdIndicator(Context context) {
		super(context);
		init(context);
	}

	public BirdIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BirdIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	//初始化方法
	private void init(Context context) {
		this.context = context;
		initDrawables();
		ViewUtils.makeView(context, R.layout.bird_indicator_container, this, true);
		initIndicatorLayout();
	}

	/**
	 * 初始化所有的鸟Drawable资源
	 */
	private void initDrawables() {
		birdAddDrawable = ResourcesUtils.getDrawable(R.mipmap.bird_add);
		birdOneDrawable = ResourcesUtils.getDrawable(R.mipmap.bird_1);
		birdTwoDrawable = ResourcesUtils.getDrawable(R.mipmap.bird_2);
		birdThreeDrawable = ResourcesUtils.getDrawable(R.mipmap.bird_3);
	}

	/**
	 * 初始化鸟容器
	 */
	private void initIndicatorLayout() {
		indicatorLayout = (LinearLayout) findViewById(R.id.indicators_layout);
		LayoutParams lp = (LayoutParams) indicatorLayout.getLayoutParams();
		lp.width = LayoutParams.MATCH_PARENT;
		// 设置容器高度为固定，避免执行动画的时候超出边界
		lp.height = DisplayUtils.getScreenHeight(context) / 6;
		setGravity(Gravity.CENTER);
		indicatorLayout.setLayoutParams(lp);
		initLayoutTrasition();
	}

	/**
	 * 设置初始游标个数<p>
	 * 使用前必须先调用这个方法设置初始游标个数<br>
	 * 没有鸟的时候传0，游标中至少有一个加号鸟，最多不超过上限3个,
	 * 传3的时候加号鸟将被移除
	 *
	 * @param indicatorNumber 初始游标个数
	 */
	public void setIndicators(int indicatorNumber) {
		if (indicatorNumber < 0 || indicatorNumber > INDICATOR_LIMIT) {
			LogUtils.i(TAG, "初始化的鸟个数不能小于0或者大于最大鸟个数限制：" + INDICATOR_LIMIT);
			return;
		}
		// 初始化加入鸟游标
		while (this.indicatorNumber <= indicatorNumber) {
			LogUtils.i(TAG, "indicatorNumber: " + this.indicatorNumber);
			addIndicator();
		}
		// 默认都选中第一个鸟
		setCurrentIndex(0);
	}


	/**
	 * 绑定点击添加鸟事件
	 *
	 * @param onClickAddListener OnClickAddListener
	 */
	public void setOnClickAddListener(OnClickAddListener onClickAddListener) {
		this.onClickAddListener = onClickAddListener;
	}

	/**
	 * 绑定选择游标事件
	 *
	 * @param onSelectListener OnSelectListener
	 */
	public void setOnSelectListener(OnSelectListener onSelectListener) {
		this.onSelectListener = onSelectListener;
	}

	/**
	 * 获取当前的游标个数
	 *
	 * @return 返回当前游标个数
	 */
	public int getIndicatorNumber() {
		int birdNum = indicatorNumber - 1;
		if (birdNum < 0) {
			return 0;
		}
		return birdNum;
	}

	/**
	 * 加入单个游标
	 */
	public void addIndicator() {
		ImageView indicator = (ImageView) ViewUtils.makeView(context, R.layout.bird_indicator);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		lp.setMargins(40, 0, 40, 0);
		indicator.setLayoutParams(lp);
		switch (indicatorNumber) {
			case 0:
				LogUtils.i(TAG, "0个鸟的时候添加鸟加");
				indicator.setImageDrawable(birdAddDrawable);
				indicatorLayout.addView(indicator);
				loadAddAnim(indicator);
				indicator.setTag(INDICATOR_TAG_PREFIX + -1);
				// 绑定点击添加鸟游标事件
				indicator.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onClickAddListener != null) {
							LogUtils.i(TAG, "点击了添加鸟按钮");
							onClickAddListener.onClickAdd();
						}
					}
				});
				indicatorNumber++;
				break;
			case 1:
				LogUtils.i(TAG, "1个鸟的时候添加鸟1");
				indicator.setImageDrawable(birdOneDrawable);
				indicatorLayout.addView(indicator, 0);
				loadAddAnim(indicator);
				indicator.setTag(INDICATOR_TAG_PREFIX + 0);
				indicator.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onSelectListener != null) {
							LogUtils.i(TAG, "选择了鸟1");
							onSelectListener.onSelect(0);
							setCurrentIndex(0);
						}
					}
				});
				indicatorNumber++;
				// 添加鸟1的时候默认选中鸟1
				setCurrentIndex(0);
				break;
			case 2:
				LogUtils.i(TAG, "2个鸟的时候添加鸟2");
				indicator.setImageDrawable(birdTwoDrawable);
				indicatorLayout.addView(indicator, 1);
				loadAddAnim(indicator);
				indicator.setTag(INDICATOR_TAG_PREFIX + 1);
				indicator.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onSelectListener != null) {
							LogUtils.i(TAG, "选择了鸟2");
							onSelectListener.onSelect(1);
							setCurrentIndex(1);
						}
					}
				});
				indicatorNumber++;
				break;
			case 3:
				LogUtils.i(TAG, "3个鸟的时候先移除鸟加，再添加鸟3");
				indicatorLayout.removeViewAt(2);
				indicator.setImageDrawable(birdThreeDrawable);
				indicatorLayout.addView(indicator, 2);
				loadAddAnim(indicator);
				indicator.setTag(INDICATOR_TAG_PREFIX + 2);
				indicator.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onSelectListener != null) {
							LogUtils.i(TAG, "选择了鸟3");
							onSelectListener.onSelect(2);
							setCurrentIndex(2);
						}
					}
				});
				indicatorNumber++;
				break;
		}

	}

	/**
	 * 获取当前选中的游标位置
	 *
	 * @return 返回当前选中的游标位置，没有初始化游标或者没有游标的情况下将返回-2
	 */
	public int getCurrentIndex() {
		return currentIndex;
	}

	/**
	 * 设置当前选中游标的位置
	 *
	 * @param index 当前选中游标的位置，游标位置可以设置从0开始到游标总个数减1的整数
	 */
	public void setCurrentIndex(int index) {
//		currentIndex = index;
		int birdNum = getIndicatorNumber();
		if (birdNum == 0) {
			LogUtils.i(TAG, "请先初始化游标再设置选中游标的位置");
			return;
		}
		if (index < 0 || index > birdNum - 1) {
			LogUtils.i(TAG, "游标位置index越界");
			return;
		}
		if (currentIndex == index) {
			LogUtils.i(TAG, "要选中的位置:" + index + " 同当前位置:" + currentIndex + "相同");
			return;
		}
		// 找到当前已经选中的游标
		View currentSelectedView = indicatorLayout.findViewWithTag(INDICATOR_TAG_PREFIX + currentIndex);
		// 找到需要选中的游标
		View wonderSelectedView = indicatorLayout.findViewWithTag(INDICATOR_TAG_PREFIX + index);
		loadSelectAnim(wonderSelectedView);
		loadUnSelectAnim(currentSelectedView);
		// 刷新当前选中的游标位置
		currentIndex = index;
		// TODO:设置当前选中的游标

	}

	private void loadAddAnim(View view) {
		if (view == null) {
			LogUtils.i(TAG, "没有找到要执行动画的View");
			return;
		}
		Animation translateY = AnimationUtils.loadAnimation(context, R.anim.push_top_in);
		translateY.setDuration(INDICATOR_ADD_ANIM_DURATION);
		view.startAnimation(translateY);
	}

	// 选中游标执行动画
	private void loadSelectAnim(View view) {
		if (view == null) {
			LogUtils.i(TAG, "没有找到要执行动画的View");
			return;
		}
		// 沿Y轴旋转180度
		ObjectAnimator rotateY = ObjectAnimator.ofFloat(view, "rotationY", 0f, 180f);
		// x轴放大2倍
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.8f);
		// y轴放大2倍
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.8f);
		AnimatorSet animatorSet = new AnimatorSet();
//		animatorSet.play(rotateY).after(scaleX);
		animatorSet.play(scaleX).with(scaleY);
		animatorSet.setDuration(INDICATOR_SELECTED_ANIM_DURATION);
		animatorSet.setInterpolator(new BounceInterpolator());
		animatorSet.start();
	}

	// 取消选中游标执行动画
	private void loadUnSelectAnim(View view) {
		if (view == null) {
			LogUtils.i(TAG, "没有找到要执行动画的View");
			return;
		}
		// 沿Y轴旋转180度
		ObjectAnimator rotateY = ObjectAnimator.ofFloat(view, "rotationY", 180f, 0f);
		// x轴缩小2倍
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.8f, 1f);
		// y轴缩小2倍
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.8f, 1f);
		AnimatorSet animatorSet = new AnimatorSet();
		// 旋转操作完成后执行x,y轴的同时缩放
//		animatorSet.play(rotateY).after(scaleX);
		animatorSet.play(scaleX).with(scaleY);
		animatorSet.setDuration(INDICATOR_SELECTED_ANIM_DURATION);
		animatorSet.setInterpolator(new BounceInterpolator());
		animatorSet.start();
	}

	private void initLayoutTrasition() {
		LayoutTransition transition = new LayoutTransition();
		// 当View在容器中出现时，对View设置的动画
		transition.setAnimator(LayoutTransition.APPEARING,
				transition.getAnimator(LayoutTransition.APPEARING));
		// 当View在容器中出现时，对其他View位置造成影响，对其他View设置的动画
		transition.setAnimator(LayoutTransition.CHANGE_APPEARING,
				transition.getAnimator(LayoutTransition.CHANGE_APPEARING));
		// 当View在容器中消失时，对View设置的动画
		transition.setAnimator(LayoutTransition.DISAPPEARING,
				transition.getAnimator(LayoutTransition.DISAPPEARING));
		// 当View在容器中消失时，对其他View位置造成影响，对其他View设置的动画
		transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING,
				transition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING));
		indicatorLayout.setLayoutTransition(transition);
	}

	/**
	 * 点击添加鸟游标事件监听接口
	 */
	public interface OnClickAddListener {
		void onClickAdd();
	}

	/**
	 * 选择游标事件监听接口
	 */
	public interface OnSelectListener {
		/**
		 * 选中游标回调
		 *
		 * @param index 当前选中的游标位置
		 */
		void onSelect(int index);

	}


}
