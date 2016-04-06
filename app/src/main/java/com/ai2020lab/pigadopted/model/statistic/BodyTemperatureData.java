package com.ai2020lab.pigadopted.model.statistic;

import com.google.gson.annotations.Expose;

/**
 * Created by Rocky on 16/4/5.
 */
public class BodyTemperatureData {
    /**
     * 日期
     */
    @Expose
    public String date;

    /**
     * 体温
     */
    @Expose
    public float temp;
}
