package com.ai2020lab.pigadopted.chart;

import com.ai2020lab.aiutils.common.JsonUtils;
import com.ai2020lab.pigadopted.TestHelper;
import com.ai2020lab.pigadopted.model.statistic.WeightStaticResponse;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rocky on 16/4/5.
 */
public class ChartDataAdapterTest {

    @Test
    public void isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void groupWeek() {

        String path = System.getProperty("user.dir");

        WeightStaticResponse response = JsonUtils.getInstance()
                .deserializeToObj(TestHelper.getStringFromFile("testFiles/pigWeightResponse.json"), WeightStaticResponse.class);

        List<LineChartPoint> weekGroups = ChartDataAdapter.groupByWeek(LineChartPointFactory.createPointList(response.data.dataList));

        String dataPath = "";
        File f = new File(dataPath);
    }
}
