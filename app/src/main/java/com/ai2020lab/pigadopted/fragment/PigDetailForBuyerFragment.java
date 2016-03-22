package com.ai2020lab.pigadopted.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.part_list);
    }

    @Override
    protected View inflateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pig_detail_for_buyer, container, false);
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
    protected PigDetailInfoAndOrderResponse loadData() {
        return super.loadData();
    }

    @Override
    protected int getPigPartImageResID(String partID) {
        final int firstPartId = 1;
        final int firstPartImageId = R.mipmap.pig_part_has_number_01;

        return firstPartImageId + (Integer.parseInt(partID) - firstPartId);
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
