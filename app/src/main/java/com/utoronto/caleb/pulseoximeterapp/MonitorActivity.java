package com.utoronto.caleb.pulseoximeterapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MonitorActivity extends Activity {
    
    UsbManager mUsbManager;
    String TAG;

    final String SENSOR_FINGERTIP = "USBUART";

    private PendingIntent mPermissionIntent;

    private static final String ACTION_USB_PERMISSION = "com.utoronto.caleb.pulseoximeterapp.action.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            startMonitorService();
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        this.TAG = MainActivity.TAG;
        this.mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        startMonitorService();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Starting Monitor activity");
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        super.onResume();
    }

    private void startMonitorService() {
        ArrayList<UsbDevice> devices = getAvailableDevices();
        Log.d(TAG, "Attempting to start Monitor Service.");
        ArrayList<String> deviceNames = new ArrayList<>();
        for (UsbDevice device: devices) {
            if (!this.mUsbManager.hasPermission(device)) {
                this.mUsbManager.requestPermission(device, this.mPermissionIntent);
                Log.d(TAG, "Device " + device.getProductName() +  " is missing permissions. Requesting.");
                return;
            } else {
                deviceNames.add(device.getDeviceName());
            }
        }

        if (deviceNames.size() == 0) {
            Toast.makeText(this, R.string.no_devices, Toast.LENGTH_LONG).show();
            Log.e(TAG,"Cannot start Monitoring service with 0 useable devices attached.");
            finish();
            return;
        }

        Log.d(TAG, "Starting Monitor service with " + deviceNames.size() + " devices.");

        Intent intent = new Intent(this, MonitorService.class);
        intent.setAction(MonitorService.ACTION_MONITOR);
        intent.putStringArrayListExtra(MonitorService.DEVICE_PARAM, deviceNames);
        startService(intent);
    }

    private ArrayList<UsbDevice> getAvailableDevices() {
        ArrayList<UsbDevice> devices = new ArrayList<>();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator it = deviceList.values().iterator();
        Log.d(TAG, deviceList.size() + " devices detected.");
        while (it.hasNext()) {
            UsbDevice device = (UsbDevice) it.next();
            Log.d(TAG, "Checking " + device.getProductName() + ".");
            switch (device.getProductName()) {
                case SENSOR_FINGERTIP:
                    Log.d(TAG, "Fingertip Sensor detected");
                    devices.add(device);
                    break;
            }
            it.remove();
        }
        return devices;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mUsbReceiver);
        super.onPause();
    }
}
