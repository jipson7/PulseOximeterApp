package com.utoronto.caleb.pulseoximeterapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MonitorActivity extends Activity {
    
    UsbManager mUsbManager;
    String TAG;

    final String SENSOR_FINGERTIP = "USBUART";

    private PendingIntent mPermissionIntent;

    private static final String ACTION_USB_PERMISSION = "com.utoronto.caleb.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            monitorDevice(device);
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
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
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
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
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
        UsbSerialDriver driver = getUsbDriver(device);
        UsbDeviceConnection connection = this.mUsbManager.openDevice(device);

        if (connection == null) {
            Log.e(TAG, "Must request permission to access device.");
            this.mUsbManager.requestPermission(device, this.mPermissionIntent);
            return;
        } else {
            Log.d(TAG, "Successfully connected to " + device.getProductName());
        }

        UsbSerialPort port = driver.getPorts().get(0);
        try {
            port.open(connection);
            port.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UsbSerialDriver getUsbDriver(UsbDevice device) {
        ProbeTable customTable = new ProbeTable();
        customTable.addProduct(device.getVendorId(), device.getProductId(), CdcAcmSerialDriver.class);
        UsbSerialProber prober = new UsbSerialProber(customTable);
        List<UsbSerialDriver> drivers = prober.findAllDrivers(this.mUsbManager);
        return drivers.get(0);
    }
}
