package com.ai2020lab.pigadopted.model.order;

import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 猪部位信息实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigPart implements Serializable {
	/**
	 * 部位id
	 */
	@Expose
	@SerializedName("part_id")
	public int partID;
	/**
	 * 部位名称
	 */
	@Expose
	@SerializedName("part_name")
	public String partName;

	@Expose
	@SerializedName("part_code")
	public String partCode;
	/**
	 * 认购日期时间戳
	 */
	@Expose
	@SerializedName("purchase_date")
	public long purchaseTime;
	/**
	 * 买家用户信息
	 */
	@Expose
	@SerializedName("user_info")
	public UserInfo userInfo;

	public String purchaseDate;


}
