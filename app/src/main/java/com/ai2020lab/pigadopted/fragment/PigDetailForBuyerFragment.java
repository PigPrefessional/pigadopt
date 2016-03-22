package com.ai2020lab.pigadopted.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.model.order.PigPart;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrderResponse;

import java.util.List;

/**
 * Created by Rocky on 16/3/22.
 */
public class PigDetailForBuyerFragment extends PigDetailForSellerFragment {


    @Override
    protected void setupOtherViews(View rootView) {

    }

    @Override
    protected View inflateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pig_detail_for_buyer, container, false);
    }

    @Override
    protected void loadBuyersData(List<PigPart> pigParts) {

    }


    @Override
    protected PigDetailInfoAndOrderResponse loadData() {
        return super.loadData();
    }

    @Override
    protected int getPigPartImageResID(String partID) {
        final int firstPartId = 1;
        final int firstPartImageId = R.mipmap.pig_part_has_number_01;

        return firstPartImageId + (Integer.parseInt(partID) - firstPartId);
    }
}
