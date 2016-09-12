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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluemaestro.utility.sdk.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.Entry;
import com.bluemaestro.utility.sdk.views.graphs.BMLineChart;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Garrett on 17/08/2016.
 */
public class BMTempHumi extends BMDevice {

    private static final Pattern burst_pattern =
            Pattern.compile("T([0-9]*\\.[0-9])H([0-9]*\\.[0-9])D([0-9]*\\.[0-9])");

    // Battery level
    private int battery;

    // Current temperature, humidity, and dew point
    private double currTemperature;
    private double currHumidity;
    private double currDewPoint;

    // Number of threshold breaches
    private int numBreach;

    // Highest temperature and humidity recorded
    private double highTemperature;
    private double highHumidity;

    // Lowest temperature and humidity recorded
    private double lowTemperature;
    private double lowHumidity;

    // Highest temperature, humidity, and dew point recorded in last 24 hours
    private double high24Temperature;
    private double high24Humidity;
    private double high24DewPoint;

    // Lowest temperature, humidity, and dew point recorded in last 24 hours
    private double low24Temperature;
    private double low24Humidity;
    private double low24DewPoint;

    // Average temperature, humidity, and dew point recorded in last 24 hours
    private double avg24Temperature;
    private double avg24Humidity;
    private double avg24DewPoint;


    public BMTempHumi(BluetoothDevice device, byte id) {
        super(device, id);
    }

    @Override
    public void updateWithData(int rssi, byte[] mData, byte[] sData){
        super.updateWithData(rssi, mData, sData);
        this.battery = mData[4];

        this.currTemperature = convertToUInt16(mData[9], mData[10]) / 10.0;
        this.currHumidity = convertToUInt16(mData[11], mData[12]) / 10.0;
        this.currDewPoint = convertToUInt16(mData[13], mData[14]) / 10.0;

        this.mode = mData[15];

        this.numBreach = mData[16];

        this.highTemperature = convertToUInt16(sData[3], sData[4]) / 10.0;
        this.highHumidity = convertToUInt16(sData[5], sData[6]) / 10.0;

        this.lowTemperature = convertToUInt16(sData[7], sData[8]) / 10.0;
        this.lowHumidity = convertToUInt16(sData[9], sData[10]) / 10.0;

        this.high24Temperature = convertToUInt16(sData[11], sData[12]) / 10.0;
        this.high24Humidity = convertToUInt16(sData[13], sData[14]) / 10.0;
        this.high24DewPoint = convertToUInt16(sData[15], sData[16]) / 10.0;

        this.low24Temperature = convertToUInt16(sData[17], sData[18]) / 10.0;
        this.low24Humidity = convertToUInt16(sData[19], sData[20]) / 10.0;
        this.low24DewPoint = convertToUInt16(sData[21], sData[22]) / 10.0;

        this.avg24Temperature = convertToUInt16(sData[23], sData[24]) / 10.0;
        this.avg24Humidity = convertToUInt16(sData[25], sData[26]) / 10.0;
        this.avg24DewPoint = convertToUInt16(sData[27], sData[28]) / 10.0;
    }

    public int getBatteryLevel(){
        return battery;
}

    public double getCurrentTemperature(){
        return currTemperature;
    }
    public double getCurrentHumidity(){
        return currHumidity;
    }
    public double getCurrentDewPoint(){
        return currDewPoint;
    }

    public boolean isInAeroplaneMode(){
        return mode % 100 == 5;
    }
    public boolean isInFahrenheit(){
        return mode >= 100;
    }
    public String getTempUnits(){
        return isInFahrenheit() ? "°F" : "°C";
    }

    public int getNumBreach(){
        return numBreach;
    }

    public double getHighestTemperature(){
        return highTemperature;
    }
    public double getHighestHumidity(){
        return highHumidity;
    }

    public double getLowestTemperature(){
        return lowTemperature;
    }
    public double getLowestHumidity(){
        return lowHumidity;
    }

    public double getHighest24Temperature(){
        return high24Temperature;
    }
    public double getHighest24Humidity(){
        return high24Humidity;
    }
    public double getHighest24DewPoint(){
        return high24DewPoint;
    }

    public double getLowest24Temperature(){
        return low24Temperature;
    }
    public double getLowest24Humidity(){
        return low24Humidity;
    }
    public double getLowest24DewPoint(){
        return low24DewPoint;
    }

