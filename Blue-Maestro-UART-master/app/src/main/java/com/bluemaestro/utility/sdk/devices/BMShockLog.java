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

package com.bluemaestro.utility.sdk.devices;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.Entry;
import com.bluemaestro.utility.sdk.views.graphs.BMLineChart;

import java.util.regex.Pattern;

/**
 * Created by Garrett on 15/08/2016.
 */
public class BMShockLog extends BMDevice {

    public BMShockLog(BluetoothDevice device, byte id) {
        super(device, id);
    }

    @Override
    public void updateWithData(int rssi, byte[] mData, byte[] sData){
        super.updateWithData(rssi, mData, sData);
    }

    @Override
    public void setupChart(Chart chart, String command){
        if(chart instanceof BMLineChart && command.equals("*bur")){
            // If we sent the "*bur" command, setup the chart
            BMLineChart lineChart = (BMLineChart) chart;
            lineChart.init("", 24);
            lineChart.setLabels(
                    new String[]{"X", "Y", "Z"},
                    new int[]{Color.RED, Color.BLUE, Color.BLACK}
            );
        }
    }

    @Override
    public void updateChart(Chart chart, String text){
        if(chart instanceof BMLineChart){
            BMLineChart lineChart = (BMLineChart) chart;
            // Only continue if it's XYZ values
            String regex = "x:[0-9]* y:[0-9]* z:[0-9]*";
            if(!Pattern.matches(regex, text)) return;

            // Parse values
            String[] element = text.split(" ");
            int[] value = new int[element.length];
            for(int i = 0; i < value.length; i++) {
                value[i] = new Integer(element[i].split(":")[1].trim());
            }
            // Insert values as new entries
            int index = lineChart.getEntryCount();
            lineChart.addEntry("X", new Entry(index, value[0]));
            lineChart.addEntry("Y", new Entry(index, value[1]));
            lineChart.addEntry("Z", new Entry(index, value[2]));
        }
    }
}
