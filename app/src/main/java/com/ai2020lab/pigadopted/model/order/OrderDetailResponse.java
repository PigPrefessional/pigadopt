package com.ai2020lab.pigadopted.model.order;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 查询猪猪订单信息响应数据实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class OrderDetailResponse extends ResponseData<OrderDetailResponse.PigPartList> {

	public class PigPartList {
		@Expose
		@SerializedName("pig_parts")
		public List<PigPart> pigParts;
	}
}
