package com.ai2020lab.pigadopted.biz;

import android.content.Context;

import com.ai2020lab.pigadopted.model.statistic.BodyTemperatureResponse;
import com.ai2020lab.pigadopted.model.statistic.StatisticDataRequest;
import com.ai2020lab.pigadopted.model.statistic.StepStaticResponse;
import com.ai2020lab.pigadopted.model.statistic.WeightStaticResponse;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;

/**
 * Created by Rocky on 16/4/5.
 */
public class HttpStatisticDataManager implements StatisticsDataManager {

    private Context mContext;

    public HttpStatisticDataManager(Context context) {
        mContext = context;
    }

    @Override
    public void queryWeightList(String pigID, DataType type, String beginDate, String endDate,
                                JsonHttpResponseHandler<WeightStaticResponse> handler) {

        StatisticDataRequest request = createRequest(pigID, type, beginDate, endDate);

        HttpManager.postJson(mContext, UrlName.PIG_WEIGHT_LIST.getUrl(), request, handler);
    }

    @Override
    public void queryTemperatureList(String pigID, DataType type, String beginDate, String endDate,
                                     JsonHttpResponseHandler<BodyTemperatureResponse> handler) {

        StatisticDataRequest request = createRequest(pigID, type, beginDate, endDate);

        HttpManager.postJson(mContext, UrlName.PIG_TEMPERATURE_LIST.getUrl(), request, handler);
    }

    @Override
    public void queryStepList(String pigID, DataType type, String beginDate, String endDate,
                              JsonHttpResponseHandler<StepStaticResponse> handler) {

        StatisticDataRequest request = createRequest(pigID, type, beginDate, endDate);

        HttpManager.postJson(mContext, UrlName.PIG_STEP_LIST.getUrl(), request, handler);
    }

    private StatisticDataRequest createRequest(String pigID, DataType type, String beginDate, String endDate) {
        StatisticDataRequest request = new StatisticDataRequest();
        request.pigID = pigID;
        request.type = type.getTypeName();
        request.begin = beginDate;
        request.end = endDate;

        return request;
    }
}
