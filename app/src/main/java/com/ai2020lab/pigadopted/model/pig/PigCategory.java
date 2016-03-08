package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪品种实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigCategory {

	/**
	 * 猪品种名
	 */
	@Expose
	@SerializedName("category_id")
	public String categoryID;
	/**
	 * 猪品种名
	 */
	@Expose
	@SerializedName("category_name")
	public String categoryName;
	/**
	 * 猪品种照片地址链接
	 */
	@Expose
	@SerializedName("category_photo")
	public String categoryPhoto;

}
