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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Garrett on 15/08/2016.
 *
 * This is a map which will create a Blue Maestro device, given its ID.
 * This allows less duplication of code, and contains the Blue Maestro device
 * creation in one area
 */
public enum BMDeviceMap {

    INSTANCE;

    /**
     * Map of ID to Blue Maestro device
     */
    private final Map<Byte, Class<? extends BMDevice>> dvMap
            = new HashMap<Byte, Class<? extends BMDevice>>();
    private final Map<String, BMDevice> deviceMap
            = new HashMap<String, BMDevice>();

    /**
     * IDs
     */
    private static final byte TEMPO_DISC_T  = 0x01;
    private static final byte TEMP_HUMI     = 0x16;

    /**
     * Initialize IDs to classes
     */
    BMDeviceMap(){
        // Blue Maestro devices
        dvMap.put(TEMPO_DISC_T,     BMTempoDiscT.class);
        dvMap.put(TEMP_HUMI,        BMTempHumi.class);
    }

    public final void clear(){

    }

    /**
     * Creates a Blue Maestro device. If not found, will default to a generic Blue Maestro device.
     * @param id The ID of the Blue Maestro device
     * @param device The BluetoothDevice currently viewed
     * @return The Blue Maestro device
     */
    public final BMDevice createBMDevice(byte id, BluetoothDevice device){
        BMDevice bmDevice = null;
        Class<? extends BMDevice> clazz = dvMap.get(id);
        if(clazz != null){
            try {
                bmDevice = clazz
                            .getDeclaredConstructor(BluetoothDevice.class, byte.class)
                            .newInstance(device, id);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if(bmDevice == null) bmDevice = new BMDefaultDevice(device, (byte) 0xFF);
        deviceMap.put(device.getAddress(), bmDevice);
        return bmDevice;
    }
    /**
     * Gets a current Blue Maestro device
     * @param address The address of the device
     * @return The Blue Maestro device
     */
    public final BMDevice getBMDevice(String address){
        return deviceMap.get(address);
    }
}
