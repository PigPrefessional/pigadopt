/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aiviews.recyclerview.animator;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;

/**
 * Created by Justin Z on 2016/3/23.
 * 502953057@qq.com
 */
public class FlipInTopXAnimator extends BaseItemAnimator {
	public FlipInTopXAnimator() {
	}

	public FlipInTopXAnimator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}

	@Override
	protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
		ViewCompat.animate(holder.itemView)
				.rotationX(90)
				.setDuration(getRemoveDuration())
				.setInterpolator(mInterpolator)
				.setListener(new BaseItemAnimator.DefaultRemoveVpaListener(holder))
				.setStartDelay(getRemoveDelay(holder))
				.start();
	}

	@Override
	protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
		ViewCompat.setRotationX(holder.itemView, 90);
	}

	@Override
	protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
		holder.itemView.setPivotX(holder.itemView.getWidth() / 2);
		holder.itemView.setPivotY(0);
		ViewCompat.animate(holder.itemView)
				.rotationX(0)
				.setDuration(getAddDuration())
				.setInterpolator(mInterpolator)
				.setListener(new BaseItemAnimator.DefaultAddVpaListener(holder))
				.setStartDelay(getAddDelay(holder))
				.start();
	}
}
