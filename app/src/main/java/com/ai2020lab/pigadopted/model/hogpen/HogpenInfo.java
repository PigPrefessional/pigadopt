package com.ai2020lab.pigadopted.model.hogpen;

import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 猪圈信息实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class HogpenInfo {

	@Expose
	@SerializedName("hogpen_id")
	public String hogpenID;

	@Expose
	@SerializedName("owner_id")
	public String ownerID;

	@Expose
	@SerializedName("owner_name")
	public String ownerName;

	@Expose
	@SerializedName("owner_img")
	public String ownerImg;

	@Expose
	@SerializedName("area")
	public float area;

	@Expose
	@SerializedName("capacity")
	public int capacity;

	@Expose
	@SerializedName("images")
	public List<String> images;

	@Expose
	@SerializedName("pigs")
	public List<PigInfo> pigInfos;


}
