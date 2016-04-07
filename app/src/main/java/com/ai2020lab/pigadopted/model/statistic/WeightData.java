package com.ai2020lab.pigadopted.model.statistic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rocky on 16/3/18.
 */
public class WeightData implements Serializable {

    /**
     * 日期
     */
    @Expose
    public String date;

    /**
     * 重量
     */
    @Expose
    public float weight;

}
