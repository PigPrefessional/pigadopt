/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aiviews.wheelview;

final class OnItemSelectedRunnable implements Runnable {
	final WheelView loopView;

	OnItemSelectedRunnable(WheelView loopview) {
		loopView = loopview;
	}

	@Override
	public final void run() {
		loopView.onItemSelectedListener.onItemSelected(loopView.getCurrentItem());
	}
}
