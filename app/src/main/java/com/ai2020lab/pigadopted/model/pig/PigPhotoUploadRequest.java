package com.ai2020lab.pigadopted.model.pig;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪猪照片上传请求数据实体类
 * Created by Justin on 2015/12/3.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigPhotoUploadRequest {

	/**
	 * 拍照距离
	 */
	@Expose
	@SerializedName("distance")
	public String distance;
	/**
	 * 设备信息
	 */
	@Expose
	@SerializedName("device_info")
	public String deviceInfo;
	/**
	 * 拍照时间
	 */
	@Expose
	@SerializedName("collected_time")
	public long collectedTime;
	/**
	 * 猪id
	 */
	@Expose
	@SerializedName("pig_id")
	public String pigID;


}
