package com.ai2020lab.pigadopted.model.statistic;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Rocky on 16/4/5.
 */
public class StepData implements Serializable {
    /**
     * 日期
     */
    @Expose
    public String date;

    /**
     * 步数
     */
    @Expose
    public float step;
}
