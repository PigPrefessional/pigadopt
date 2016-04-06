package com.ai2020lab.pigadopted.net;

/**
 * Created by Rocky on 15/7/17.
 * 接口Url
 */
public enum UrlName {

	// 卖家猪详情
    PIG_INFO_FOR_SELLER("findSellerPigInfo"),
	// 买家猪详情
    PIG_INFO_FOR_CUSTOMER("findCustomerPigInfo"),
	// 买家猪列表
    PIG_LIST_FOR_CUSTOMER("queryCustomerPigList"),
	// 猪部位列表
    PIG_BODY_PART_LIST("queryPigBodyPartList"),
	// 猪品种列表
    PIG_CATEGORY_LIST("queryPigCategoryList"),
	// 根据partyID查询卖家信息
    SELLER_INFO_BY_PARTYID("queryProviderInfo"),
	// 根据partyID查询买家信息
	BUYER_INFO_BY_PARTYID("queryCustomerInfo"),
	ADD_GROWTH_INFO("uploadGrowthPhoto");




    private String name;

    UrlName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return "http://" + HOST + ":" + PORT + "/" + name;
    }

    private static final String HOST = "171.221.254.231";
    private static final int PORT = 3002;

}
