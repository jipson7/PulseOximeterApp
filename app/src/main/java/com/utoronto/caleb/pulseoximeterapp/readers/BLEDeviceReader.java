package com.utoronto.caleb.pulseoximeterapp.readers;

import android.content.Context;

import com.utoronto.caleb.pulseoximeterapp.UsbDataHandler;

public class BLEDeviceReader extends AbstractDeviceReader {

    public BLEDeviceReader(String deviceName, Context context, UsbDataHandler handler) {
        super(deviceName, context, handler);
    }

    @Override
    public void saveData(byte[] bytes) {

    }
}
