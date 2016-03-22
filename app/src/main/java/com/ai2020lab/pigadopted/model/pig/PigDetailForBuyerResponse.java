package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.base.ResponseData;

/**
 * 买家页面查询猪详情响应实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigDetailForBuyerResponse extends ResponseData<PigDetailForBuyerResponse.PigDetailForBuyerResult> {

	/**
	 * 返回结果只包含growth_info,health_info和order_info
	 */
	public class PigDetailForBuyerResult extends PigDetailInfoAndOrder {

	}
}
