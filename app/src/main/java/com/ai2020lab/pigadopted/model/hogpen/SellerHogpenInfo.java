package com.ai2020lab.pigadopted.model.hogpen;

import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Justin on 2016/3/7.
 * Email:502953057@qq.com,zhenghx3@asiainfo.com
 */
public class SellerHogpenInfo extends HogpenInfo implements Serializable{
	/**
	 * 饲养猪列表
	 */
	@Expose
	@SerializedName("pig_list")
	public List<PigDetailInfoAndOrder> pigInfos;
}
