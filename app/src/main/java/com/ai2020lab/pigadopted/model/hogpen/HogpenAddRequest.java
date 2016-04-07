package com.ai2020lab.pigadopted.model.hogpen;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 添加猪圈请求实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class HogpenAddRequest extends HogpenInfo {

	/**
	 * 用户id
	 */
	@Expose
	@SerializedName("user_id")
	public int userID;

}
