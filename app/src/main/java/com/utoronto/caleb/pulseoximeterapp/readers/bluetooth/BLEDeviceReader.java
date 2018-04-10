package com.utoronto.caleb.pulseoximeterapp.readers.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.Log;

import com.utoronto.caleb.pulseoximeterapp.MainActivity;
import com.utoronto.caleb.pulseoximeterapp.UsbDataHandler;
import com.utoronto.caleb.pulseoximeterapp.readers.IDeviceReader;

import java.util.Arrays;

public class BLEDeviceReader extends Thread implements IDeviceReader {

    public static final String TAG = "BLE_READER";

    UsbDataHandler mDataHandler;
    Context mContext;
    Intent mBLEServiceIntent;

    private final BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEDeviceService.ACTION_SEND_DATA.equals(action)) {
                byte[] data = intent.getByteArrayExtra(BLEDeviceService.DATA_EXTRA);
                saveData(data);
            }
        }
    };


    public BLEDeviceReader(BluetoothDevice device, Context context, UsbDataHandler handler) {
        mDataHandler = handler;
        mContext = context;
        mBLEServiceIntent = new Intent(context, BLEDeviceService.class);
        mBLEServiceIntent.putExtra(MainActivity.BLUETOOTH_DEVICE_PARAM, device);
        registerDataReceiver();
    }

    private void registerDataReceiver() {
        IntentFilter filter = new IntentFilter(BLEDeviceService.ACTION_SEND_DATA);
        mContext.registerReceiver(mDataReceiver, filter);
    }

    @Override
    public void run() {
        Log.d(TAG, "Attempting to start BLE Service");
        mContext.startService(mBLEServiceIntent);
    }

    @Override
    public void stopMonitor() {
        mContext.unregisterReceiver(mDataReceiver);
        mContext.stopService(mBLEServiceIntent);
    }

    @Override
    public void saveData(byte[] data) {
        int[] packet_int = new int[20];
        for ( int i = 0; i < data.length; i++)
            packet_int[i] = data[i] & 0xFF;
        Log.d(TAG, "Incoming data: " + Arrays.toString(packet_int));
    }


}
