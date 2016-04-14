package com.ai2020lab.pigadopted.chart;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.ai2020lab.aiutils.common.JsonUtils;
import com.ai2020lab.pigadopted.TestHelper;
import com.ai2020lab.pigadopted.model.statistic.WeightStaticResponse;

import java.util.List;

/**
 * Created by Rocky on 16/4/13.
 */
public class ChartDataAdapterTest extends AndroidTestCase {

    @SmallTest
    public void testGroupByWeek() {
        WeightStaticResponse response = JsonUtils.getInstance()
                .deserializeToObj(TestHelper.getFromAsset(getContext(), "jsonFiles/pigDetailResponse.json"), WeightStaticResponse.class);

        List<LineChartPoint> weekGroups = ChartDataAdapter.groupByWeek(LineChartPointFactory.createPointList(response.data.dataList));
    }
}
