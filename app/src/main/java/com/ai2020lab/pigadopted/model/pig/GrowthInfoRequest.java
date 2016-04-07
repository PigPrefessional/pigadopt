package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.base.PageSplitInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 查询猪猪成长历程请求数据实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class GrowthInfoRequest {

	@Expose
	@SerializedName("pig_id")
	public int pigID;

	@Expose
	@SerializedName("page_split_info")
	public PageSplitInfo pageSplitInfo;

}
