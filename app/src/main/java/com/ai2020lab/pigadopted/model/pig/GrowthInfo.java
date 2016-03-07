package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪猪生长记录实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class GrowthInfo {

	@Expose
	@SerializedName("pig_photo")
	public String pigPhoto;

	@Expose
	@SerializedName("pig_weight")
	public float pigWeight;

	@Expose
	@SerializedName("collected_time")
	public long collectedTime;

	public String collectedDate;


}
