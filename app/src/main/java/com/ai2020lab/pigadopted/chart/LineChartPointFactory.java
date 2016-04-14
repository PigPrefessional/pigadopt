package com.ai2020lab.pigadopted.chart;

import com.ai2020lab.pigadopted.model.statistic.BodyTemperatureData;
import com.ai2020lab.pigadopted.model.statistic.StepData;
import com.ai2020lab.pigadopted.model.statistic.WeightData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rocky on 16/4/13.
 */
public class LineChartPointFactory {

    public static LineChartPoint createPoint(Object data) {
        LineChartPoint point = null;

        if (data instanceof WeightData) {
            WeightData weight = (WeightData) data;
            point = new WeightPoint(weight);
        } else if (data instanceof StepData) {
            StepData step = (StepData) data;
            point = new StepPoint(step);
        } else if (data instanceof BodyTemperatureData) {
            BodyTemperatureData temp = (BodyTemperatureData) data;
            point = new TemperaturePoint(temp);
        }

        return point;
    }

    public static List<LineChartPoint> createPointList(List<? extends Object> list) {
        List<LineChartPoint> points = new LinkedList<>();

        for (Object data : list) {
            LineChartPoint point = createPoint(data);
            if (point != null) {
                points.add(point);
            }
        }

        return points;
    }

    private static class WeightPoint implements LineChartPoint {

        private WeightData data;

        WeightPoint(WeightData data) {
            this.data = data;
        }

        @Override
        public float getYValue() {
            return data.weight;
        }

        @Override
        public String getXValue() {
            return data.date;
        }
    }

    private static class StepPoint implements LineChartPoint {

        private StepData data;

        StepPoint(StepData data) {
            this.data = data;
        }

        @Override
        public float getYValue() {
            return data.step;
        }

        @Override
        public String getXValue() {
            return data.date;
        }
    }

    private static class TemperaturePoint implements LineChartPoint {

        private BodyTemperatureData data;

        TemperaturePoint(BodyTemperatureData data) {
            this.data = data;
        }

        @Override
        public float getYValue() {
            return data.temp;
        }

        @Override
        public String getXValue() {
            return data.date;
        }
    }
}
