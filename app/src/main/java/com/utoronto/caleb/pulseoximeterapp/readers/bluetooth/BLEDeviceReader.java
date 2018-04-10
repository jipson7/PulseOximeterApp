package com.utoronto.caleb.pulseoximeterapp.readers.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.utoronto.caleb.pulseoximeterapp.MainActivity;
import com.utoronto.caleb.pulseoximeterapp.UsbDataHandler;
import com.utoronto.caleb.pulseoximeterapp.readers.IDeviceReader;

public class BLEDeviceReader extends Thread implements IDeviceReader {

    public static final String TAG = "BLE_READER";

    UsbDataHandler mDataHandler;
    Context mContext;
    Intent mBLEServiceIntent;

    public BLEDeviceReader(BluetoothDevice device, Context context, UsbDataHandler handler) {
        mDataHandler = handler;
        mContext = context;
        mBLEServiceIntent = new Intent(context, BLEDeviceService.class);
        mBLEServiceIntent.putExtra(MainActivity.BLUETOOTH_DEVICE_PARAM, device);
    }

    @Override
    public void run() {
        Log.d(TAG, "Attempting to start BLE Service");
        mContext.startService(mBLEServiceIntent);
    }

    @Override
    public void stopMonitor() {
        mContext.stopService(mBLEServiceIntent);
    }

    @Override
    public void saveData(byte[] bytes) {
        //TODO
    }
}
