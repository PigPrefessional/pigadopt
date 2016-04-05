package com.ai2020lab.pigadopted.biz;

import android.content.Context;

import com.ai2020lab.pigadopted.model.pig.PigDetailAndOrderRequest;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrderResponse;
import com.ai2020lab.pigadopted.net.HttpManager;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.ai2020lab.pigadopted.net.UrlName;

/**
 * Created by Rocky on 16/4/1.
 */
public class HttpPigDetailManager implements PigDetailManager {
    private Context mContext;

    public HttpPigDetailManager(Context context) {
        mContext = context;
    }

    @Override
    public void findSellerPigDetailInfo(String pigID, JsonHttpResponseHandler<PigDetailInfoAndOrderResponse> handler) {
        PigDetailAndOrderRequest request = new PigDetailAndOrderRequest();
        request.pigID = pigID;

        HttpManager.postJson(mContext, UrlName.PIG_INFO_SELLER.getUrl(), request, handler);
    }

    @Override
    public void findCustomerPigDetailInfo(String pigID, String userID, JsonHttpResponseHandler<PigDetailInfoAndOrderResponse> handler) {
        PigDetailAndOrderRequest request = new PigDetailAndOrderRequest();
        request.pigID = pigID;
        request.userID = userID;

        HttpManager.postJson(mContext, UrlName.PIG_INFO_CUSTOMER.getUrl(), request, handler);
    }
}