    public double getAverage24Temperature(){
        return avg24Temperature;
    }
    public double getAverage24Humidity(){
        return avg24Humidity;
    }
    public double getAverage24DewPoint(){
        return avg24DewPoint;
    }

    @Override
    public void updateViewGroup(ViewGroup vg){
        super.updateViewGroup(vg);
        final TextView tvtemp = (TextView) vg.findViewById(R.id.temperature);
        final TextView tvhumi = (TextView) vg.findViewById(R.id.humidity);
        final TextView tvdewp = (TextView) vg.findViewById(R.id.dewPoint);

        final TextView tvtempHigh = (TextView) vg.findViewById(R.id.highTemp);
        final TextView tvtempLow = (TextView) vg.findViewById(R.id.lowTemp);
        final TextView tvhumiHigh = (TextView) vg.findViewById(R.id.highHumi);
        final TextView tvHumiLow = (TextView) vg.findViewById(R.id.lowHumi);

        final TextView tvtemp24High = (TextView) vg.findViewById(R.id.high24Temp);
        final TextView tvhumi24High = (TextView) vg.findViewById(R.id.high24Humi);
        final TextView tvdewp24High = (TextView) vg.findViewById(R.id.high24Dew);

        final TextView tvtemp24Low = (TextView) vg.findViewById(R.id.low24Temp);
        final TextView tvhumi24Low = (TextView) vg.findViewById(R.id.low24Humi);
        final TextView tvdewp24Low = (TextView) vg.findViewById(R.id.low24Dew);

        final TextView tvtemp24Avg = (TextView) vg.findViewById(R.id.avg24Temp);
        final TextView tvhumi24Avg = (TextView) vg.findViewById(R.id.avg24Humi);
        final TextView tvdewp24Avg = (TextView) vg.findViewById(R.id.avg24Dew);

        final TextView tvmode = (TextView) vg.findViewById(R.id.mode);
        final TextView tvnumb = (TextView) vg.findViewById(R.id.numBreach);

        vg.findViewById(R.id.tables).setVisibility(View.VISIBLE);
        vg.findViewById(R.id.extras).setVisibility(View.VISIBLE);

        tvtemp.setVisibility(View.VISIBLE);
        tvtemp.setText("Temperature = " + getCurrentTemperature() + getTempUnits());
        tvhumi.setVisibility(View.VISIBLE);
        tvhumi.setText("Humidity = " + getCurrentHumidity() + "%");
        tvdewp.setVisibility(View.VISIBLE);
        tvdewp.setText("Dew Point = " + getCurrentDewPoint() + getTempUnits());

        tvtempHigh.setVisibility(View.VISIBLE);
        tvtempHigh.setText("Highest T. = " + getHighestTemperature() + getTempUnits());
        tvtempLow.setVisibility(View.VISIBLE);
        tvtempLow.setText("Highest H. = " + getHighestHumidity() + "%");
        tvhumiHigh.setVisibility(View.VISIBLE);
        tvhumiHigh.setText("Lowest T. = " + getLowestTemperature() + getTempUnits());
        tvHumiLow.setVisibility(View.VISIBLE);
        tvHumiLow.setText("Lowest H. = " + getLowestHumidity() + "%");

        tvtemp24High.setVisibility(View.VISIBLE);
        tvtemp24High.setText("High T. = " + getHighest24Temperature() + getTempUnits());
        tvhumi24High.setVisibility(View.VISIBLE);
        tvhumi24High.setText("High H. = " + getHighest24Humidity() + "%");
        tvdewp24High.setVisibility(View.VISIBLE);
        tvdewp24High.setText("High D. = " + getHighest24DewPoint() + getTempUnits());

        tvtemp24Low.setVisibility(View.VISIBLE);
        tvtemp24Low.setText("Low T. = " + getLowest24Temperature() + getTempUnits());
        tvhumi24Low.setVisibility(View.VISIBLE);
        tvhumi24Low.setText("Low H. = " + getLowest24Humidity() + "%");
        tvdewp24Low.setVisibility(View.VISIBLE);
        tvdewp24Low.setText("Low D. = " + getLowest24DewPoint() + getTempUnits());

        tvtemp24Avg.setVisibility(View.VISIBLE);
        tvtemp24Avg.setText("Avg T. = " + getAverage24Temperature() + getTempUnits());
        tvhumi24Avg.setVisibility(View.VISIBLE);
        tvhumi24Avg.setText("Avg H. = " + getAverage24Humidity() + "%");
        tvdewp24Avg.setVisibility(View.VISIBLE);
        tvdewp24Avg.setText("Avg D. = " + getAverage24DewPoint() + getTempUnits());

        String modeText = "Mode = " +
                (isInFahrenheit() ? "\n - Fahrenheit" : "\n - Celsius") +
                " (" + getTempUnits() + ")" +
                (isInAeroplaneMode() ? "\n - Aeroplane Mode" : "");
        tvmode.setVisibility(View.VISIBLE);
        tvmode.setText(modeText);

        tvnumb.setVisibility(View.VISIBLE);
        tvnumb.setText("No. of threshold breaches = " + getNumBreach());
    }

