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
 * 录音机音频采集源类型常量类
 * <p/>
 * Created by Justin on 2016/2/25.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class AudioSourceType {
	/**
	 * 采集电话音频
	 */
	public final static int PHONE_DOUBLELINK = 1;
	/**
	 * 采集电话上行音频
	 */
	public final static int PHONE_UPLINK = 2;
	/**
	 * 采集电话下行音频
	 */
	public final static int PHONE_DOWNLINK = 3;
	/**
	 * 采集麦克风音频
	 */
	public final static int MIC = 4;

}
