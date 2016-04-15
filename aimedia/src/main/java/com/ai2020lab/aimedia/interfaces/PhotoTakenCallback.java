/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aimedia.interfaces;

/**
 * 拍照回调
 * Created by Justin Z on 2016/4/13.
 * 502953057@qq.com
 */
public interface PhotoTakenCallback {
	void photoTaken(byte[] data, int orientation);
}
