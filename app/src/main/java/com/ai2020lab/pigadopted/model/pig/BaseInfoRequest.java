package com.ai2020lab.pigadopted.model.pig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 查询猪猪基本信息请求数据实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class BaseInfoRequest {
	
	@Expose
	@SerializedName("pig_id")
	public String pigID;
}
