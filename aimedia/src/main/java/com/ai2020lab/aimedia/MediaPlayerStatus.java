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

package com.ai2020lab.aimedia;

/**
 * MediaPlayer状态常量类
 *
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class MediaPlayerStatus {
    /**
     * 闲置状态
     */
    public final static int IDLE = 0;
    /**
     * 准备状态-参数配置完成，并成功调用了prepare()方法
     */
    public final static int PREPARE = 1;
    /**
     * 开始状态-成功调用了start()方法
     */
    public final static int START = 2;
    /**
     * 停止状态-成功调用了pause()方法
     */
    public final static int PAUSE = 3;
    /**
     * 停止状态-成功调用了stop()方法
     */
    public final static int STOP = 4;
}
