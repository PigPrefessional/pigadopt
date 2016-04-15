package com.ai2020lab.pigadopted.chart;

import com.ai2020lab.pigadopted.common.DateUtils;
import com.github.mikephil.charting.data.Entry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 将具体的数据转换为一般的图表数据
 * Created by Rocky on 16/4/5.
 */
public class ChartDataAdapter {


    public static XYValues convertToChartValues(List<LineChartPoint> dataList) {
        XYValues xyv = new XYValues();


        for (int i = 0; i < dataList.size(); i++) {
            final LineChartPoint point = dataList.get(i);

            fillDataItem(xyv, i, point.getXValue(), point.getYValue());
        }

        return xyv;
    }

    public static List<LineChartPoint> groupByMonth(List<LineChartPoint> dataList) {
        return groupPoints(dataList, monthGroupStrategy, averageStrategy);
    }

    public static List<LineChartPoint> groupByWeek(List<LineChartPoint> dataList) {
        return groupPoints(dataList, weekGroupStrategy, averageStrategy);
    }

    public static List<LineChartPoint> groupPoints(List<LineChartPoint> dataList, GroupStrategy groupStrategy, PointExtractStrategy extractStrategy) {
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
            } else if (groupStrategy.isInSameGroup(lastCalendar, calendar)) {
                tempList.add(point);

                if (i == dataList.size() - 1) {
                    result.add(extractStrategy.extractRepresentPoint(tempList));
                }
            } else {
                result.add(extractStrategy.extractRepresentPoint(tempList));

                if (i == dataList.size() - 1) {
                    result.add(point);
                } else {

                    tempList.clear();
                    lastCalendar = calendar;
                    tempList.add(point);
                }
            }
        }

        return result;
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
            c = DateUtils.toCalendar(dateStr, DateUtils.DATE_FORMAT_yMd);
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

    public static GroupStrategy<Calendar> weekGroupStrategy = new GroupStrategy<Calendar>() {
        @Override
        public boolean isInSameGroup(Calendar left, Calendar right) {
            return DateUtils.isSameWeek(left, right);
        }
    };

    public static GroupStrategy<Calendar> monthGroupStrategy = new GroupStrategy<Calendar>() {
        @Override
        public boolean isInSameGroup(Calendar left, Calendar right) {
            return DateUtils.isSameMonth(left, right);
        }
    };

    public static PointExtractStrategy averageStrategy = new PointExtractStrategy() {
        @Override
        public LineChartPoint extractRepresentPoint(List<LineChartPoint> points) {
            float y = 0;

            for (LineChartPoint point: points) {
                y += point.getYValue();
            }

            AbsLineChartPoint reuslt = new AbsLineChartPoint();
            reuslt.yValue = y / points.size();
            reuslt.xValue = points.get(0).getXValue();

            return reuslt;
        }
    };

    public static PointExtractStrategy maxStrategy = new PointExtractStrategy() {
        @Override
        public LineChartPoint extractRepresentPoint(List<LineChartPoint> points) {
            float max = 0;

            for (LineChartPoint point: points) {
                if (point.getYValue() > max) {
                    max = point.getYValue();
                }
            }

            AbsLineChartPoint reuslt = new AbsLineChartPoint();
            reuslt.yValue = max;
            reuslt.xValue = points.get(0).getXValue();

            return reuslt;
        }
    };

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

    private interface GroupStrategy<T> {
        boolean isInSameGroup(T left, T right);
    }

    private interface PointExtractStrategy {
        // extract one point which can represent a group points
        LineChartPoint extractRepresentPoint(List<LineChartPoint> points);
    }

    public static class XYValues {
        public float maxYValue;
        public ArrayList<String> xVals = new ArrayList<String>();
        public ArrayList<Entry> yVals = new ArrayList<Entry>();
    }


}
