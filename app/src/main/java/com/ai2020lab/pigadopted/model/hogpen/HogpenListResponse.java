package com.ai2020lab.pigadopted.model.hogpen;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 查询卖家猪圈列表响应实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class HogpenListResponse extends ResponseData<HogpenListResponse.SellerHogpenListResult> {

	public class SellerHogpenListResult {

		@Expose
		@SerializedName("user_info")
		public UserInfo userInfo;

		@Expose
		@SerializedName("hogpen_list")
		public List<SellerHogpenInfo> hogpenInfos;

	}
}
