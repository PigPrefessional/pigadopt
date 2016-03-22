package com.ai2020lab.pigadopted.model.pig;

import com.ai2020lab.pigadopted.model.order.OrderInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rocky on 16/3/22.
 */
public class PigDetailInfoAndOrder extends PigDetailInfo {
    @Expose
    @SerializedName("order_info")
    public OrderInfo orderInfo;
}
