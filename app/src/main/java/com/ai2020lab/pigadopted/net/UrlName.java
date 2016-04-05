package com.ai2020lab.pigadopted.net;

/**
 * Created by Rocky on 15/7/17.
 * 接口Url
 */
public enum UrlName {

    TEST("findCustomerProposalDetail"),

    PIG_LIST("queryPigList"),
    PIG_DETAIL("findPigDetail"),
    PIG_ORDER_DETAIL("findPigOrderDetail"),
    PIG_CATEGORIES("queryPigCategories"),
    PIG_LAIRAGE("pigLairage"),
    PIG_PHOTO_UPLOAD("uploadPigPhoto"),
    HOGPEN_DETAIL("findHogpenDetail"),
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

    private static final String HOST = "10.5.1.249";
    private static final int PORT = 3002;
    private static final String PIG_ADOPTED = "pigadopted";

}
