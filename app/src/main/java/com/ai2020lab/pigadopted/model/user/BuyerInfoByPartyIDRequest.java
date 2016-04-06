/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 根据partyID查询买家家用户信息请求实体类
 * Created by Justin Z on 2016/4/5.
 * 502953057@qq.com
 */
public class BuyerInfoByPartyIDRequest {
	@Expose
	@SerializedName("party_id")
	public int partyID;
}
