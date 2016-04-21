/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.aimedia.interfaces;

import java.io.File;

/**
 * 照片保存监听接口
 * Created by Justin Z on 2016/4/13.
 * 502953057@qq.com
 */
public interface PhotoSavedListener {
	void savedBefore();
	void savedFailure();
	void savedSuccess(File Photo, File cropperPhoto);
}
