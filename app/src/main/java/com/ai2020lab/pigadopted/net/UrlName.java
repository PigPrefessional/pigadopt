package com.ai2020lab.pigadopted.net;

/**
 * Created by Rocky on 15/7/17.
 * 接口Url
 */
public enum UrlName {
	// 添加猪圈
	ADD_HOGPEN("addHogpen"),
	// 添加猪
	ADD_PIG("addPig"),
	// 卖家猪圈列表
	HOGPEN_LIST_FOR_BUYER("queryHogpensList"),
	// 买家猪列表
	PIG_LIST_FOR_BUYER("queryCustomerPigList"),
	// 猪部位列表
	PIG_BODY_PART_LIST("queryPigBodyPartList"),
	// 猪品种列表
	PIG_CATEGORY_LIST("queryPigCategoryList"),
	// 根据partyID查询卖家信息
	SELLER_INFO_BY_PARTYID("queryProviderInfo"),
	// 根据partyID查询买家信息
	BUYER_INFO_BY_PARTYID("queryCustomerInfo"),
	// 查询猪生长信息
	GROWTH_INFO_LIST("queryGrowthInfoList"),
	// 上传猪成长照片
	GROWTH_INFO_UPLOAD("uploadGrowthPhoto"),
	PIG_INFO_CUSTOMER("findCustomerPigInfo"),
	PIG_INFO_SELLER("findSellerPigInfo"),
	PIG_WEIGHT_LIST("queryWeightList"),
	PIG_TEMPERATURE_LIST("queryTemperatureList"),
	PIG_STEP_LIST("queryStepList");

	private String name;

	UrlName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return "http://" + HOST + ":" + PORT + "/" + name;
	}

	private static final String HOST = "171.221.254.231";
//	private static final String HOST = "10.5.1.116";
	private static final int PORT = 3002;

}
