package com.utoronto.caleb.pulseoximeterapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends Activity {

    UsbManager usbManager;

    static String TAG = "PULSE_OXIMETER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    public void onClickDevicesBtn(View v) {
        this.logAvailableDevices();
    }

    public void logAvailableDevices(){
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Log.d(TAG, deviceList.size() + " devices found.");
        Iterator it = deviceList.values().iterator();
        while (it.hasNext()) {
            UsbDevice device = (UsbDevice) it.next();
            String deviceName = device.getDeviceName();
            Log.d(TAG, "Device Name: " + deviceName);
            int vendorID = device.getVendorId();
            int productID = device.getProductId();
            Log.d(TAG, "Vendor ID: " + vendorID + ", Product ID: " + productID);
            String productName = device.getProductName();
            Log.d(TAG, "Product Name: " + productName);
            String manufacturerName = device.getManufacturerName();
            Log.d(TAG, "Manufacturer Name: " + manufacturerName);
            String serial = device.getSerialNumber();
            Log.d(TAG, "Serial No: " + device.getSerialNumber());
        }
    }

    public void onClickMonitorBtn(View v) {
        Intent intent = new Intent(this, MonitorActivity.class);
        startActivity(intent);
    }
}
