package org.techtown.singlediary;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.getColor;

public class StatisticFragment extends Fragment {
    PieChart chart1;
    BarChart chart2;
    LineChart chart3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.statistic_fragment, container, false);

        chart1 = view.findViewById(R.id.chart1);
        setChart1Ui();
        chart2 = view.findViewById(R.id.chart2);
        setChart2Ui();
        chart3 = view.findViewById(R.id.chart3);
        setChart3Ui();

        return view;
    }

    private void setChart1Ui(){
        chart1.setUsePercentValues(true);
        chart1.getDescription().setEnabled(false);

        chart1.setCenterText("기분별 비율");

        chart1.setTransparentCircleColor(Color.WHITE);
        chart1.setTransparentCircleAlpha(110);

        chart1.setHoleRadius(58f);
        chart1.setTransparentCircleRadius(61f);

        chart1.setDrawCenterText(true);

        chart1.setHighlightPerTapEnabled(true);

        Legend l = chart1.getLegend();
        l.setEnabled(false);

        // entry label styling
        chart1.setEntryLabelColor(Color.WHITE);
        chart1.setEntryLabelTextSize(12f);

        setData1();
    }

    private void setData1(){
        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile1_24)));
        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile2_24)));
        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile3_24)));
        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile4_24)));
        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile5_24)));

        PieDataSet dataSet = new PieDataSet(entries, "기분별 비율");

        dataSet.setDrawIcons(true);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, -40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart1.setData(data);

        chart1.invalidate();
    }

    private void setChart2Ui(){
        chart2.setDrawBarShadow(false);
        chart2.setDrawValueAboveBar(false);

        chart2.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        chart2.setPinchZoom(false);

        chart2.setDrawGridBackground(false);

        XAxis xAxis = chart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);

        YAxis leftAxis = chart2.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart2.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = chart2.getLegend();
        l.setEnabled(false);

        setData2();
    }

    public void setData2(){
        ArrayList<BarEntry> values = new ArrayList<>();

        values.add(new BarEntry(0, 10.0f, getResources().getDrawable(R.drawable.smile1_24)));
        values.add(new BarEntry(1, 20.0f, getResources().getDrawable(R.drawable.smile2_24)));
        values.add(new BarEntry(2, 5.0f, getResources().getDrawable(R.drawable.smile3_24)));
        values.add(new BarEntry(3, 30.0f, getResources().getDrawable(R.drawable.smile4_24)));
        values.add(new BarEntry(4, 50.0f, getResources().getDrawable(R.drawable.smile5_24)));

        BarDataSet set1;

        if (chart2.getData() != null &&
                chart2.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart2.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart2.getData().notifyDataChanged();
            chart2.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "요일별 기분");

            set1.setDrawIcons(true);
            set1.setIconsOffset(new MPPointF(0, -25));
            set1.setColors(getColor(getContext(), android.R.color.holo_orange_light),
                    getColor(getContext(), android.R.color.holo_blue_light),
                    getColor(getContext(), android.R.color.holo_green_light),
                    getColor(getContext(), android.R.color.holo_red_light),
                    getColor(getContext(), android.R.color.holo_purple));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart2.setData(data);
        }
    }

    private void setChart3Ui(){
        // background color
        chart3.setBackgroundColor(Color.WHITE);

        // disable description text
        chart3.getDescription().setEnabled(false);

        // enable touch gestures
        chart3.setTouchEnabled(true);

        // set listeners
        chart3.setDrawGridBackground(false);

        // enable scaling and dragging
        chart3.setDragEnabled(true);
        chart3.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart3.setPinchZoom(true);

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart3.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart3.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart3.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(100f);
            yAxis.setAxisMinimum(0f);
        }

        // get the legend (only possible after setting data)
        Legend l = chart3.getLegend();
        l.setEnabled(false);

        setData3();
    }

    private void setData3() {
        ArrayList<Entry> values = new ArrayList<>();

        values.add(new Entry(0, 20.0f, getResources().getDrawable(R.drawable.smile1_24)));
        values.add(new Entry(1, 40.0f, getResources().getDrawable(R.drawable.smile2_24)));
        values.add(new Entry(2, 60.0f, getResources().getDrawable(R.drawable.smile3_24)));
        values.add(new Entry(3, 10.0f, getResources().getDrawable(R.drawable.smile4_24)));
        values.add(new Entry(4, 80.0f, getResources().getDrawable(R.drawable.smile5_24)));

        LineDataSet set1;

        if (chart3.getData() != null &&
                chart3.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart3.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart3.getData().notifyDataChanged();
            chart3.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setDrawIcons(true);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart3.getAxisLeft().getAxisMinimum();
                }
            });

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart3.setData(data);
        }
    }
}
