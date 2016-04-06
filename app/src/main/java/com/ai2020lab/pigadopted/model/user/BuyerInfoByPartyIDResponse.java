/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.model.user;

import com.ai2020lab.pigadopted.model.base.ResponseData;

/**
 * 根据partyID查询买家用户信息响应实体类
 * Created by Justin Z on 2016/4/5.
 * 502953057@qq.com
 */
public class BuyerInfoByPartyIDResponse extends ResponseData<BuyerInfoByPartyIDResponse.BuyerInfoResult> {
	public class BuyerInfoResult extends UserInfo {

	}
}
