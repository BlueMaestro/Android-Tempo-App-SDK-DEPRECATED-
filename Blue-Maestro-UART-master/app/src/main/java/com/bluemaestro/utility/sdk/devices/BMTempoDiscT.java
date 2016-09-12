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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluemaestro.utility.sdk.R;
import com.github.mikephil.charting.charts.Chart;

/**
 * Created by Garrett on 05/08/2016.
 *
 * Blue Maesttro Tempo Disc T
 */
public class BMTempoDiscT extends BMDevice {

    private double temperature;

    public BMTempoDiscT(BluetoothDevice device, byte id) {
        super(device, id);
    }

    @Override
    public void updateWithData(int rssi, byte[] mData, byte[] sData){
        super.updateWithData(rssi, mData, sData);
        // Conversion from unsigned to signed, then dividing by 10.0 for degrees
        temperature = convertToUInt16(mData[7], mData[6]) / 10.0;
    }

    @Override
    public void setupChart(Chart chart, String command) {

    }

    @Override
    public void updateChart(Chart chart, String text) {

    }

    public double getTemperature(){
        return temperature;
    }

    @Override
    public void updateViewGroup(ViewGroup vg){
        super.updateViewGroup(vg);
        final TextView tvtemp = (TextView) vg.findViewById(R.id.temperature);
        tvtemp.setVisibility(View.VISIBLE);
        tvtemp.setText("Temperature = " + getTemperature() + "°C");
    }
}
