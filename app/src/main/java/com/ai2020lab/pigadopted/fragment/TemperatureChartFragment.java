package com.ai2020lab.pigadopted.fragment;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.chart.ChartDataAdapter;
import com.ai2020lab.pigadopted.chart.LineChartPoint;
import com.ai2020lab.pigadopted.model.statistic.BodyTemperatureData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

/**
 * Created by Rocky on 16/4/12.
 */
public class TemperatureChartFragment extends StatisticsChartFragment<BodyTemperatureData> {
    @Override
    protected int getTitleID() {
        return R.string.pig_chart_title_temperature;
    }

    @Override
    protected int getIconID() {
        return R.mipmap.pig_chart_icon_temperature;
    }

    @Override
    protected void setChartColor(LineDataSet set) {
        set.setColor(getResources().getColor(R.color.pig_chart_temperature_line));
        set.setCircleColor(getResources().getColor(R.color.pig_chart_temperature_line));
        set.setFillColor(getResources().getColor(R.color.pig_chart_temperature_fill));
    }

    @Override
    protected ChartDataAdapter.XYValues convertToChartValues(List<LineChartPoint> dayPoints, int viewType) {
        ChartDataAdapter.XYValues xyValues = null;

        switch (viewType) {
            case VIEW_MONTH:
                xyValues = ChartDataAdapter.convertToChartValues(
                        ChartDataAdapter.groupPoints(dayPoints, ChartDataAdapter.monthGroupStrategy, ChartDataAdapter.maxStrategy));
                break;
            case VIEW_WEEK:
                xyValues = ChartDataAdapter.convertToChartValues(
                        ChartDataAdapter.groupPoints(dayPoints, ChartDataAdapter.weekGroupStrategy, ChartDataAdapter.maxStrategy));
                break;
            case VIEW_DAY:
                xyValues = ChartDataAdapter.convertToChartValues(dayPoints);
                break;
            default:
                break;
        }

        return xyValues;
    }
}
