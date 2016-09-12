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

package com.bluemaestro.utility.sdk.ble;

/**
 * Created by Garrett on 15/08/2016.
 *
 * BluetoothLeScanner and ScanRecord are not provided until Android SDK 21.
 * Instead, for compatibility, we create a small parser that allows us to
 * grab the manufacturer data (as that is all which is needed).
 *
 * The scan record holds e.g. service UUIDs, device name, etc. as well as
 * manufacturer data.
 * The data is concatenated togather, which each piece in the following format:
 * [data-id][data-values]
 *
 * For more information on IDs, see:
 * https://www.bluetooth.org/en-us/specification/assigned-numbers/generic-access-profile
 */
public final class ScanRecordParser {

    private final byte[] manufacturerData;
    private final byte[] scanResponseData;

    public ScanRecordParser(byte[] scanRecord){
        // We look for 0xFF 0x33 0x01 to detect manufacturer/scan response data
        int i, k;
        for(i = 0; !isManufacturerID(i, scanRecord); ) i++;
        for(k = i + 1; !isManufacturerID(k, scanRecord); ) k++;

        k = (k > scanRecord.length) ? scanRecord.length : k;

        // Manufacturer data
        this.manufacturerData = new byte[k - i];
        for(int j = 0; j < manufacturerData.length; j++, i++){
            manufacturerData[j] = scanRecord[i];
        }

        // Scan response data
        this.scanResponseData = new byte[scanRecord.length - k];
        for(int l = 0; l < scanResponseData.length; l++, k++){
            scanResponseData[l] = scanRecord[k];
        }

        //Log.d("ScanRecordParser", "Manufacturer data: 0x" + Utility.bytesAsHexString(manufacturerData));
        //Log.d("ScanRecordParser", "Scan Response data: 0x" + Utility.bytesAsHexString(scanResponseData));
    }

    public final byte[] getManufacturerData(){
        return manufacturerData;
    }

    public final byte[] getScanResponseData() {return scanResponseData; }

    private static final boolean isManufacturerID(int i, byte[] scanRecord){
        if(i >= scanRecord.length - 2)                  return true;
        if(((int) scanRecord[i] & 0xFF) != 0xFF)        return false;
        if(((int) scanRecord[i+1] & 0xFF) != 0x33)      return false;
        if(((int) scanRecord[i+2] & 0xFF) != 0x01)      return false;
        return true;
    }
}
