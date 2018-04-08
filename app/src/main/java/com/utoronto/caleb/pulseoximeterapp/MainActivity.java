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
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends Activity {

    private final String TAG = "MAIN_ACTIVITY";

    UsbManager mUsbManager;

    public static final String USB_DEVICE_PARAM = "com.utoronto.caleb.pulseoximeterapp.param.USB_DEVICE_PARAMETER";

    private Switch mFingertipSwitch;
    private Switch mFloraSwitch;
    private Switch mBluetoothSwitch;

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
        FirebaseApp.initializeApp(this);
        setupDeviceSwitches();
    }

    private void setupDeviceSwitches() {
        mFingertipSwitch = findViewById(R.id.switch_fingertip);
        mFloraSwitch = findViewById(R.id.switch_flora);
        mBluetoothSwitch = findViewById(R.id.switch_bluetooth);
    }

    private void setupUsbPermissionHandler() {
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    public void onClickLogUsbDevices(View v) {
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

        ArrayList<String> usbDeviceNames = new ArrayList<>();

        if (mFingertipSwitch.isChecked()) {
            String name = getDeviceName(Device.FINGERTIP);
            if (name == null) {
                Log.e(TAG, "Unable to locate: " + Device.FINGERTIP.toString());
                return;
            }
            usbDeviceNames.add(name);
        }
        if (mFloraSwitch.isChecked()) {
            Log.e(TAG, "Unable to locate: " + Device.MAX30102.toString());
            String name = getDeviceName(Device.MAX30102);
            if (name == null) {
                return;
            }
            usbDeviceNames.add(name);
        }
        if (mBluetoothSwitch.isChecked()) {
            //TODO pass the device somehow
        }

        Intent intent = new Intent(this, MonitorActivity.class);
        intent.putStringArrayListExtra(USB_DEVICE_PARAM, usbDeviceNames);
        startActivity(intent);
    }

    private String getDeviceName(Device d) {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator it = deviceList.values().iterator();
        while (it.hasNext()) {
            UsbDevice device = (UsbDevice) it.next();
            String name = device.getProductName();
            if (d.is(name)) {
                if (!this.mUsbManager.hasPermission(device)) {
                    this.mUsbManager.requestPermission(device, this.mPermissionIntent);
                    return null;
                }
                return device.getDeviceName();
            }
            it.remove();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }
}
