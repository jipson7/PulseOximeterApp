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
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends Activity {

    private final String TAG = "MAIN_ACTIVITY";

    UsbManager mUsbManager;

    public static final String DEVICE_PARAM = "com.utoronto.caleb.pulseoximeterapp.param.DEVICE_PARAMETER";

    ArrayList<String> mDeviceNames;

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
                            Toast.makeText(MainActivity.this, R.string.device_granted, Toast.LENGTH_LONG).show();
                            startMonitorActivity();
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + device);
                        finish();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        setupUsbPermissionHandler();
    }

    private void setupUsbPermissionHandler() {
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    public void onClickDevicesBtn(View v) {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
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
        startMonitorActivity();
    }

    private void startMonitorActivity() {
        if (checkDevicePermissions()) {
            Intent intent = new Intent(this, MonitorActivity.class);
            intent.putStringArrayListExtra(DEVICE_PARAM, mDeviceNames);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_devices, Toast.LENGTH_SHORT).show();
            Log.e(TAG,"Missing permission for supported device.");
        }
    }

    private boolean checkDevicePermissions() {
        ArrayList<UsbDevice> devices = getAvailableDevices();
        mDeviceNames = new ArrayList<>();
        for (UsbDevice device: devices) {
            if (!this.mUsbManager.hasPermission(device)) {
                this.mUsbManager.requestPermission(device, this.mPermissionIntent);
                Log.d(TAG, "Device " + device.getProductName() +  " is missing permissions. Requesting.");
                return false;
            } else {
                mDeviceNames.add(device.getDeviceName());
            }
        }

        return (mDeviceNames.size() > 0);
    }

    private ArrayList<UsbDevice> getAvailableDevices() {
        ArrayList<UsbDevice> devices = new ArrayList<>();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator it = deviceList.values().iterator();
        Log.d(TAG, deviceList.size() + " devices detected.");
        while (it.hasNext()) {
            UsbDevice device = (UsbDevice) it.next();
            String name = device.getProductName();
            if (Device.FINGERTIP.nameEquals(name)) {
                Log.d(TAG, "Fingertip Sensor detected");
                devices.add(device);
            }
            it.remove();
        }
        return devices;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }
}
