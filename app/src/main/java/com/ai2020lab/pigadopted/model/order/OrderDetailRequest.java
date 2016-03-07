package com.ai2020lab.pigadopted.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 查询猪猪订单信息请求数据实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class OrderDetailRequest {

	@Expose
	@SerializedName("pig_id")
	public String pigID;
}
