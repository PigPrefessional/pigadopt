/*
 * Copyright 2015 Justin Z
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ai2020lab.aiutils.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Justin on 2015/7/17.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class ViewUtils {

	private final static String TAG = ViewUtils.class.getSimpleName();

	/**
	 * 这个方式是对layoutInflater.inflate(int, null)的封装
	 * 使用这种方式创建View通常需要自己重新定义LayoutParams
	 * @param context 上下文引用
	 * @param resId 布局文件资源id
	 * @return 返回创建成功的View对象，失败则返回null
	 */
	public static View makeView(Context context, int resId) {
		return makeView(context, resId, null);
	}

	/**
	 * 这个方式是对layoutInflater.inflate(int, ViewGroup)的封装
	 * @param context 上下文引用
	 * @param resId 布局文件资源ID
	 * @param root 生成View的父容器,root不为空，则返回将View加入root,并返回，否则返回View
	 * @return 成功创建返回View，否则返回null
	 */
	public static View makeView(Context context, int resId, ViewGroup root){
		View v = null;
		try{
			v = LayoutInflater.from(context).inflate(resId, root);
		} catch(Exception e){
			LogUtils.e(TAG, "makeView:生成View异常", e);
		}
		return v;
	}

	/**
	 * 这个方式是对layoutInflater.inflate(int, ViewGroup, boolean)的封装
	 * isAttch只有在root不为空的时候有效，root为空，这个方法同makeView(Context, int)效果一样
	 * @param context 上下文引用
	 * @param resId 布局文件的id
	 * @param root ViewGroup对象的引用
	 * @param isAttach 这个参数只有在root不为空的时候生效，true-将把生成的View加入到root中，并返回root,
	 *                    false-仅仅使用root的LayoutParams作为View的LayoutParmas,返回的是View
	 * @return 成功创建返回View，否则返回null
	 */
	public static View makeView(Context context, int resId, ViewGroup root, boolean isAttach){
		View v = null;
		try{
			v = LayoutInflater.from(context).inflate(resId, root, isAttach);
		} catch(Exception e){
			LogUtils.e(TAG, "makeView:生成View异常", e);
		}
		return v;
	}



}
