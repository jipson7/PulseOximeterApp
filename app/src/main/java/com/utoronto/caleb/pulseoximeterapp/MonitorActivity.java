package com.utoronto.caleb.pulseoximeterapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MonitorActivity extends Activity {
    
    UsbManager usbManager;
    String TAG;

    final String SENSOR_FINGERTIP = "USBUART";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        this.TAG = MainActivity.TAG;
        this.usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        startDeviceMonitor();
    }

    private void startDeviceMonitor() {
        ArrayList<UsbDevice> devices = getAvailableDevices();
        for (UsbDevice device: devices) {
            // Spawn Thread here to monitor device if multiple
            monitorDevice(device);
        }
    }

    private ArrayList<UsbDevice> getAvailableDevices() {
        ArrayList<UsbDevice> devices = new ArrayList<>();
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator it = deviceList.values().iterator();
        Log.d(TAG, deviceList.size() + " devices detected.");
        while (it.hasNext()) {
            UsbDevice device = (UsbDevice) it.next();
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

    private void monitorDevice(UsbDevice device) {
        
    }
}
