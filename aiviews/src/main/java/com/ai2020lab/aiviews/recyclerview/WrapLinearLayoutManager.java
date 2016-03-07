package com.ai2020lab.aiviews.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ai2020lab.aiutils.common.LogUtils;

/**
 * 自定义高度自适应的LinearLayoutManager
 * Created by Justin on 2016/1/21.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class WrapLinearLayoutManager extends LinearLayoutManager {
	private final static String TAG = WrapLinearLayoutManager.class.getSimpleName();

	private int[] mMeasuredDimension = new int[2];

	public WrapLinearLayoutManager(Context context) {
		super(context);
	}

	public WrapLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public WrapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
	}

	/**
	 * Measure the attached RecyclerView. Implementations must call
	 * {@link #setMeasuredDimension(int, int)} before returning.
	 * <p/>
	 * <p>The default implementation will handle EXACTLY measurements and respect
	 * the minimum width and height properties of the host RecyclerView if measured
	 * as UNSPECIFIED. AT_MOST measurements will be treated as EXACTLY and the RecyclerView
	 * will consume all available space.</p>
	 *
	 * @param recycler   Recycler
	 * @param state      Transient state of RecyclerView
	 * @param widthSpec  Width {@link View.MeasureSpec}
	 * @param heightSpec Height {@link View.MeasureSpec}
	 */
	@Override
	public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
		final int widthMode = View.MeasureSpec.getMode(widthSpec);
		final int heightMode = View.MeasureSpec.getMode(heightSpec);
		final int widthSize = View.MeasureSpec.getSize(widthSpec);
		final int heightSize = View.MeasureSpec.getSize(heightSpec);
		LogUtils.i(TAG, "onMeasure called. \n widthMode " + widthMode
				+ " \n heightMode " + heightMode
				+ " \n widthSize " + widthSize
				+ " \n heightSize " + heightSize
				+ " \n getItemCount() " + getItemCount());
		int width = 0;
		int height = 0;
		for (int i = 0; i < getItemCount(); i++) {
			measureScrapChild(recycler, i,
					View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
					mMeasuredDimension);

			if (getOrientation() == HORIZONTAL) {
				width = width + mMeasuredDimension[0];
				if (i == 0) {
					height = mMeasuredDimension[1];
				}
			} else {
				height = height + mMeasuredDimension[1];
				if (i == 0) {
					width = mMeasuredDimension[0];
				}
			}
		}
		switch (widthMode) {
			case View.MeasureSpec.EXACTLY:
				width = widthSize;
			case View.MeasureSpec.AT_MOST:
			case View.MeasureSpec.UNSPECIFIED:
		}

		switch (heightMode) {
			case View.MeasureSpec.EXACTLY:
				height = heightSize;
			case View.MeasureSpec.AT_MOST:
			case View.MeasureSpec.UNSPECIFIED:
		}
		// 最后调用这个方法设置宽高
		setMeasuredDimension(width, height);

	}

	private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
	                               int heightSpec, int[] measuredDimension) {
		try {
			View view = recycler.getViewForPosition(0);//fix 动态添加时报IndexOutOfBoundsException
			if (view != null) {
				RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();

				int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
						getPaddingLeft() + getPaddingRight(), p.width);

				int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
						getPaddingTop() + getPaddingBottom(), p.height);

				view.measure(childWidthSpec, childHeightSpec);
				measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
				measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
				recycler.recycleView(view);
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "计算高度异常", e);
		}
	}

}
