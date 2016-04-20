package com.ai2020lab.pigadopted.model.pig;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 猪猪照片上传请求数据实体类
 * Created by Justin on 2015/12/3.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigPhotoUploadRequest implements Serializable {

	/**
	 * 拍照距离
	 */
	@Expose
	@SerializedName("photo_distance")
	public double distance;
	/**
	 * 拍照的时候手机同垂直方向的夹角，单位为度
	 */
	@Expose
	@SerializedName("vertical_angle")
	public double verticalAngle;

	@Expose
	@SerializedName("object_left")
	public int objectLeft;

	@Expose
	@SerializedName("object_top")
	public int objectTop;

	@Expose
	@SerializedName("object_right")
	public int objectRight;

	@Expose
	@SerializedName("object_down")
	public int objectDown;

	@Expose
	@SerializedName("pic_width")
	public int picWidth;

	@Expose
	@SerializedName("pic_height")
	public int picHeight;

	/**
	 * 设备ID，小米手机为1
	 */
	@Expose
	@SerializedName("equipment_id")
	public int equipmentID = 1;
	/**
	 * 拍照时间
	 */
	@Expose
	@SerializedName("record_time")
	public long recordTime;
	/**
	 * 猪id
	 */
	@Expose
	@SerializedName("pig_id")
	public int pigID;

	public String pigPhoto;


}
