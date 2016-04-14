package com.ai2020lab.pigadopted.chart;

import android.os.Bundle;

import com.ai2020lab.pigadopted.fragment.StatisticsChartFragment;
import com.ai2020lab.pigadopted.fragment.StepsChartFragment;
import com.ai2020lab.pigadopted.fragment.TemperatureChartFragment;
import com.ai2020lab.pigadopted.fragment.WeightChartFragment;

import java.io.Serializable;

/**
 * Created by Rocky on 16/4/12.
 */
public class StatisticsChartFragmentFactory {

    public enum ChartType {
        STEPS, TEMPERATURE, WEIGHT
    }

    public static StatisticsChartFragment createFragment(int width, int height, ChartType chartType, Serializable dataSet) {
        StatisticsChartFragment f = null;
        switch (chartType) {
            case WEIGHT:
                f = new WeightChartFragment();
                break;
            case TEMPERATURE:
                f = new TemperatureChartFragment();
                break;
            case STEPS:
                f = new StepsChartFragment();
                break;
            default:
                break;
        }

        Bundle args = new Bundle();
        args.putInt(StatisticsChartFragment.WIDTH, width);
        args.putInt(StatisticsChartFragment.HEIGHT, height);

        args.putSerializable(StatisticsChartFragment.DATA_SET, dataSet);

        f.setArguments(args);
        return f;
    }
}
