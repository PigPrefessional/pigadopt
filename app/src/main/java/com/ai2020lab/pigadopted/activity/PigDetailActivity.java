package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;

public class PigDetailActivity extends AIBaseActivity {

    public static final String KEY_DETAIL_TYPE = "KeyDetailType";
    public static final int TYPE_SELLER = 1;
    public static final int TYPE_BUYER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int type = getIntent().getExtras().getInt(KEY_DETAIL_TYPE, TYPE_BUYER);
        if (type == TYPE_SELLER) {
            setContentView(R.layout.activity_pig_detail_for_seller);
        } else {
            setContentView(R.layout.activity_pig_detail_for_buyer);
        }

        supportToolbar(true);
        setToolbarTitle(getText(R.string.pig_detail_title).toString());
        setToolbarLeft(R.drawable.toolbar_back_selector);
        setToolbarRight(R.drawable.growth_history_camera_selector);
     /*   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }



}
