package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigDetailForBuyerRequest {
	/**
	 * 猪id
	 */
	@Expose
	@SerializedName("pig_id")
	public String pigID;
	/**
	 * 用户信息
	 */
	@Expose
	@SerializedName("user_info")
	public UserInfo userInfo;
}
