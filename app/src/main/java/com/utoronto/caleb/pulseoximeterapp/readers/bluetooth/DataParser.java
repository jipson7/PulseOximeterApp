package com.utoronto.caleb.pulseoximeterapp.readers.bluetooth;

import android.util.Log;

import java.util.Arrays;

public class DataParser {
    private static String TAG = BLEDeviceReader.TAG;
    void parseNRF52_DATA_NOTIFICATION(byte[] packet) {
        int[] packet_int = new int[20];
        for ( int i = 0; i < packet.length; i++)
            packet_int[i] = packet[i] & 0xFF;

        Log.d(TAG, Arrays.toString(packet_int));
    }
}
