package com.ai2020lab.pigadopted.activity;

import android.os.Bundle;

import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;

public class PigDetailActivity extends AIBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pig_detail);

        supportToolbar(true);
        setToolbarTitle("详细信息");
        setToolbarLeft(R.drawable.pig_detail_back_selector);
        setToolbarRight(R.drawable.pig_detail_grow_history_selector);
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
