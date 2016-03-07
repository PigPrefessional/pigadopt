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

package com.ai2020lab.aiutils.system;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ResourcesUtils;

import java.util.List;

/**
 * 应用系统工具类，可以判断进程名，应用是否退到后台运行,创建应用桌面快捷方式等
 * Created by Justin on 2015/7/13.
 * Email:502953057@qq.com
 */
public class AppUtils {

	private final static String TAG = AppUtils.class.getSimpleName();

	/**
	 * 判断当前进程是否和给出的名字相符
	 *
	 * @param processName 进程名
	 * @param context     上下文引用，如果为空，则返回false
	 * @return 当前进程名和给出进程名相符则返回true, 否则返回false
	 */
	public static boolean isProcessNamedBy(String processName, Context context) {
		if (context == null) {
			LogUtils.i(TAG, "isProcessNamedBy:上下文引用为空");
			return false;
		}

		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
		if (processInfoList == null || processInfoList.size() == 0) {
			return false;
		}

		for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
			if (processInfo != null && processInfo.pid == pid
					&& processName.equals(processInfo.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断当前应用是否运行在后台<br/>
	 * 这个方法需要在AndroidManifest.xml中配置权限android.permission.GET_TASKS
	 *
	 * @param context 上下文引用
	 * @return 运行在后台则返回true, 否则返回false
	 */
	@SuppressWarnings("deprecation")
	public static boolean isAppInBackground(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "isAppInBackground:上下文引用为空");
			return false;
		}
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
		if (taskList != null && !taskList.isEmpty()) {
			ComponentName topActivity = taskList.get(0).topActivity;
			if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 判断给定类名的服务是否正在运行
	 *
	 * @param context   上下文引用
	 * @param className 指定的服务类名
	 * @return 指定名字的服务正在运行则返回true, 否则返回false
	 */
	public static boolean isServiceRunning(Context context, String className) {
		if (context == null) {
			LogUtils.i(TAG, "isServiceRunning:上下文引用为空");
			return false;
		}
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
		if (serviceList == null || serviceList.size() == 0) {
			return false;
		}
		int size = serviceList.size();
		for (int i = 0; i < size; i++) {
			if (serviceList.get(i).service.getClassName().equals(className)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 创建应用桌面快捷方式
	 *
	 * @param context 快捷方式启动的上下文引用
	 * @param icon    应用图标的资源名称drawable,mipmap
	 * @param name    快捷方式的名称
	 */
	public static void createShortCut(Context context, String icon, String name) {
		if (context == null) {
			LogUtils.i(TAG, "createShortCut:上下文引用为空");
			return;
		}
		if (TextUtils.isEmpty(name)) {
			LogUtils.i(TAG, "createShortCut:快捷方式名称为空");
			return;
		}
		// 获取快捷方式图标资源的ID
		int iconID = ResourcesUtils.getMipmapIdentifier(icon);
		if(iconID == 0){
			LogUtils.i(TAG, "createShortCut:找不到指定的图标资源");
			return;
		}
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		shortcut.putExtra("duplicate", false); // 不允许重复创建
		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
		ComponentName comp = new ComponentName(context.getPackageName(), "."
				+ ((Activity) context).getLocalClassName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
				new Intent(Intent.ACTION_MAIN).setComponent(comp));
		// 快捷方式的图标
		// 现在图标一般都放在mipmap
		Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context,
				iconID);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		context.sendBroadcast(shortcut);
	}

	/**
	 * 获取当前栈顶的Activity，可能应用已经退出获得是历史栈信息<br>
	 * 这个方法需要在AndroidManifest.xml中配置权限android.permission.GET_TASKS
	 * @param context 上下文引用
	 * @return 返回栈顶的Activity名
	 */
	@SuppressWarnings("deprecation")
	public static String getTopActivity(Context context) {
		if (context == null) {
			LogUtils.i(TAG, "getTopActivity:上下文引用为空");
			return null;
		}
		// 获得当前运行环境的Activity管理器
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 通过管理器获得当前任务栈信息
		List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		// 从任务栈中获得栈顶的activity名
		if (runningTaskInfos != null) {
			return (runningTaskInfos.get(0).topActivity).toString();
		}
		return null;
	}


}
