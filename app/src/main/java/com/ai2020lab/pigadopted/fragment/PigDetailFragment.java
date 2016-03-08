package com.ai2020lab.pigadopted.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ai2020lab.pigadopted.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 16/3/7.
 */
public class PigDetailFragment extends Fragment {

    private FrameLayout mPigPartsContainer;


    public PigDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_pig_detail, container, false);

        mPigPartsContainer = (FrameLayout) rootView.findViewById(R.id.pig_parts_container);

        displayPig();

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

}
