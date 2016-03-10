package com.ai2020lab.pigadopted.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ai2020lab.pigadopted.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 16/3/7.
 */
public class PigDetailFragment extends Fragment {

    private FrameLayout mPigPartsContainer;
    private RecyclerView mBuyerRecyclerView;


    public PigDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_pig_detail, container, false);

        mPigPartsContainer = (FrameLayout) rootView.findViewById(R.id.pig_parts_container);
        mBuyerRecyclerView = (RecyclerView) rootView.findViewById(R.id.buyer_list);

        displayPig();
        loadBuyerList();

        return rootView;
    }

    private void displayPig() {
        List<PigPart> partList = new ArrayList<>();

        PigPart part = new PigPart();
        part.partId = "1";
        partList.add(part);

        part = new PigPart();
        part.partId = "10";
        partList.add(part);

        part = new PigPart();
        part.partId = "6";
        partList.add(part);

        assemblePigParts(partList);

    }

    private void loadBuyerList() {
        List<Buyer> buyers = new ArrayList<>();
        Buyer buyer = new Buyer();
        buyer.portrait = "http://tse4.mm.bing.net/th?id=OIP.Md7bcb36c7db90393682b4bf44487d9f2o0&pid=15.1";
        buyer.name = "小沈阳";
        buyers.add(buyer);

        buyer = new Buyer();
        buyer.portrait = "http://tse4.mm.bing.net/th?id=OIP.Md7bcb36c7db90393682b4bf44487d9f2o0&pid=15.1";
        buyer.name = "赵四";
        buyers.add(buyer);

        buyer = new Buyer();
        buyer.portrait = "http://tse4.mm.bing.net/th?id=OIP.Md7bcb36c7db90393682b4bf44487d9f2o0&pid=15.1";
        buyer.name = "鸭蛋";
        buyers.add(buyer);

        buyer = new Buyer();
        buyer.portrait = "http://tse4.mm.bing.net/th?id=OIP.Md7bcb36c7db90393682b4bf44487d9f2o0&pid=15.1";
        buyer.name = "张三";
        buyers.add(buyer);


        mBuyerRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBuyerRecyclerView.setLayoutManager(mLayoutManager);
        mBuyerRecyclerView.setAdapter(new BuyerAdapter(getContext(), buyers));
    }

    private void assemblePigParts(List<PigPart> partList) {
        if (partList == null || partList.size() == 0) {
            return;
        }

        final int firstPartId = 1;
        final int firstPartImageId = R.mipmap.pig_part_01;

        for (PigPart part : partList) {

            final int partImageId = firstPartImageId + (Integer.parseInt(part.partId) - firstPartId);

            ImageView image = new ImageView(getContext());
            image.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

            image.setImageResource(partImageId);

            mPigPartsContainer.addView(image);
        }
    }


    private class PigPart {
        String name;
        String partId;
    }

    private class Buyer {
        String name;
        String portrait;
    }

    private class BuyerAdapter extends
            RecyclerView.Adapter<BuyerAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<Buyer> mBuyers;

        public BuyerAdapter(Context context, List<Buyer> buyers) {
            mInflater = LayoutInflater.from(context);
            mBuyers = buyers;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View view) {
                super(view);
            }

            ImageView mPortrait;
            TextView mName;
        }

        @Override
        public int getItemCount() {
            return mBuyers.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.item_buyer,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.mPortrait = (ImageView) view
                    .findViewById(R.id.buyer_portrait);
            viewHolder.mName = (TextView) view.findViewById(R.id.buyer_name);

            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int index) {
            final ImageLoader imageLoader = ImageLoader.getInstance();

            final Buyer buyer = mBuyers.get(index);

            viewHolder.mName.setText(buyer.name);
            imageLoader.displayImage(buyer.portrait, viewHolder.mPortrait);
        }

    }

}
