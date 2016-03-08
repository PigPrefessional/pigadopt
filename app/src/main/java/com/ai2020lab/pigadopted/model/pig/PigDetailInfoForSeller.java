package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.order.OrderInfoForSeller;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 卖家猪详情实体类
 * Created by Justin on 2016/3/8.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigDetailInfoForSeller extends PigDetailInfo {
	@Expose
	@SerializedName("order_info")
	public OrderInfoForSeller orderInfo;
}
