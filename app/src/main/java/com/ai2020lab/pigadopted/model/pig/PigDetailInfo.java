package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 猪详情信息实体类，包括猪基本信息，生长历程，健康信息
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigDetailInfo implements Serializable {

	@Expose
	@SerializedName("pig_info")
	public PigInfo pigInfo;

	@Expose
	@SerializedName("growth_info")
	public GrowthInfo growthInfo;

	@Expose
	@SerializedName("health_info")
	public HealthInfo healthInfo;


}
