package com.ai2020lab.pigadopted.chart;

import com.ai2020lab.pigadopted.common.DateUtils;
import com.ai2020lab.pigadopted.model.statistic.BodyTemperatureData;
import com.ai2020lab.pigadopted.model.statistic.StepData;
import com.ai2020lab.pigadopted.model.statistic.WeightData;
import com.github.mikephil.charting.data.Entry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 将具体的数据转换为一般的图表数据
 * Created by Rocky on 16/4/5.
 */
public class ChartDataAdapter {

    public static XYValues convertToXYValues(List<? extends Object> dataList) {
        List<LineChartPoint> points = LineChartPointFactory.createPointList(dataList);

        return convertModelData(points);
    }

    public static XYValues convertFromDayToMonth(List<LineChartPoint> dataList) {
        return convertToXYValues(fromDayToMonth(dataList));
    }

    private static XYValues convertModelData(List<LineChartPoint> dataList) {
        XYValues xyv = new XYValues();


        for (int i = 0; i < dataList.size(); i++) {
            final LineChartPoint point = dataList.get(i);

            fillDataItem(xyv, i, point.getXValue(), point.getYValue());
        }

        return xyv;
    }

//    public static Map<WeekPoint, List<LineChartPoint>> groupByWeek(List<LineChartPoint> dataList) {
//        return null;
//    }

    public static List<LineChartPoint> groupByWeek(List<LineChartPoint> dataList) {
        Calendar lastCalendar = null;
        List<LineChartPoint> result = new ArrayList<>();
        List<LineChartPoint> tempList = new ArrayList<>();

        if (dataList.size() == 1) {
            return dataList;
        }

        for (int i = 0; i < dataList.size(); i++) {
            final LineChartPoint point = dataList.get(i);
            final Calendar calendar = parseDate(point);


            if (lastCalendar == null) {
                lastCalendar = calendar;
                tempList.add(point);
            } else if (DateUtils.isSameWeek(lastCalendar, calendar)) {
                tempList.add(point);
            } else {
                if (i == dataList.size() - 1) {
                    result.add(point);
                } else {
                    result.add(getAverage(tempList));

                    lastCalendar = calendar;
                    tempList.add(point);
                }
            }
        }

        return result;
    }

    private static List<LineChartPoint> fromDayToMonth(List<LineChartPoint> dataList) {
        return null;
    }

    private static void fillDataItem(XYValues xyValues, int index, String xValue, float yValue) {
        xyValues.xVals.add(xValue);
        xyValues.yVals.add(new Entry(yValue, index));

        if (xyValues.maxYValue < yValue) {
            xyValues.maxYValue = yValue;
        }
    }

    private static Calendar parseDate(LineChartPoint point) {
        Calendar c = null;

        final String dateStr = point.getXValue();

        try {
            c = DateUtils.toDefaultCalendar(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return c;
    }

    private static LineChartPoint getAverage(List<LineChartPoint> points) {
        float y = 0;

        for (LineChartPoint point: points) {
            y += point.getYValue();
        }

        AbsLineChartPoint reuslt = new AbsLineChartPoint();
        reuslt.yValue = y / points.size();
        reuslt.xValue = points.get(0).getXValue();

        return reuslt;
    }

    private static class AbsLineChartPoint implements LineChartPoint {

        float yValue;
        String xValue;

        @Override
        public float getYValue() {
            return yValue;
        }

        @Override
        public String getXValue() {
            return xValue;
        }
    }

    public static class XYValues {
        public float maxYValue;
        public ArrayList<String> xVals = new ArrayList<String>();
        public ArrayList<Entry> yVals = new ArrayList<Entry>();
    }

    public static class WeekPoint {
        public int year;
        public int month;
        public int week;
    }
}
