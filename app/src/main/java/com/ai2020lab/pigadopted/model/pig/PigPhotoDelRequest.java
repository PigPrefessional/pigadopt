package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪照片删除请求实体类-用于照片比对失败的时候删除照片
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigPhotoDelRequest {
	/**
	 * 猪id
	 */
	@Expose
	@SerializedName("pig_id")
	public String pigID;
	/**
	 * 猪照片地址链接
	 */
	@Expose
	@SerializedName("pig_photo")
	public String pigPhoto;
}
