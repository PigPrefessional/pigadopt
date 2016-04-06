/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.ai2020lab.pigadopted.model.order.PigPart;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 查询猪身体部位列表响应实体类
 * Created by Justin Z on 2016/4/5.
 * 502953057@qq.com
 */
public class PigPartsListResponse extends ResponseData<PigPartsListResponse.PigPartsListResult> {


	public class PigPartsListResult {

		@Expose
		@SerializedName("pig_part_list")
		public List<PigPart> pigParts;
	}
}
