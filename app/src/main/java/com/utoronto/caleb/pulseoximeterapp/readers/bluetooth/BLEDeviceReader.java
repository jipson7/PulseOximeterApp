package com.utoronto.caleb.pulseoximeterapp.readers.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.utoronto.caleb.pulseoximeterapp.UsbDataHandler;
import com.utoronto.caleb.pulseoximeterapp.readers.IDeviceReader;

public class BLEDeviceReader extends Thread implements IDeviceReader {

    public static final String TAG = "BLE_READER";

    UsbDataHandler mHandler;
    NRF52BLESensor mSensor;

    public BLEDeviceReader(BluetoothDevice device, Context context, UsbDataHandler handler) {
        mHandler = handler;
        mSensor = new NRF52BLESensor(context, device);
    }

    @Override
    public void run() {
        mSensor.resume();
    }

    @Override
    public void stopMonitor() {
        mSensor.pause();
    }
}
