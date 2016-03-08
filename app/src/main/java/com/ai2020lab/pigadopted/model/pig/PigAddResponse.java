package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 猪入栏响应实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PigAddResponse extends ResponseData<PigAddResponse.PigAddResult> {

	public class PigAddResult {
		/**
		 * 猪id
		 */
		@Expose
		@SerializedName("pig_id")
		public String pigID;
	}
}
