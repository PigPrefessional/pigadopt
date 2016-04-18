package com.ai2020lab.pigadopted.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.ToastUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.biz.HttpPigDetailManager;
import com.ai2020lab.pigadopted.biz.PigDetailManager;
import com.ai2020lab.pigadopted.common.DataManager;
import com.ai2020lab.pigadopted.model.order.PigPart;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrderResponse;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.net.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Rocky on 16/3/22.
 */
public class PigDetailForBuyerFragment extends PigDetailForSellerFragment {

    private ImageView mHogpen1;
    private ImageView mHogpen2;
    private ImageView mHogpen3;

    private static final String TAG = "PigDetailForBuyer";

    @Override
    protected void setupOtherViews(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.part_list);
        mHogpen1 = (ImageView) rootView.findViewById(R.id.hogpen_1);
        mHogpen2 = (ImageView) rootView.findViewById(R.id.hogpen_2);
        mHogpen3 = (ImageView) rootView.findViewById(R.id.hogpen_3);
    }

    @Override
    protected View inflateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pig_detail_for_buyer, container, false);
    }

    @Override
    protected void loadPigDetailData(PigInfo pigInfo) {
        setupHogpenImages();

        PigDetailManager pigDetailManager = new HttpPigDetailManager(getContext());

        final AIBaseActivity activity = (AIBaseActivity) getActivity();

        activity.showLoading(getString(R.string.prompt_loading));

        pigDetailManager.findCustomerPigDetailInfo("" + pigInfo.pigID, String.valueOf(DataManager.getInstance().getBuyerInfo().userID),
                new JsonHttpResponseHandler<PigDetailInfoAndOrderResponse>(activity) {
            @Override
            public void onHandleFailure(String errorMsg) {
                Log.i(TAG, errorMsg);
                activity.dismissLoading();
                ToastUtils.getInstance().showToast(activity, errorMsg);
            }

            @Override
            public void onHandleSuccess(int statusCode, Header[] headers, PigDetailInfoAndOrderResponse jsonObj) {
                activity.dismissLoading();

                final PigDetailInfoAndOrderResponse response = jsonObj;
                final PigDetailInfoAndOrder result = response.data;
                response.data.pigInfo = mPigInfo;

                setupPigInfoUI(result);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                activity.dismissLoading();
                ToastUtils.getInstance().showToast(activity, R.string.prompt_loading_failure);
            }
        });
    }

    private void setupHogpenImages() {
        if (mPigInfo.hogpenInfo != null && mPigInfo.hogpenInfo.hogpenPhoto != null) {
            initHogpenImage(mHogpen1, mPigInfo.hogpenInfo.hogpenPhoto);
        }
    }

    @Override
    protected void loadBuyersData(List<PigPart> pigParts) {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new PartsAdapter(getContext(), pigParts));
    }

    @Override
    protected int getPigPartImageResID(PigPart  pigPart) {
        return DataManager.getInstance().getPigPartWithNumberImageResID(pigPart);
    }

    private void initHogpenImage(ImageView imageView, final String url) {
        ImageLoader.getInstance().displayImage(url, imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private class PartsAdapter extends
            RecyclerView.Adapter<PartsAdapter.ViewHolder> {

        private final int COLUMNS = 3;
        private LayoutInflater mInflater;
        private List<PigPart> mParts;

        public PartsAdapter(Context context, List<PigPart> parts) {
            mInflater = LayoutInflater.from(context);
            mParts = parts;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View view) {
                super(view);
            }

            TextView mPart1;
            TextView mPart2;
            TextView mPart3;
        }

        @Override
        public int getItemCount() {
            int mod = mParts.size() % COLUMNS;
            int row = mParts.size() / COLUMNS;

            return mod == 0 ? row : row + 1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.item_buyed_pig_part,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.mPart1 = (TextView) view.findViewById(R.id.buyed_part_1);
            viewHolder.mPart2 = (TextView) view.findViewById(R.id.buyed_part_2);
            viewHolder.mPart3 = (TextView) view.findViewById(R.id.buyed_part_3);

            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int index) {

            final int row = index;


            for (int partIndex = COLUMNS * row, i = 0; partIndex < mParts.size() && i < COLUMNS; i++, partIndex++) {

                final String partName = mParts.get(partIndex).partName;
                final String text = mParts.get(partIndex).partID + ":" + partName;

                switch (i) {
                    case 0:
                        viewHolder.mPart1.setText(text);
                        break;
                    case 1:
                        viewHolder.mPart2.setText(text);
                        break;
                    case 2:
                        viewHolder.mPart3.setText(text);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
