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
 * BMDevice is an abstract class for Blue Maestro BLE devices
 */
public abstract class BMDevice {

    /**
     * MAC address of the device
     */
    protected final String address;

    /**
     * Name of the device
     */
    protected final String name;

    /**
     * ID/Version of the device
     */
    protected final byte version;

    /**
     * Received Signal Strength Indicator
     */
    protected byte rssi;

    /**
     * Bond state
     */
    protected int bondState;

    /**
     * Device mode
     */
    protected byte mode;

    /**
     * Constructor
     * @param device Bluetooth device
     * @param version Version of this Blue Maestro device
     */
    public BMDevice(BluetoothDevice device, byte version){
        this.address = device.getAddress();
        this.name = device.getName();
        this.version = version;
        this.bondState = device.getBondState();
        this.mode = 0;
    }

    public String getAddress() { return address; }
    public String getName() { return name; }
    public byte getVersion() { return version; }
    public byte getRSSI() { return rssi; }
    public int getBondState() {return bondState; }
    public byte getMode(){
        return mode;
    }

    /**
     * Updates this Blue Maestro device with new data
     * @param rssi The new RSSI of the device
     * @param mData The new manufacturer data of the device
     * @param sData The new scan response data of the device
     */
    public void updateWithData(int rssi, byte[] mData, byte[] sData){
        this.rssi = (byte) rssi;
    }

    /**
     * Sets up the chart for data
     * @param chart The chart to setup
     * @param command The command sent to the device
     */
    public abstract void setupChart(Chart chart, String command);

    /**
     * Updates the chart with data
     * @param chart The chart to update
     * @param text The data to update the chart with
     */
    public abstract void updateChart(Chart chart, String text);

    /**
     * Updates the device's view group to update its display in the device element list
     * @param vg The view group to update
     */
    public void updateViewGroup(ViewGroup vg){
        final TextView tvaddr = (TextView) vg.findViewById(R.id.address);
        final TextView tvname = (TextView) vg.findViewById(R.id.name);
        final TextView tvpair = (TextView) vg.findViewById(R.id.paired);
        final TextView tvrssi = (TextView) vg.findViewById(R.id.rssi);

        // Name
        tvname.setText(getName());
        tvname.setVisibility(View.VISIBLE);

        // MAC address
        tvaddr.setText(getAddress());
        tvaddr.setVisibility(View.VISIBLE);

        // RSSI
        byte rssival = getRSSI();
        if (rssival != 0) {
            tvrssi.setVisibility(View.VISIBLE);
            tvrssi.setText("Rssi = " + String.valueOf(rssival));
        } else{
            tvrssi.setVisibility(View.GONE);
        }

        // Pair bonding
        if (getBondState() == BluetoothDevice.BOND_BONDED) {
            tvpair.setVisibility(View.VISIBLE);
            tvpair.setText(R.string.paired);
        } else {
            tvpair.setVisibility(View.GONE);
        }
    }

    public boolean equals(Object object){
        if(object == null) return false;
        if(!(object instanceof BMDevice)) return false;
        return address == ((BMDevice) object).address;
    }
    public int hashCode(){
        return address.hashCode();
    }

    /**
     * Checks if the scan data is from a Blue Maestro BLE device
     * @param data
     * @return
     */
    public static final boolean isBMDevice(byte[] data){
        return data.length >= 3
                && ((int) data[1] & 0xFF) == 0x33
                && ((int) data[2] & 0xFF) == 0x01;
    }

    /**
     * Convert two bytes to unsigned int 16
     * @param first
     * @param second
     * @return
     */
    protected static final int convertToUInt16(byte first, byte second){
        int value = (int) first & 0xFF;
        value *= 256;
        value += (int) second & 0xFF;
        value -= (value > 32768) ? 65536 : 0;
        return value;
    }
}
