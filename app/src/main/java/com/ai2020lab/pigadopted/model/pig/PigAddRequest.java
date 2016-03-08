package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪入栏请求实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigAddRequest extends PigInfo {
	/**
	 * 品种id
	 */
	@Expose
	@SerializedName("category_id")
	public String category_id;
	/**
	 * 猪圈id
	 */
	@Expose
	@SerializedName("hogpen_id")
	public String hogpenID;
}
