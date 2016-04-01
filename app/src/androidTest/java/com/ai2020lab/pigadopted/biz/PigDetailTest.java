package com.ai2020lab.pigadopted.biz;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.ai2020lab.aiutils.common.JsonUtils;
import com.ai2020lab.pigadopted.TestHelper;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrderResponse;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Rocky on 16/4/1.
 */
public class PigDetailTest extends AndroidTestCase {

    private final String TAG = "PigDetailTest";
    private PigDetailManager mPigDetailManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mPigDetailManager = new HttpPigDetailManager(getContext());

    }

    @SmallTest
    public void testFindSellerPigDetailInfo() {
        final PigDetailInfoAndOrderResponse[] response = {null};

        final CountDownLatch lock = new CountDownLatch(1);
        mPigDetailManager.findSellerPigDetailInfo("1", new JsonHttpResponseHandler<PigDetailInfoAndOrderResponse>(getContext()) {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG, responseString);
                lock.countDown();
            }

            @Override
            public void onHandleSuccess(int statusCode, Header[] headers, PigDetailInfoAndOrderResponse jsonObj) {
                response[0] = jsonObj;
                Log.i(TAG, jsonObj.toString());
                lock.countDown();
            }
        });

        try {
            lock.await(2000, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(response[0]);

    }

    @SmallTest
    public void testParsePigDetailJson() {
        PigDetailInfoAndOrderResponse response
                = JsonUtils.getInstance()
                .deserializeToObj(TestHelper.getFromAsset(getContext(), "jsonFiles/pigDetailResponse.json"), PigDetailInfoAndOrderResponse.class);

        assertNotNull(response);
    }
}
