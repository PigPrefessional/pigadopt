package com.ai2020lab.pigadopted.fragment;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.chart.ChartDataAdapter;
import com.ai2020lab.pigadopted.chart.LineChartPoint;
import com.ai2020lab.pigadopted.model.statistic.WeightData;

import java.util.List;

/**
 * Created by Rocky on 16/4/12.
 */
public class WeightChartFragment extends StatisticsChartFragment<WeightData> {
    @Override
    protected int getTitleID() {
        return R.string.pig_chart_title_weight;
    }

    @Override
    protected int getIconID() {
        return R.mipmap.pig_chart_icon_weight;
    }

    @Override
    protected ChartDataAdapter.XYValues convertDataSet(List<LineChartPoint> dataList) {
        // init chart is display day's data. Here, we want to display week's data, so zoom out once
        zoomOutViewType();
        return ChartDataAdapter.convertToChartValues(ChartDataAdapter.groupByWeek(dataList));
    }
}
