package com.ai2020lab.pigadopted.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪部位信息实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigPart {

	@Expose
	@SerializedName("part_id")
	public String partID;

	@Expose
	@SerializedName("part_name")
	public String partName;

	@Expose
	@SerializedName("part_price")
	public float partPrice;

	@Expose
	@SerializedName("part_weight")
	public float partWeight;

	@Expose
	@SerializedName("customer_id")
	public String customerID;

	@Expose
	@SerializedName("customer_name")
	public String customerName;

	@Expose
	@SerializedName("customer_img")
	public String customerImg;

	@Expose
	@SerializedName("purchase_date")
	public long purchaseTime;

	public String purchaseDate;















}
