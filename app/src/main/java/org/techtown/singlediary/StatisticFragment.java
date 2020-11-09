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
import android.util.Log;
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
import com.github.mikephil.charting.components.AxisBase;
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Arrays;

import static androidx.core.content.ContextCompat.getColor;

public class StatisticFragment extends Fragment {
    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    class PieChartClass{
        PieChart chart;

        PieChartClass(View viewById) {
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
                for(int i=0; i<5; i++){
                    if(smile[i] != 0){
                        String idName = "smile" + (i+1) + "_24";
                        final int resourceId = getResources().getIdentifier(idName, "drawable", getActivity().getPackageName());
                        entries.add(new PieEntry(getPercentage(smile[i], recordCount), "", getResources().getDrawable(resourceId)));
                    }
                }
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

    class BarChartClass{
        BarChart chart;
        int dayOfTheWeek;

        BarChartClass(View viewById, int dayOfTheWeek) {
            this.chart = (BarChart) viewById;
            this.dayOfTheWeek = dayOfTheWeek;
        }

        public void setBarChartUi(){
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
            xAxis.setDrawLabels(false);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(30f);
            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            leftAxis.setAxisMaximum(110f);

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setEnabled(false);

            Legend l = chart.getLegend();
            l.setEnabled(false);

            setBarChartData();
        }

        private void setBarChartData(){
            ArrayList<BarEntry> values = new ArrayList<>();

            Cursor cursor = database.rawQuery("SELECT condition FROM diary WHERE strftime('%w', date) IN ('" + dayOfTheWeek + "')", null);

            int[] smile = new int[5];
            int recordCount = cursor.getCount();
            for(int i=0; i<recordCount; i++) {
                cursor.moveToNext();
                int condition = cursor.getInt(0);
                smile[condition]++;
            }

            for(int i=0; i<5; i++){
                String idName = "smile" + (i+1) + "_24";
                final int resourceId = getResources().getIdentifier(idName, "drawable", getActivity().getPackageName());
                values.add(new BarEntry(i, getPercentage(smile[i], recordCount), getResources().getDrawable(resourceId)));
            }

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
    }

    class LineChartClass {
        LineChart chart;

        LineChartClass(View viewById) {
            this.chart = (LineChart) viewById;
        }

        public void setLineChartUi() {
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

                xAxis.setLabelCount(5, /*force: */true);
                xAxis.setAxisMaximum(4);
                xAxis.setAxisMinimum(0);

                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                xAxis.setDrawGridLines(false);
            }

            YAxis yAxis;
            {   // // Y-Axis Style // //
                yAxis = chart.getAxisLeft();

                // disable dual axis (only use LEFT axis)
                chart.getAxisRight().setEnabled(false);


                // axis range
                yAxis.setAxisMaximum(110f);
                yAxis.setAxisMinimum(0f);
            }

            // get the legend (only possible after setting data)
            Legend l = chart.getLegend();
            l.setEnabled(false);

            setLineChartData();
        }

        private void setLineChartData() {
            ArrayList<Entry> values = new ArrayList<>();

            Cursor cursor = database.rawQuery("SELECT condition, date FROM diary ORDER BY date(date) DESC LIMIT 5", null);
            int recordCount = cursor.getCount();

            int[] dateCondition = new int[5];
            final String[] dateString = new String[]{"","","","",""};

            for(int i=4; i>=5-recordCount; i--) {

                cursor.moveToNext();
                switch(cursor.getInt(0)){
                    case 0:
                        dateCondition[i] = 20;
                        break;
                    case 1:
                        dateCondition[i] = 40;
                        break;
                    case 2:
                        dateCondition[i] = 60;
                        break;
                    case 3:
                        dateCondition[i] = 80;
                        break;
                    case 4:
                        dateCondition[i] = 100;
                        break;
                }
                String[] dateInfo = cursor.getString(1).split("-");
                dateString[i] = dateInfo[1] + "/" + dateInfo[2];
            }

            for(int i=0; i<5; i++){
                values.add(new Entry(i, dateCondition[i]));
            }

            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawLabels(true);
            xAxis.setValueFormatter(new ValueFormatter(){
                @Override
                public String getFormattedValue(float value) {
                    Log.d("formattedValue", Float.toString(value));
                    return dateString[(int)value];
                }
            });

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

                set1.setDrawIcons(false);

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
                set1.setFormSize(15.f);

                // text size of values
                set1.setValueTextSize(9f);

                set1.setDrawValues(false);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.statistic_fragment, container, false);
        dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();

        PieChartClass chart1 = new PieChartClass(view.findViewById(R.id.chart1));
        chart1.setPieChartUi();

        BarChartClass[] chart2 = new BarChartClass[7];
        Resources res = getResources();
        for(int i=0; i<7; i++) {
            String idName = "chart2_" + (i+1);
            chart2[i] = new BarChartClass(view.findViewById(res.getIdentifier(idName, "id", getActivity().getPackageName())), i);
            chart2[i].setBarChartUi();
        }

        LineChartClass chart3 = new LineChartClass(view.findViewById(R.id.chart3));
        chart3.setLineChartUi();

        return view;
    }

    private float getPercentage(int divident, int divider){
        if(divider == 0)
            return 0.0f;
        return divident / (float)divider * 100.0f;
    }
}
