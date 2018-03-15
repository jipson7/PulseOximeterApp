package com.utoronto.caleb.pulseoximeterapp;

import android.content.Context;
import android.util.Log;


public class FloraReader extends CustomDeviceReader {

    private String TAG = "FLORA_READER";

    public FloraReader(String deviceName, Context context, UsbDataHandler handler) {
        super(deviceName, context, handler);
    }

    @Override
    public void saveData(byte[] bytes) {
        Log.d(TAG, "Incoming bytes on Flora");
    }
}
