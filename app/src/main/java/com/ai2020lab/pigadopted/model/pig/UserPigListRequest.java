package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 获取指定用户的猪猪列表请求数据实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class UserPigListRequest {

	@Expose
	@SerializedName("user_id")
	public String userID;

	/**
	 * 用户角色类型:1-饲养者，2-领养者
	 */
	@Expose
	@SerializedName("role_type")
	public int roleType;
}
