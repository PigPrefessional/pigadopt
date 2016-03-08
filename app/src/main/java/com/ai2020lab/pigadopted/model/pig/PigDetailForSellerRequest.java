package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 卖家页面查询猪详情请求实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigDetailForSellerRequest {
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
