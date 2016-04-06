package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.base.ResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 查询猪猪种类响应数据实体类
 * Created by Justin on 2015/12/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class CategoriesListResponse extends ResponseData<CategoriesListResponse.CategoriesListResult> {

	public class CategoriesListResult {

		@Expose
		@SerializedName("category_list")
		public List<PigCategory> pigCategories;
	}

}
