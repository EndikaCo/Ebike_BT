package com.example.ebike_bt;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class Chart extends Activity{
    //charts
    LineChart chart;
    ILineDataSet set;

     Chart(Activity activity) {

        chart = activity.findViewById(R.id.id_chart);
        // enable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.TRANSPARENT);

        LineData data = new LineData();

        data.setValueTextColor(Color.WHITE);

        // add empty data
        chart.setData(data);

    }


    public void addEntry(double entry) {

        LineData data = chart.getData();

        if (data != null) {

             set = data.getDataSetByIndex(0);

            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();

                data.addDataSet(set);

            }

            chart.setVisibleYRange(-10,75, YAxis.AxisDependency.LEFT);

            data.addEntry(new Entry(set.getEntryCount(), (float) entry), 0);

            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(120);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    public void addEntry2(double entry) {

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set2 = data.getDataSetByIndex(1);

            if (set2 == null) {
                set2 = createSet2();

                data.addDataSet(set2);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) entry),1);

            data.notifyDataChanged();

            chart.notifyDataSetChanged();

            chart.setVisibleXRangeMaximum(120);

            chart.moveViewToX(data.getEntryCount());
        }

    }

    public void addEntry3(double entry) {

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set3 = data.getDataSetByIndex(2);

            if (set3 == null) {
                set3 = createSet3();

                data.addDataSet(set3);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) entry),2);

            data.notifyDataChanged();

            chart.notifyDataSetChanged();

            chart.setVisibleXRangeMaximum(120);

            chart.moveViewToX(data.getEntryCount());

        }

    }


    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Voltage");
       // set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.BLUE);
        //set.setCircleColor(Color.WHITE);
        set.setLineWidth(3f);
        set.setDrawCircles(false);
        //set.setDrawCircleHole(false);
        //set.setDrawCircles(false);
       // set.setCircleRadius(4f);
        //set.setFillAlpha(65);
        //set.setFillColor(ColorTemplate.getHoloBlue());
        //set.setHighLightColor(Color.rgb(244, 117, 117));
        //set.setValueTextColor(Color.WHITE);
        //set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    private LineDataSet createSet2() {

        LineDataSet set2 = new LineDataSet(null, "Amperes");
        set2.setColor(ColorTemplate.getHoloBlue());
        set2.setLineWidth(3f);
        set2.setDrawCircles(false);
        set2.setValueTextColor(Color.WHITE);
        set2.setValueTextSize(9f);
        set2.setDrawValues(false);
        return set2;
    }
    private LineDataSet createSet3() {

        LineDataSet set3 = new LineDataSet(null, "Speed");
        set3.setColor(Color.YELLOW);
        set3.setLineWidth(3f);
        set3.setDrawCircles(false);
        set3.setValueTextColor(Color.WHITE);

        set3.setValueTextSize(9f);
        set3.setDrawValues(true);

        return set3;
    }

}