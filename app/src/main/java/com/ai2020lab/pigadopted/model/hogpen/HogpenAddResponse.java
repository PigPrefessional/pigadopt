package com.ai2020lab.pigadopted.model.hogpen;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 添加猪圈响应实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class HogpenAddResponse extends ResponseData<HogpenAddResponse.HogpenAddResult> {

	public class HogpenAddResult {
		@Expose
		@SerializedName("hogpen_id")
		public int hogpenID;

	}
}
