package com.ai2020lab.pigadopted.chart;

import com.ai2020lab.aiutils.common.JsonUtils;
import com.ai2020lab.pigadopted.TestHelper;
import com.ai2020lab.pigadopted.common.DateUtils;
import com.ai2020lab.pigadopted.model.statistic.WeightStaticResponse;

import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Rocky on 16/4/5.
 */
public class ChartDataAdapterTest {

    @Test
    public void groupWeek() {

        WeightStaticResponse response = JsonUtils.getInstance()
                .deserializeToObj(TestHelper.getStringFromFile("testFiles/pigWeightResponse.json"), WeightStaticResponse.class);

        assertEquals(16, response.data.dataList.size());
        List<LineChartPoint> weekGroups = ChartDataAdapter.groupByWeek(LineChartPointFactory.createPointList(response.data.dataList));

        assertEquals(14, weekGroups.size());

        assertEquals("2015-12-01", weekGroups.get(0).getXValue());
        assertEquals("2015-12-08", weekGroups.get(1).getXValue());
        assertEquals("2015-12-15", weekGroups.get(2).getXValue());
        assertEquals("2015-12-22", weekGroups.get(3).getXValue());
        assertEquals("2015-12-29", weekGroups.get(4).getXValue());
        assertEquals("2016-01-08", weekGroups.get(5).getXValue());
        assertEquals("2016-01-15", weekGroups.get(6).getXValue());
        assertEquals("2016-01-22", weekGroups.get(7).getXValue());
        assertEquals("2016-01-29", weekGroups.get(8).getXValue());
        assertEquals("2016-02-01", weekGroups.get(9).getXValue());
        assertEquals("2016-02-08", weekGroups.get(10).getXValue());
        assertEquals("2016-02-15", weekGroups.get(11).getXValue());
        assertEquals("2016-02-22", weekGroups.get(12).getXValue());
        assertEquals("2016-02-29", weekGroups.get(13).getXValue());

    }

    @Test
    public void groupMonth() {

        WeightStaticResponse response = JsonUtils.getInstance()
                .deserializeToObj(TestHelper.getStringFromFile("testFiles/pigWeightResponse.json"), WeightStaticResponse.class);

        assertEquals(16, response.data.dataList.size());
        List<LineChartPoint> weekGroups = ChartDataAdapter.groupByMonth(LineChartPointFactory.createPointList(response.data.dataList));

        assertEquals(4, weekGroups.size());

        assertEquals("2015-12-01", weekGroups.get(0).getXValue());
        assertEquals("2016-01-01", weekGroups.get(1).getXValue());
        assertEquals("2016-02-01", weekGroups.get(2).getXValue());
        assertEquals("2016-03-01", weekGroups.get(3).getXValue());

    }

    @Test
    public void sameWeek() {
        try {
            Calendar leftC = DateUtils.toCalendar("2015-11-30", DateUtils.DATE_FORMAT_yMd);
            Calendar righttC = DateUtils.toCalendar("2015-12-08", DateUtils.DATE_FORMAT_yMd);

            assertFalse(DateUtils.isSameWeek(leftC, righttC));

            righttC = DateUtils.toCalendar("2015-12-05", DateUtils.DATE_FORMAT_yMd);
            assertTrue(DateUtils.isSameWeek(leftC, righttC));

            // over one year test
            leftC = DateUtils.toCalendar("2015-12-30", DateUtils.DATE_FORMAT_yMd);
            righttC = DateUtils.toCalendar("2016-01-02", DateUtils.DATE_FORMAT_yMd);
            assertTrue(DateUtils.isSameWeek(leftC, righttC));

            leftC = DateUtils.toCalendar("2015-12-30", DateUtils.DATE_FORMAT_yMd);
            righttC = DateUtils.toCalendar("2016-01-04", DateUtils.DATE_FORMAT_yMd);
            assertFalse(DateUtils.isSameWeek(leftC, righttC));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
