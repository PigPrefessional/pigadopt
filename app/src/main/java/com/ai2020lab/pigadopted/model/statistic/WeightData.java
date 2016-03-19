package com.ai2020lab.pigadopted.model.statistic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rocky on 16/3/18.
 */
public class WeightData {

    /**
     * 日期
     */
    @Expose
    @SerializedName("buyer_number")
    public String date;

    /**
     * 重量
     */
    @Expose
    @SerializedName("buyer_number")
    public float weight;

}
