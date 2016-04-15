/*
 * Copyright 2016 Justin Z
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

package com.ai2020lab.aimedia.model;

/**
 * 摄像头状态常量类
 *
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class CameraStatus {
    /**
     * 摄像头打开失败,可能是设备正在被其他进程占用造成
     */
    public final static int OPEN_FAIL = -2;
    /**
     * 摄像头不可用,可能是没有找到设备造成
     */
    public final static int INVALID = -1;
    /**
     * 后置摄像头
     */
    public final static int FACING_BACK = 0;
    /**
     * 前置摄像头
     */
    public final static int FACING_FRONT = 1;

}
