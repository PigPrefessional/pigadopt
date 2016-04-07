package com.ai2020lab.pigadopted.chart;

import com.ai2020lab.pigadopted.model.statistic.BodyTemperatureData;
import com.ai2020lab.pigadopted.model.statistic.StepData;
import com.ai2020lab.pigadopted.model.statistic.WeightData;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * 将具体的数据转换为一般的图表数据
 * Created by Rocky on 16/4/5.
 */
public class ChartDataAdapter {

    public static XYValues convertWeightData(List<WeightData> dataList) {
        XYValues xyv = new XYValues();

        for (int i = 0; i < dataList.size(); i++) {
            final WeightData data = dataList.get(i);

            fillDataItem(xyv, i, data.date, data.weight);
        }

        return xyv;
    }

    public static XYValues convertStepData(List<StepData> dataList) {
        XYValues xyv = new XYValues();


        for (int i = 0; i < dataList.size(); i++) {
            final StepData data = dataList.get(i);

            fillDataItem(xyv, i, data.date, data.step);
        }

        return xyv;
    }

    public static XYValues convertTemperatureData(List<BodyTemperatureData> dataList) {
        XYValues xyv = new XYValues();


        for (int i = 0; i < dataList.size(); i++) {
            final BodyTemperatureData data = dataList.get(i);

            fillDataItem(xyv, i, data.date, data.temp);
        }

        return xyv;
    }

    private static void fillDataItem(XYValues xyValues, int index, String xValue, float yValue) {
        xyValues.xVals.add(xValue);
        xyValues.yVals.add(new Entry(yValue, index));

        if (xyValues.maxYValue < yValue) {
            xyValues.maxYValue = yValue;
        }
    }

    public static class XYValues {
        public float maxYValue;
        public ArrayList<String> xVals = new ArrayList<String>();
        public ArrayList<Entry> yVals = new ArrayList<Entry>();
    }
}
