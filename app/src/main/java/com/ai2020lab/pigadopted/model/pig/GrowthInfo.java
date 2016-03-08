package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪猪生长记录实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class GrowthInfo {

	/**
	 * 猪照片链接地址
	 */
	@Expose
	@SerializedName("pig_photo")
	public String pigPhoto;
	/**
	 * 猪当前体重
	 */
	@Expose
	@SerializedName("pig_weight")
	public float pigWeight;
	/**
	 * 猪本次增长体重
	 */
	@Expose
	@SerializedName("increased_weight")
	public float increasedWeight;
	/**
	 * 猪图片的宽度同屏幕宽度的比例,使用猪出栏最大体重和猪当前体重的比例得出
	 */
	@Expose
	@SerializedName("body_form")
	public float bodyForm;
	/**
	 * 照片上传时间
	 */
	@Expose
	@SerializedName("collected_time")
	public long collectedTime;

	public String collectedDate;


}
