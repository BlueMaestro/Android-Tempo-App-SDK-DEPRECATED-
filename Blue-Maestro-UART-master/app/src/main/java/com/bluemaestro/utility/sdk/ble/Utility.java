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
 * Created by Garrett on 24/08/2016.
 */
public final class Utility {

    private Utility(){

    }

    public static final String bytesAsHexString(byte[] bytes){
        StringBuilder stringBuilder = new StringBuilder(bytes.length);
        for(byte byteChar : bytes)
            stringBuilder.append(String.format("%02X", byteChar)).append(" ");
        return stringBuilder.toString();
    }

    public static final String convertValueTo(String value, String units){
        if(value == null) return null;
        float val = new Float(value);
        switch(units.charAt(units.length() - 1)){
            case 'C':
                val = (val - 32) / 1.8f;
                break;
            case 'F':
                val = val * 1.8f + 32;
                break;
            default:
                break;
        }
        return String.format("%.1f", val);
    }

    public static final String formatKey(String key){
        key = key.trim();
        key = key.replace("_", " ");
        key = key.toLowerCase();
        char[] array = key.toCharArray();
        for(int i = 0; i < array.length - 1; i++){
            if(array[i] == ' ' || array[i] == '°') array[i+1] = Character.toUpperCase(array[i+1]);
        }
        array[0] = Character.toUpperCase(array[0]);
        return new String(array);
    }

}
