package com.ai2020lab.pigadopted.fragment;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.chart.ChartDataAdapter;
import com.ai2020lab.pigadopted.model.statistic.StepData;

import java.util.List;

/**
 * Created by Rocky on 16/4/12.
 */
public class StepsChartFragment extends StatisticsChartFragment<StepData> {
    @Override
    protected int getTitleID() {
        return R.string.pig_chart_title_steps;
    }

    @Override
    protected int getIconID() {
        return R.mipmap.pig_chart_icon_steps;
    }


}