    @Override
    public void setupChart(Chart chart, String command){
        if(!(chart instanceof BMLineChart)) return;
        BMLineChart lineChart = (BMLineChart) chart;
        if(command.equals("*bur")){
            // If we sent the "*bur" command, setup the chart
            lineChart.init("", 5);
            lineChart.setLabels(
                    new String[]{
                            "Temperature (" + getTempUnits() + ")",
                            "Humidity",
                            "Dew Point (" + getTempUnits() + ")"
                    },
                    new int[]{
                            Color.RED,
                            Color.BLUE,
                            Color.GREEN
                    }
            );
        } else if(Pattern.matches("\\*units[c|f]", command)){
            // If we sent the "*units" command, change units
            boolean change = true;
            String oldUnits = getTempUnits();
            switch(command.charAt(command.length() - 1)){
                case 'c':   if(isInFahrenheit()) mode -= 100;   break;
                case 'f':   if(!isInFahrenheit()) mode += 100;  break;
                default:    change = false;                     break;
            }
            if(!change) return;
            int length = lineChart.getEntryCount() / 3;
            float[] temperature = new float[length];
            float[] humidity = new float[length];
            float[] dewPoint = new float[length];
            Log.d("BMTempHumi", "Length: " + length);
            // Store the old temperatures
            for(int i = 0; i < length; i++){
                temperature[i] = lineChart.getEntry("Temperature (" + oldUnits + ")", i).getY();
                humidity[i] = lineChart.getEntry("Humidity", i).getY();
                dewPoint[i] = lineChart.getEntry("Dew Point (" + oldUnits + ")", i).getY();
            }
            // Manipulate the graph
            lineChart.setLabels(
                    new String[]{
                            "Temperature (" + getTempUnits() + ")",
                            "Humidity",
                            "Dew Point (" + getTempUnits() + ")"
                    },
                    new int[]{
                            Color.RED,
                            Color.BLUE,
                            Color.GREEN
                    }
            );
            // Manipulate the data
            for (int i = 0; i < length; i++) {
                temperature[i] = (isInFahrenheit())
                        ? temperature[i] * 1.8f + 32.0f
                        : (temperature[i] - 32.0f) / 1.8f;
                dewPoint[i] = (isInFahrenheit())
                        ? dewPoint[i] * 1.8f + 32.0f
                        : (dewPoint[i] - 32.0f) / 1.8f;
            }
            // Add the temperatures back in
            for(int i = 0; i < length; i++){
                lineChart.addEntry("Temperature (" + getTempUnits() + ")", new Entry(i, temperature[i]));
                lineChart.addEntry("Humidity", new Entry(i, humidity[i]));
                lineChart.addEntry("Dew Point (" + getTempUnits() + ")", new Entry(i, dewPoint[i]));
            }
        }
    }

    @Override
    public void updateChart(Chart chart, String text){
        if(!(chart instanceof BMLineChart)) return;
        BMLineChart lineChart = (BMLineChart) chart;
        // Only continue if it's THD values
        Matcher matcher = burst_pattern.matcher(text);
        if(!matcher.matches()) return;

        // Parse values
        float[] value = new float[3];
        for(int i = 0; i < value.length; i++) {
            value[i] = new Float(matcher.group(i + 1).trim());
        }
        // Insert values as new entries
        int index = lineChart.getEntryCount() / 3;
        lineChart.addEntry("Temperature (" + getTempUnits() + ")", new Entry(index, value[0]));
        lineChart.addEntry("Humidity", new Entry(index, value[1]));
        lineChart.addEntry("Dew Point (" + getTempUnits() + ")", new Entry(index, value[2]));
    }
}
