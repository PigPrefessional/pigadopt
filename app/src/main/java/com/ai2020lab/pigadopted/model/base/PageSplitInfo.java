package com.ai2020lab.pigadopted.model.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 分页加载数据实体类
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class PageSplitInfo {
	/**
	 * 一次请求返回的数据总数
	 */
	@Expose
	@SerializedName("data_number")
	public int dataNum;
	/**
	 * 第一条加载的数据在所有数据中的位置下标
	 */
	@Expose
	@SerializedName("start_index")
	public int startIndex;
}
