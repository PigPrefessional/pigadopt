package com.ai2020lab.pigadopted.biz;

import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrderResponse;
import com.ai2020lab.pigadopted.model.statistic.BodyTemperatureResponse;
import com.ai2020lab.pigadopted.model.statistic.StepStaticResponse;
import com.ai2020lab.pigadopted.model.statistic.WeightStaticResponse;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;

/**
 * Created by Rocky on 16/4/5.
 */
public interface StatisticsDataManager {

    void queryWeightList(String pigID, DataType type, String beginDate, String endDate, JsonHttpResponseHandler<WeightStaticResponse> handler);
    void queryTemperatureList(String pigID, DataType type, String beginDate, String endDate, JsonHttpResponseHandler<BodyTemperatureResponse> handler);
    void queryStepList(String pigID, DataType type, String beginDate, String endDate, JsonHttpResponseHandler<StepStaticResponse> handler);


    public static enum DataType {
        HOUR("hour"), DAY("day");

        private String typeName;

        private DataType(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }
}
