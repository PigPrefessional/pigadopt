package com.ai2020lab.pigadopted.biz;

import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrderResponse;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;

/**
 * Created by Rocky on 16/4/1.
 */
public interface PigDetailManager {

    void findSellerPigDetailInfo(String pigID, JsonHttpResponseHandler<PigDetailInfoAndOrderResponse> handler);
    void findCustomerPigDetailInfo(String pigID, String userID, JsonHttpResponseHandler<PigDetailInfoAndOrderResponse> handler);
}
