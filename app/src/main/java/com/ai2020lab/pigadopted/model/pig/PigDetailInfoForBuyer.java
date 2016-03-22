package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.order.OrderInfo;
import com.ai2020lab.pigadopted.model.order.OrderInfoForBuyer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 卖家猪详情实体类
 * Created by Justin on 2016/3/8.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigDetailInfoForBuyer extends PigDetailInfo {
	@Expose
	@SerializedName("order_info")
	public OrderInfo orderInfo;
}
