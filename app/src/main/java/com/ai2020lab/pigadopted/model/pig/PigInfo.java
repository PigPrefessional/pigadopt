package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪猪基本信息实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigInfo {

	@Expose
	@SerializedName("pig_id")
	public String pigID;

    @Expose
    @SerializedName("category_id")
	public String categoryID;

	@Expose
	@SerializedName("category_name")
	public String categoryName;

	@Expose
	@SerializedName("owner_id")
	public String ownerID;

	@Expose
	@SerializedName("owner_name")
	public String ownerName;

	@Expose
	@SerializedName("hogpen_id")
	public String hogpenID;

	@Expose
	@SerializedName("birthday")
	public long birthday;

	public String birthDate;

	@Expose
	@SerializedName("attended_time")
	public long attendedTime;

	public String attendedDate;


}
