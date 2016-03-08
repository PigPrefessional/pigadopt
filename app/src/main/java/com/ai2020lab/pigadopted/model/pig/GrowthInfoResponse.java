package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 查询猪猪成长历程响应数据实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class GrowthInfoResponse extends ResponseData<GrowthInfoResponse.GrowthInfoList> {

	public class GrowthInfoList {

		@Expose
		@SerializedName("growth_info_list")
		public List<GrowthInfo> growthInfos;
	}
}
