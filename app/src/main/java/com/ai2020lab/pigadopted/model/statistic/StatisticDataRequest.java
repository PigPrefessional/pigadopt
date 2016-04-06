package com.ai2020lab.pigadopted.model.statistic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 查询统计数据请求
 * Created by Rocky on 16/4/5.
 */
public class StatisticDataRequest {
    /**
     * 猪id
     */
    @Expose
    @SerializedName("pig_id")
    public String pigID;
    /**
     * 获取方式，按小时或天获取数据("hour", "day")
     */
    @Expose
    public String type;
    /**
     * 数据开始日期
     */
    @Expose
    public String begin;
    /**
     * 数据结束日期
     */
    @Expose
    public String end;
}
