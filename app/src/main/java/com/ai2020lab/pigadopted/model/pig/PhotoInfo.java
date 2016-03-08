package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 拍照信息实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PhotoInfo {
	/**
	 * 拍照距离
	 */
	@Expose
	@SerializedName("distance")
	public String distance;
	/**
	 * 设备信息
	 */
	@Expose
	@SerializedName("device_info")
	public String deviceInfo;
	/**
	 * 拍照时间
	 */
	@Expose
	@SerializedName("collected_time")
	public long collectedTime;
}
