/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aiviews.wheelview;

import android.os.Handler;
import android.os.Message;

final class MessageHandler extends Handler {
	public static final int WHAT_INVALIDATE_LOOP_VIEW = 1000;
	public static final int WHAT_SMOOTH_SCROLL = 2000;
	public static final int WHAT_ITEM_SELECTED = 3000;

	final WheelView mLoopView;

	MessageHandler(WheelView loopView) {
		this.mLoopView = loopView;
	}

	@Override
	public final void handleMessage(Message msg) {
		switch (msg.what) {
			case WHAT_INVALIDATE_LOOP_VIEW:
				mLoopView.invalidate();
				break;

			case WHAT_SMOOTH_SCROLL:
				mLoopView.smoothScroll(WheelView.ACTION.FLING);
				break;

			case WHAT_ITEM_SELECTED:
				mLoopView.onItemSelected();
				break;
		}
	}

}
