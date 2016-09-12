/*
 * Copyright (c) 2016, Blue Maestro
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bluemaestro.utility.sdk.views.graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Garrett on 11/08/2016.
 *
 * A Blue Maestro line chart. Includes easy setting of the legend, easy adding of entries,
 * and options such as threshold lines and zones.
 */
public class BMLineChart extends LineChart {

    private LineData data;
    private Map<String, Integer> dataSets;
    private Typeface font;

    private int width;
    private int height;

    private int xRange = 10;

    private List<LimitZone> zones;

    public BMLineChart(Context context) {
        super(context);
        this.data = new LineData();
        this.dataSets = new LinkedHashMap<String, Integer>();
        this.font = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
        this.zones = new ArrayList<LimitZone>();
    }

    public BMLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.data = new LineData();
        this.dataSets = new LinkedHashMap<String, Integer>();
        this.font = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
        this.zones = new ArrayList<LimitZone>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(LimitZone zone : zones) zone.onDraw(canvas);
        super.onDraw(canvas);
    }

    /**
     * Initialises the chart
     * @param description The chart description
     * @param xRange The range of x values to display (unused)
     */
    public void init(String description, int xRange) {
        this.dataSets = new LinkedHashMap<String, Integer>();
        this.width = 900;
        this.height = 900;
        this.xRange = (xRange - 1 > 0) ? xRange - 1 : 0;
        int length = (width < height) ? width : height;

        setDescription(description);
        setDescriptionTypeface(font);
        setDescriptionTextSize(length * 10.0f / 800);

        //animateY(300);
        setNoDataText("No data");
        setNoDataTextDescription("No data");

        YAxis leftAxis = getAxisLeft();
        leftAxis.setTypeface(font);
        leftAxis.setTextSize(length * 10.0f / 800);

        getAxisRight().setEnabled(false);

        XAxis xAxis = getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
    }

    /**
     * Sets the labels of the chart
     * @param labels The data labels
     * @param colors The colors for each label
     */
    public void setLabels(String[] labels, int[] colors) {
        List<ILineDataSet> dataLists = new ArrayList<ILineDataSet>();
        dataSets.clear();
        int length = (width < height) ? width : height;
        for(int index = 0; index < labels.length && index < colors.length; index++) {
            List<Entry> entries = new ArrayList<Entry>();
            LineDataSet dataSet = new LineDataSet(entries, labels[index]);
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setColor(colors[index]);
            dataSet.setCircleColor(colors[index]);
            dataSet.setLineWidth(length * 3.0f / 800);
            dataSet.setCircleRadius(length * 4.0f / 800);
            dataSet.setCircleHoleRadius(length * 2.0f / 800);
            dataSet.setValueTypeface(font);
            dataSets.put(labels[index], index);
            dataLists.add(dataSet);
        }
        this.data = new LineData(dataLists);
        data.setValueTextSize(length * 5.0f / 800);
        setData(data);

        notifyDataSetChanged();

        Legend legend = getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setTextSize(length * 10.0f / 800);
        legend.setTypeface(font);
        legend.setEnabled(true);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        legend.setXEntrySpace(4.0f);

        invalidate();
    }

    /**
     * Adds an entry to the chart
     * @param label The data label to add the entry
     * @param entry The entry to add
     */
    public void addEntry(String label, Entry entry) {
        int index = dataSets.get(label);
        data.addEntry(entry, index);
        update();
        int range = xRange * dataSets.keySet().size();
        moveViewToX(getEntryCount() / range - xRange - 1);
    }

    /**
     * Gets an entry from the chart
     * @param label The data label the entry is under
     * @param index The index of the data value
     * @return The entry
     */
    public Entry getEntry(String label, int index){
        ILineDataSet dataSet = data.getDataSetByIndex(dataSets.get(label));
        return dataSet.getEntryForIndex(index);
    }

    /**
     * Gets the total number of entries
     * E.g. 3 data * 20 values = return 60 entries
     * @return
     */
    public int getEntryCount() {
        return data.getEntryCount();
    }

    /**
     * Adds a threshold line
     * @param threshold The threshold value
     * @param label The label for the line
     * @param color The color of the line (e.g. Color.argb(0x20, 0xCC, 0xCC, 0xCC))
     */
    public void addThresholdLine(float threshold, String label, int color){
        YAxis leftAxis = getAxisLeft();
        LimitLine ll = new LimitLine(threshold);
        ll.setLineColor(color);
        ll.setLineWidth(1f);
        ll.enableDashedLine(20f, 15f, 1f);
        ll.setLabel(label);
        ll.setTextSize(10f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        leftAxis.addLimitLine(ll);
    }

    /**
     * Adds a threshold zone
     * @param threshold The threshold value
     * @param op The operator to define the zone ("==", "<", ">", "<=", ">=")
     * @param label The label for the zone
     * @param color The color of the zone (e.g. Color.argb(0x20, 0xCC, 0xCC, 0xCC))
     */
    public void addThresholdZone(float threshold, String op, String label, int color){
        LimitZone zone = new LimitZone(threshold, op);
        zone.setViewPortHandler(mViewPortHandler);
        zone.setAxisTransformer(mLeftAxisTransformer);
        zone.setColor(color);
        zones.add(zone);

        YAxis leftAxis = getAxisLeft();
        LimitLine ll = new LimitLine(threshold);
        ll.setLineColor(color);
        ll.setLineWidth(1f);
        if(!op.equals("<=") && !op.equals(">=")){
            ll.enableDashedLine(20f, 15f, 1f);
        }
        ll.setLabel(label);
        ll.setTextSize(10f);
        if(op.equals(">") || op.equals(">=")){
            ll.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        } else{
            ll.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        }
        leftAxis.addLimitLine(ll);
    }

    /**
     * Updates the chart
     */
    public void update() {
        notifyDataSetChanged();
        invalidate();
    }
}
