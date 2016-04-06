package com.ai2020lab.pigadopted.model.hogpen;

import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 猪圈信息实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class HogpenInfo implements Serializable {
	/**
	 * 猪圈id
	 */
	@Expose
	@SerializedName("hogpen_id")
	public String hogpenID;
	/**
	 * 猪圈名字
	 */
	@Expose
	@SerializedName("hogpen_name")
	public String hogpenName;
	/**
	 * 猪圈长
	 */
	@Expose
	@SerializedName("hogpen_length")
	public float hogpenLength;
	/**
	 * 猪圈宽
	 */
	@Expose
	@SerializedName("hogpen_width")
	public float hogpenWidth;
	/**
	 * 猪圈照片
	 */
	@Expose
	@SerializedName("hogpen_photo")
	public String hogpenPhoto;
	/**
	 * 卖家用户信息
	 */
	@Expose
	@SerializedName("user_info")
	public UserInfo userInfo;


}
