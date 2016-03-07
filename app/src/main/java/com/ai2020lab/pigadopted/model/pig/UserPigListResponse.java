package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.ai2020lab.pigadopted.model.hogpen.HogpenInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 获取指定用户的猪猪列表响应数据实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class UserPigListResponse extends ResponseData<UserPigListResponse.HogpenInfoList> {

	public class HogpenInfoList {

		@Expose
		@SerializedName("hogpen_info_list")
		public List<HogpenInfo> hogpenInfos;
	}


}
