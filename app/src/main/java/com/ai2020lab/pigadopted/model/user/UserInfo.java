package com.ai2020lab.pigadopted.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 用户信息实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class UserInfo{
	/**
	 * 用户id
	 */
	@Expose
	@SerializedName("user_id")
	public String userID;
	/**
	 * 用户名
	 */
	@Expose
	@SerializedName("user_name")
	public String userName;
	/**
	 * 用户头像地址链接
	 */
	@Expose
	@SerializedName("user_portrait")
	public String userPortrait;
	/**
	 * 用户性别
	 */
	@Expose
	@SerializedName("user_gender")
	public int userGender;


}
