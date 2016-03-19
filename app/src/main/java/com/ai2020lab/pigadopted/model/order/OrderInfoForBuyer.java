package com.ai2020lab.pigadopted.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 买家订单信息实体类
 * Created by Justin on 2016/3/8.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class OrderInfoForBuyer extends OrderInfo {
	/**
	 * 买家已经订购的部位列表,只包含当前买家订购的部位列表
	 */
	@Expose
	@SerializedName("order_part_list")
	public List<PigPart> pigParts;
}
