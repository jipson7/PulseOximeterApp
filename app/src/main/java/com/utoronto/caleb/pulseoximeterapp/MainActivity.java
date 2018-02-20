package com.utoronto.caleb.pulseoximeterapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends Activity {

    UsbManager manager;

    String TAG = "PULSE_OXIMETER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.manager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    public void onClickDevicesBtn(View v) {
        this.logAvailableDevices();
    }

    public void logAvailableDevices(){
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Log.d(TAG, deviceList.size() + " devices found.");
        Iterator it = deviceList.values().iterator();
        while (it.hasNext()) {
            UsbDevice device = (UsbDevice) it.next();
            int vendorID = device.getVendorId();
            int productID = device.getProductId();
            Log.d(TAG, "Vendor ID: " + vendorID + ", Product ID: " + productID);
            String productName = device.getProductName();
            Log.d(TAG, "Product Name: " + productName);
            String manufacturerName = device.getManufacturerName();
            Log.d(TAG, "Manufacturer Name: " + manufacturerName);
        }
    }

    public void onClickMonitorBtn(View v) {
        Intent intent = new Intent(this, MonitorActivity.class);
        startActivity(intent);
    }
}
