package com.ai2020lab.pigadopted.fragment;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.pigadopted.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ai2020lab.pigadopted.model.statistic.BodyTemperatureData;
import com.ai2020lab.pigadopted.model.statistic.StepData;
import com.ai2020lab.pigadopted.model.statistic.WeightData;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

/**
 * Created by Rocky on 16/3/14.
 */
public class StatisticsChartFragment extends DialogFragment implements OnChartGestureListener  {

    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String TYPE = "type";
    private static final String DATA_SET = "dataSet";

    public static final int CHART_TYPE_STEPS = 1;
    public static final int CHART_TYPE_TEMPERATURE = 2;
    public static final int CHART_TYPE_WEIGHT = 3;

    private List<WeightData> mWeightDataSet;
    private List<StepData> mStepDataSet;
    private List<BodyTemperatureData> mTemperatureDataSet;

    private LineChart mChart;
    private ImageView mChartIcon;
    private TextView mChartTitle;

    private int mWidth = -1;
    private int mHeight = -1;
    private int mType = CHART_TYPE_STEPS;

    private float mMaxYValue;

    static StatisticsChartFragment newInstance(int width, int height, int chartType) {
        StatisticsChartFragment f = new StatisticsChartFragment();

        Bundle args = new Bundle();
        args.putInt(WIDTH, width);
        args.putInt(HEIGHT, height);
        args.putInt(TYPE, chartType);

        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);

        mWidth = getArguments().getInt(WIDTH);
        mHeight = getArguments().getInt(HEIGHT);
        mType = getArguments().getInt(TYPE);

        Serializable list = getArguments().getSerializable(DATA_SET);

        switch (mType) {
            case CHART_TYPE_STEPS:
                mStepDataSet = (List<StepData>) list;
                break;
            case CHART_TYPE_WEIGHT:
                mWeightDataSet = (List<WeightData>) list;
                break;
            case CHART_TYPE_TEMPERATURE:
                mTemperatureDataSet = (List<BodyTemperatureData>) list;
                break;
            default:
                break;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_statistics_chart, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.chart);
        mChartIcon = (ImageView) rootView.findViewById(R.id.chart_type_icon);
        mChartTitle = (TextView) rootView.findViewById(R.id.chart_type_title);

        setChartIconAndTitle(mType);

        setChartProperties();
        setChartAxis();


        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data

        setChartData();
    //    setData(45, 100);

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(1000, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        setChartLegend();

        // // dont forget to refresh the drawing
        // mChart.invalidate();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (mWidth > 0 && mHeight > 0) {
            getDialog().getWindow().setLayout(mWidth, mHeight);
        }
    }

    private void setChartIconAndTitle(int chartType) {
        int iconID;
        int titleID;

        switch (chartType) {
            case CHART_TYPE_STEPS:
                iconID = R.mipmap.pig_chart_icon_steps;
                titleID = R.string.pig_chart_title_steps;
                break;
            case CHART_TYPE_TEMPERATURE:
                iconID = R.mipmap.pig_chart_icon_temperature;
                titleID = R.string.pig_chart_title_temperature;
                break;
            case CHART_TYPE_WEIGHT:
                iconID = R.mipmap.pig_chart_icon_weight;
                titleID = R.string.pig_chart_title_weight;
                break;
            default:
                iconID = R.mipmap.pig_chart_icon_weight;
                titleID = R.string.pig_chart_title_weight;
                break;
        }

        mChartIcon.setImageResource(iconID);
        mChartTitle.setText(titleID);
    }

    private void setChartProperties() {
        mChart.setOnChartGestureListener(this);
        //    mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
    }




    private void setChartAxis() {
        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        //     Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

//        LimitLine ll1 = new LimitLine(130f, "Upper Limit");
//        ll1.setLineWidth(4f);
//        ll1.enableDashedLine(10f, 10f, 0f);
//        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//        ll1.setTextSize(10f);
        //     ll1.setTypeface(tf);

//        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
//        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);
//        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        ll2.setTextSize(10f);
        //     ll2.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1);
//        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaxValue(220f);
        leftAxis.setAxisMinValue(-50f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);
    }

    private void setChartLegend() {
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
    //    l.setForm(Legend.LegendForm.LINE);


    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        changeDataSet();
//        if (mChart.getAlpha() < 0.01) {
//            mChart.animate().alpha(1f)
//                    .setDuration(1000);
//        } else {
//            mChart.animate().alpha(0f)
//                    .setDuration(1000);
//        }
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
//        mChart.animate().alpha(0f)
//                .setDuration(5000);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }


    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
   //     set1.enableDashedLine(10f, 5f, 0f);
   //     set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setDrawCubic(true);

        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(true);
        set1.setValueTextSize(9f);
    //    Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.chart_fade_red);
     //   set1.setFillDrawable(drawable);

        set1.setDrawFilled(true);

        setChartColor(set1);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

    private void setChartColor(LineDataSet set) {
        set.setColor(getResources().getColor(R.color.pig_chart_weight_line));
        set.setCircleColor(getResources().getColor(R.color.pig_chart_weight_line));
        set.setFillColor(getResources().getColor(R.color.pig_chart_weight_fill));
    }

    private void setChartData() {
        List<WeightData> list = loadWeightDataForWeeks();
        LineData data = createChartData(list);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaxValue(mMaxYValue + 5);
        mChart.setData(data);

        mChart.setVisibleXRangeMaximum(5);
    }

    private LineData createChartData(List<WeightData> dataList) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();


        for (int i = 0; i < dataList.size(); i++) {
            final WeightData data = dataList.get(i);

            xVals.add(data.date);
            yVals.add(new Entry(data.weight, i));

            if (mMaxYValue < data.weight) {
                mMaxYValue = data.weight;
            }
        }


        LineDataSet set1 = new LineDataSet(yVals, "Weight data set");

        set1.setDrawCubic(true);

        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(true);
        set1.setValueTextSize(9f);


        set1.setDrawFilled(true);

        setChartColor(set1);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);


        return new LineData(xVals, dataSets);

    }

    private List<WeightData> loadWeightData() {
        List<WeightData> list = new ArrayList<>();

        WeightData data = new WeightData();

        data.date = "周一";
        data.weight = 10;
        list.add(data);

        data = new WeightData();
        data.date = "周二";
        data.weight = 13;
        list.add(data);

        data = new WeightData();
        data.date = "周三";
        data.weight = 15;
        list.add(data);

        data = new WeightData();
        data.date = "周四";
        data.weight = 19;
        list.add(data);

        data = new WeightData();
        data.date = "周五";
        data.weight = 18;
        list.add(data);

        data = new WeightData();
        data.date = "周六";
        data.weight = 24;
        list.add(data);

        return list;
    }

    private List<WeightData> loadWeightDataForWeeks() {
        List<WeightData> list = new ArrayList<>();
        Random r = new Random();

        for (int i = 1; i < 11; ++i) {
            WeightData data = new WeightData();

            data.date = "第" + i + "周";
            data.weight = 10 * i + 10 * r.nextFloat();
            list.add(data);
        }

        return list;
    }

    private void changeDataSet() {
        List<WeightData> list = loadWeightData();
        LineData data = createChartData(list);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaxValue(mMaxYValue + 5);
        mChart.setData(data);

        mChart.invalidate();

        mChart.setVisibleXRangeMaximum(10);
        mChart.setVisibleXRangeMinimum(5);
    }

}
