package com.ai2020lab.pigadopted.fragment;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ai2020lab.aiviews.anim.AnimSimpleListener;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.chart.ChartDataAdapter;
import com.ai2020lab.pigadopted.chart.LineChartPoint;
import com.ai2020lab.pigadopted.chart.LineChartPointFactory;
import com.ai2020lab.pigadopted.chart.PigLineChartTouchListener;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 16/3/14.
 */
public abstract class StatisticsChartFragment<T> extends DialogFragment implements OnChartGestureListener  {

    private static final String TAG = "Statistics";

    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String DATA_SET = "dataSet";

    public static final int CHART_TYPE_STEPS = 1;
    public static final int CHART_TYPE_TEMPERATURE = 2;
    public static final int CHART_TYPE_WEIGHT = 3;

    private List<LineChartPoint> mPoints;

    private final int FADE_IN_DURATION = 1000;
    private final int FADE_OUT_DURATION = 1000;

    private LineChart mChart;
    private ImageView mChartIcon;
    private TextView mChartTitle;

    private int mWidth = -1;
    private int mHeight = -1;


    private float mMaxYValue;

    protected final int VIEW_DAY = 0;
    protected final int VIEW_WEEK = 1;
    protected final int VIEW_MONTH = 2;

    protected int mViewType = VIEW_DAY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);

        mWidth = getArguments().getInt(WIDTH);
        mHeight = getArguments().getInt(HEIGHT);

        List<T> dataList = (List<T>) getArguments().getSerializable(DATA_SET);
        mPoints = LineChartPointFactory.createPointList(dataList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_statistics_chart, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.chart);
        mChartIcon = (ImageView) rootView.findViewById(R.id.chart_type_icon);
        mChartTitle = (TextView) rootView.findViewById(R.id.chart_type_title);

        setChartIconAndTitle();

        setChartProperties();
        setChartAxis();

        mViewType = VIEW_DAY;

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);


        setChartData(convertDataSet(mPoints));


        mChart.animateX(1000, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        setChartLegend();

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

    protected abstract int getTitleID();
    protected abstract int getIconID();

    private void setChartIconAndTitle() {
        int titleID = getTitleID();
        int iconID = getIconID();

        mChartIcon.setImageResource(iconID);
        mChartTitle.setText(titleID);
    }

    private void setChartProperties() {
        mChart.setOnTouchListener(new PigLineChartTouchListener(mChart, mChart.getViewPortHandler().getMatrixTouch()));
        mChart.setOnChartGestureListener(this);
        //    mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        setChartTouchEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
      //  mChart.setPinchZoom(false);

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
        xAxis.setYOffset(15);
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
        leftAxis.setXOffset(15);
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

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

        if (scaleX > 1.3) {
            boolean canZoomIn = mViewType != VIEW_DAY;
            if (canZoomIn) {
                zoomIn();
            }

        } else if (scaleX < 0.8) {
            boolean canZoomOut = mViewType != VIEW_MONTH;
            if (canZoomOut) {
                zoomOut();
            }
        }
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    protected void setChartColor(LineDataSet set) {
        set.setColor(getResources().getColor(R.color.pig_chart_weight_line));
        set.setCircleColor(getResources().getColor(R.color.pig_chart_weight_line));
        set.setFillColor(getResources().getColor(R.color.pig_chart_weight_fill));
    }

    private void setChartData(ChartDataAdapter.XYValues xyValues) {
        LineData data = createChartData(xyValues);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaxValue(mMaxYValue + 5);
        mChart.setData(data);

        mChart.setVisibleXRange(1, 7);
        mChart.invalidate();

    }

    protected ChartDataAdapter.XYValues convertDataSet(List<LineChartPoint> dataList) {
        return ChartDataAdapter.convertToChartValues(dataList);
    }



    private LineData createChartData(ChartDataAdapter.XYValues xyValues) {

        mMaxYValue = xyValues.maxYValue;

        LineDataSet set1 = new LineDataSet(xyValues.yVals, "Data set");

        set1.setDrawCubic(true);

        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(true);
        set1.setValueTextSize(9f);


        set1.setDrawFilled(true);

        setChartColor(set1);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);


        return new LineData(xyValues.xVals, dataSets);

    }

    private void zoomOut() {
        zoomOutViewType();

        startScale();
    }

    protected void zoomOutViewType() {
        switch (mViewType) {
            case VIEW_DAY:
                mViewType = VIEW_WEEK;
                break;
            case VIEW_WEEK:
                mViewType = VIEW_MONTH;
                break;
            default:
                break;
        }
    }

    private void zoomIn() {
        zoomInViewType();

        startScale();
    }

    protected void zoomInViewType() {
        switch (mViewType) {
            case VIEW_MONTH:
                mViewType = VIEW_WEEK;
                break;
            case VIEW_WEEK:
                mViewType = VIEW_DAY;
                break;
            default:
                break;
        }
    }

    private void changeChartDate() {
        ChartDataAdapter.XYValues xyValues = convertToChartValues(mPoints, mViewType);


        if (xyValues != null) {
            setChartData(xyValues);
        }
    }

    protected ChartDataAdapter.XYValues convertToChartValues(List<LineChartPoint> points, int viewType) {
        ChartDataAdapter.XYValues xyValues = null;

        switch (viewType) {
            case VIEW_MONTH:
                xyValues = ChartDataAdapter.convertToChartValues(ChartDataAdapter.groupByMonth(points));
                break;
            case VIEW_WEEK:
                xyValues = ChartDataAdapter.convertToChartValues(ChartDataAdapter.groupByWeek(points));
                break;
            default:
                break;
        }

        return xyValues;
    }

    private void startScale() {
        setChartTouchEnabled(false);

        Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        anim.setDuration(FADE_OUT_DURATION);
        anim.setAnimationListener(new AnimSimpleListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                changeChartDate();
                endScale();
            }
        });

        mChart.startAnimation(anim);
    }

    private void endScale() {
        Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        anim.setDuration(FADE_IN_DURATION);
        anim.setAnimationListener(new AnimSimpleListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                setChartTouchEnabled(true);
            }
        });

        mChart.startAnimation(anim);
    }

    private void setChartTouchEnabled(boolean enabled) {
        mChart.setTouchEnabled(enabled);
        mChart.setDragEnabled(enabled);
        mChart.setScaleXEnabled(enabled);
    }
}
