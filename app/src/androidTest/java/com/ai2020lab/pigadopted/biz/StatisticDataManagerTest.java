package com.ai2020lab.pigadopted.biz;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.ai2020lab.pigadopted.model.statistic.WeightStaticResponse;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Rocky on 16/4/5.
 */
public class StatisticDataManagerTest extends AndroidTestCase {

    private static final String TAG = "StatisticTest";


    private StatisticsDataManager statisticsDataManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        statisticsDataManager = new HttpStatisticDataManager(getContext());

    }

    public void testqueryWeightList() {
        final WeightStaticResponse[] response = {null};

        final CountDownLatch lock = new CountDownLatch(1);

        statisticsDataManager.queryWeightList("1", StatisticsDataManager.DataType.DAY,
                null, null, new JsonHttpResponseHandler<WeightStaticResponse>(getContext()) {
                    @Override
                    public void onHandleSuccess(int statusCode, Header[] headers, WeightStaticResponse jsonObj) {
                        response[0] = jsonObj;
                        Log.i(TAG, jsonObj.toString());
                        lock.countDown();
                    }

                    @Override
                    public void onHandleFailure(String errorMsg) {
                        Log.i(TAG, errorMsg);
                        lock.countDown();
                    }

                });


        try {
            lock.await(10000, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(response[0]);

    }
}
