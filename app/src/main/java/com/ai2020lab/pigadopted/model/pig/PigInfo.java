package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.hogpen.HogpenInfo;
import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 猪猪基本信息实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigInfo implements Serializable {
	/**
	 * 猪id
	 */
	@Expose
	@SerializedName("pig_id")
	public String pigID;
	/**
	 * 猪圈信息
	 */
	@Expose
	@SerializedName("hogpen_info")
	public HogpenInfo hogpenInfo;
	/**
	 * 猪品种对象
	 */
	@Expose
	@SerializedName("pig_category")
	public PigCategory pigCategory;
	/**
	 * 入栏时间
	 */
	@Expose
	@SerializedName("attended_time")
	public long attendedTime;

	public String attendedDate;
	/**
	 * 入栏体重
	 */
	@Expose
	@SerializedName("attended_weight")
	public float attendedWeight;
	/**
	 * 入栏猪龄
	 */
	@Expose
	@SerializedName("attended_age")
	public float attendedAge;

	public String birthDate;

	/**
	 * 卖家用户信息
	 */
	@Expose
	@SerializedName("user_info")
	public UserInfo userInfo;


}
