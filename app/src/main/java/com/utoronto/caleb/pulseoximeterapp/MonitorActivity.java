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
    }

    private ArrayList<UsbDevice> getAvailableDevices() {
        ArrayList<UsbDevice> devices = new ArrayList<>();
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator it = deviceList.values().iterator();
        Log.d(TAG, deviceList.size() + " devices detected.");
        while (it.hasNext()) {
            UsbDevice device = (UsbDevice) it.next();
            String productName = device.getProductName();
            switch (productName) {
                case "USBUART":
                    Log.d(TAG, "Fingertip Sensor detected");
                    devices.add(device);
                    break;
            }
        }
        return devices;
    }

    private void monitorDevice(UsbDevice device) {

    }
}
