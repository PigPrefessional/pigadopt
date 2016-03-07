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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.ai2020lab.aiutils.common.LogUtils;

/**
 * 系统定时器工具类，可发送系统级定时广播
 * Created by Justin on 2015/7/13.
 * Email:502953057@qq.com
 */
public class AlarmUtil {
    /** 日志标题 */
    private final static String TAG = AlarmUtil.class.getSimpleName();

    private final static AlarmUtil INSTANCE = new AlarmUtil();

    private AlarmManager am;

    private AlarmUtil() {
    }

    /**
     * 工厂方法
     * 
     * @return
     */
    public static AlarmUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 在指定时间后发送一个广播，只发送一次
     * 
     * @param context 上下文引用
     * @param receiverAction 发送广播的action
     * @param delayTime 在该时间后发送广播，单位为毫秒
     */
    public void sendBroadcast(Context context, String receiverAction, long delayTime) {
        LogUtils.i(TAG, "发送action为：" + receiverAction + " 的单次广播");
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(receiverAction);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = SystemClock.elapsedRealtime() + delayTime;
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendIntent);
    }

    /**
     * 在指定时间后发送广播，然后再指定的时间间隔后重复发送该广播
     * 
     * @param context 上下文引用
     * @param receiverAction 发送广播的action
     * @param delayTime 在该时间后发送广播，单位为毫秒
     * @param periodTime 指定重复发送广播的时间间隔，单位为毫秒
     */
    public void sendBroadcast(Context context, String receiverAction, long delayTime,
            long periodTime) {
        LogUtils.i(TAG, "发送action为：" + receiverAction + " 的周期广播");
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(receiverAction);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = SystemClock.elapsedRealtime() + delayTime;
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
                periodTime, pendIntent);
    }

    /**
     * 取消一个指定Intent action的全局定时器
     * @param context 上下文引用
     * @param receiverAction 接收到的广播action字符窜
     */
    public void cancelAlarm(Context context, String receiverAction) {
        LogUtils.i(TAG, "取消action为：" + receiverAction + " 的广播");
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(receiverAction);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pendIntent);
    }

}
