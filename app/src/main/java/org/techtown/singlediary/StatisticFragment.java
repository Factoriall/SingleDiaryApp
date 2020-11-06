package org.techtown.singlediary;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    BarChart[] chart2;
    LineChart chart3;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    class PieChartClass{
        PieChart chart;

        public PieChartClass(View viewById) {
            this.chart = (PieChart) viewById;
        }

        public void setPieChartUi(){
            chart.setUsePercentValues(true);
            chart.getDescription().setEnabled(false);

            chart.setCenterText("기분별 비율");

            chart.setTransparentCircleColor(Color.WHITE);
            chart.setTransparentCircleAlpha(110);

            chart.setHoleRadius(58f);
            chart.setTransparentCircleRadius(61f);

            chart.setDrawCenterText(true);

            chart.setHighlightPerTapEnabled(true);

            Legend l = chart.getLegend();
            l.setEnabled(false);

            // entry label styling
            chart.setEntryLabelColor(Color.WHITE);
            chart.setEntryLabelTextSize(12f);

            setPieChartData();
        }

        private void setPieChartData(){
            ArrayList<PieEntry> entries = new ArrayList<>();

            Cursor cursor = database.rawQuery("SELECT condition FROM diary", null);
            int[] smile = new int[5];
            int recordCount = cursor.getCount();
            for(int i=0; i<recordCount; i++) {
                cursor.moveToNext();
                int condition = cursor.getInt(0);
                smile[condition]++;
            }

            if(recordCount != 0) {
                if(smile[0] != 0)
                    entries.add(new PieEntry(getPercentage(smile[0], recordCount), "", getResources().getDrawable(R.drawable.smile1_24)));
                if(smile[1] != 0)
                    entries.add(new PieEntry(getPercentage(smile[1], recordCount), "", getResources().getDrawable(R.drawable.smile2_24)));
                if(smile[2] != 0)
                    entries.add(new PieEntry(getPercentage(smile[2], recordCount), "", getResources().getDrawable(R.drawable.smile3_24)));
                if(smile[3] != 0)
                    entries.add(new PieEntry(getPercentage(smile[3], recordCount), "", getResources().getDrawable(R.drawable.smile4_24)));
                if(smile[4] != 0)
                    entries.add(new PieEntry(getPercentage(smile[4], recordCount), "", getResources().getDrawable(R.drawable.smile5_24)));
            }
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
            chart.setData(data);

            chart.invalidate();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.statistic_fragment, container, false);
        dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();

        PieChartClass chart1 = new PieChartClass(view.findViewById(R.id.chart1));
        chart1.setPieChartUi();

        Resources res = getResources();
        chart2 = new BarChart[7];
        for(int i=0; i<7; i++) {
            String idName = "chart2_" + (i+1);
            chart2[i] = view.findViewById(res.getIdentifier(idName, "id", getActivity().getPackageName()));
            setBarChartUi(chart2[i]);
        }

        chart3 = view.findViewById(R.id.chart3);
        setLineChartUi(chart3);

        return view;
    }

    private float getPercentage(int divident, int divider){
        return divident / (float)divider * 100.0f;
    }

    private void setBarChartUi(BarChart chart){
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);

        chart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = chart.getLegend();
        l.setEnabled(false);

        setBarChartData(chart);
    }

    public void setBarChartData(BarChart chart){
        ArrayList<BarEntry> values = new ArrayList<>();

        values.add(new BarEntry(0, 10.0f, getResources().getDrawable(R.drawable.smile1_24)));
        values.add(new BarEntry(1, 20.0f, getResources().getDrawable(R.drawable.smile2_24)));
        values.add(new BarEntry(2, 5.0f, getResources().getDrawable(R.drawable.smile3_24)));
        values.add(new BarEntry(3, 30.0f, getResources().getDrawable(R.drawable.smile4_24)));
        values.add(new BarEntry(4, 50.0f, getResources().getDrawable(R.drawable.smile5_24)));

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

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

            chart.setData(data);
        }
    }

    private void setLineChartUi(LineChart chart){
        // background color
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners
        chart.setDrawGridBackground(false);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(100f);
            yAxis.setAxisMinimum(0f);
        }

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        setLineChartData(chart);
    }

    private void setLineChartData(final LineChart chart) {
        ArrayList<Entry> values = new ArrayList<>();

        values.add(new Entry(0, 20.0f, getResources().getDrawable(R.drawable.smile1_24)));
        values.add(new Entry(1, 40.0f, getResources().getDrawable(R.drawable.smile2_24)));
        values.add(new Entry(2, 60.0f, getResources().getDrawable(R.drawable.smile3_24)));
        values.add(new Entry(3, 10.0f, getResources().getDrawable(R.drawable.smile4_24)));
        values.add(new Entry(4, 80.0f, getResources().getDrawable(R.drawable.smile5_24)));

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
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
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
        }
    }
}
