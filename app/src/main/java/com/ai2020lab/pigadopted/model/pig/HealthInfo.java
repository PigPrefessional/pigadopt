package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪健康状况实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class HealthInfo {

	/**
	 * 体温
	 */
	@Expose
	@SerializedName("temperature")
	public float temperature;
	/**
	 * 步数
	 */
	@Expose
	@SerializedName("steps")
	public int steps;
	/**
	 * 体脂比
	 */
	@Expose
	@SerializedName("fat_rate")
	public float fatRate;
	/**
	 * 猪状态，默认为正常
	 */
	@Expose
	@SerializedName("status")
	public int status = PigStatus.WALKING;

}
