package com.ai2020lab.pigadopted.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.model.order.OrderInfo;
import com.ai2020lab.pigadopted.model.order.OrderInfoForSeller;
import com.ai2020lab.pigadopted.model.order.PigPart;
import com.ai2020lab.pigadopted.model.pig.GrowthInfo;
import com.ai2020lab.pigadopted.model.pig.HealthInfo;
import com.ai2020lab.pigadopted.model.pig.PigCategory;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfo;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrder;
import com.ai2020lab.pigadopted.model.pig.PigDetailInfoAndOrderResponse;
import com.ai2020lab.pigadopted.model.pig.PigInfo;
import com.ai2020lab.pigadopted.model.user.UserInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Rocky on 16/3/7.
 */
public class PigDetailFragment extends Fragment {

    private FrameLayout mPigPartsContainer;
    private RecyclerView mBuyerRecyclerView;
    private ImageView mWholePig;
    private Button mWeightChartBtn;
    private TextView mBuyerNumber;
    private TextView mIncreaseWeight;
    private TextView mPigTypeName;
    private TextView mPigAge;
    private TextView mPigWeight;
    private TextView mPigTemperature;
    private TextView mPigFatRate;
    private TextView mPigSteps;


    public PigDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_pig_detail, container, false);

        setupMainLayout(rootView);
        setupOtherViews(rootView);

        final PigDetailInfoAndOrderResponse response = loadData();
        final PigDetailInfoAndOrder result = response.data;

        setupPigInfo(result);
        displayPig(result.orderInfo.pigParts);
        loadBuyerList(result.orderInfo.pigParts);
        setChartsButtonListener();


        return rootView;
    }

    protected void setupMainLayout(View rootView) {
        mPigPartsContainer = (FrameLayout) rootView.findViewById(R.id.pig_parts_container);
        mWholePig = (ImageView) rootView.findViewById(R.id.whole_pig);
        mWeightChartBtn = (Button) rootView.findViewById(R.id.weight_chart);

        mIncreaseWeight = (TextView) rootView.findViewById(R.id.pig_detail_increase_weight);
        mPigTypeName = (TextView) rootView.findViewById(R.id.pig_type_name);
        mPigAge = (TextView) rootView.findViewById(R.id.pig_age);
        mPigWeight = (TextView) rootView.findViewById(R.id.pig_weight);
        mPigTemperature = (TextView) rootView.findViewById(R.id.pig_temperature);
        mPigFatRate = (TextView) rootView.findViewById(R.id.pig_fat_rate);
        mPigSteps = (TextView) rootView.findViewById(R.id.pig_steps);
    }

    protected void setupOtherViews(View rootView) {
        mBuyerRecyclerView = (RecyclerView) rootView.findViewById(R.id.buyer_list);
        mBuyerNumber = (TextView) rootView.findViewById(R.id.buyer_number);
    }

    protected PigDetailInfoAndOrderResponse loadData() {
        PigDetailInfoAndOrderResponse response = new PigDetailInfoAndOrderResponse();

        PigDetailInfo detailInfo = new PigDetailInfo();

        PigInfo pigInfo = new PigInfo();
        pigInfo.attendedAge = 2;
        detailInfo.pigInfo = pigInfo;

        PigCategory category = new PigCategory();
        category.categoryName = "内江猪";
        pigInfo.pigCategory = category;

        GrowthInfo growthInfo = new GrowthInfo();
        growthInfo.increasedWeight = 10;
        growthInfo.pigWeight = 20;


        detailInfo.growthInfo = growthInfo;

        HealthInfo healthInfo = new HealthInfo();
        healthInfo.fatRate = 24;
        healthInfo.steps = 46;
        healthInfo.temperature = 38.2f;

        detailInfo.healthInfo = healthInfo;


        OrderInfo orderInfo = new OrderInfo();

        List<PigPart> partList = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            PigPart part = new PigPart();
            part.partID = "" + (i + 1);

            UserInfo userInfo = new UserInfo();
            userInfo.userID = "" + (i + 1);
            userInfo.userName = "买家" + (i + 1);
            userInfo.userPortrait = "http://tse4.mm.bing.net/th?id=OIP.Md7bcb36c7db90393682b4bf44487d9f2o0&pid=15.1";

            part.userInfo = userInfo;

            partList.add(part);
        }

        orderInfo.pigParts = partList;

        PigDetailInfoAndOrder result = new PigDetailInfoAndOrder();
        result.pigInfo = pigInfo;
        result.growthInfo = growthInfo;
        result.healthInfo = healthInfo;
        result.orderInfo = orderInfo;

        response.data = result;

        return response;
    }

    private void setupPigInfo(PigDetailInfoAndOrder result) {
        mIncreaseWeight.setText(String.format(getResources().getString(R.string.pig_detail_weight_increase), result.growthInfo.increasedWeight / 2));
        mPigTypeName.setText(result.pigInfo.pigCategory.categoryName);
        mPigSteps.setText(String.format(getResources().getString(R.string.pig_detail_steps), result.healthInfo.steps));
        mPigFatRate.setText(result.healthInfo.fatRate + "%");
        mPigTemperature.setText(result.healthInfo.temperature + "℃");
        mPigAge.setText(String.format(getResources().getString(R.string.pig_detail_age), result.pigInfo.attendedAge));
    }

    private void displayPig(List<PigPart> partList) {

        assemblePigParts(partList);
    }

    private void loadBuyerList(List<PigPart> pigParts) {

        List<UserInfo> buyers = new ArrayList<>();
        Map<String, UserInfo> userMap = new HashMap<>();

        for (PigPart pigPart : pigParts) {
            final UserInfo user = pigPart.userInfo;

            if (!userMap.containsKey(user.userID)) {
                userMap.put(user.userID, user);
            }
        }


        final Set<String> keys = userMap.keySet();

        for (String key : keys) {
            buyers.add(userMap.get(key));
        }

        mBuyerNumber.setText(String.format(getResources().getString(R.string.pig_detail_buyer_number), buyers.size()));

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

            final int partImageId = firstPartImageId + (Integer.parseInt(part.partID) - firstPartId);

            ImageView image = new ImageView(getContext());
            image.setLayoutParams(mWholePig.getLayoutParams());
            image.setScaleType(mWholePig.getScaleType());

            image.setImageResource(partImageId);

            mPigPartsContainer.addView(image);
            startPigPartAnim(image);
        }
    }

    private void startPigPartAnim(View animatedView) {
        Random random = new Random();

        int animationId = 0;

        switch (random.nextInt(4)) {
            case 0:
                animationId = R.anim.push_left_in;
                break;
            case 1:
                animationId = R.anim.push_top_in;
                break;
            case 2:
                animationId = R.anim.push_right_in;
                break;
            case 3:
                animationId = R.anim.push_bottom_in;
                break;
            default:
                animationId = R.anim.push_left_in;
        }

        Animation anim = AnimationUtils.loadAnimation(getContext(), animationId);
        anim.setDuration(1000);
        animatedView.startAnimation(anim);

    }

    private void setChartsButtonListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayMetrics metric = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
                int width = metric.widthPixels;  // 屏幕宽度（像素）
                int height = metric.heightPixels;  // 屏幕高度（像素


                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");

                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                final float scale = metric.density;
                // dp to px
                int marginWidth = (int) (15 * scale + 0.5f);
                int marginHeight = (int) (300 * scale + 0.5f);

                DialogFragment newFragment = StatisticsChartFragment.newInstance(width - marginWidth, height - marginHeight);


                newFragment.show(ft, "dialog");

            }
        };

        mWeightChartBtn.setOnClickListener(listener);

    }

    private class BuyerAdapter extends
            RecyclerView.Adapter<BuyerAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<UserInfo> mBuyers;

        public BuyerAdapter(Context context, List<UserInfo> buyers) {
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

            final UserInfo buyer = mBuyers.get(index);

            viewHolder.mName.setText(buyer.userName);
            imageLoader.displayImage(buyer.userPortrait, viewHolder.mPortrait);
        }

    }

}
