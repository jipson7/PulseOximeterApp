package com.utoronto.caleb.pulseoximeterapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
        int BUFFER_SIZE = 500;
        UsbEndpoint usbEndpoint = getBulkInEndpoint(device);
        UsbDeviceConnection connection = this.mUsbManager.openDevice(device);

        String productName = device.getProductName();

        if (connection == null) {
            Log.e(TAG, "Must request permission to access " + productName);
            this.mUsbManager.requestPermission(device, this.mPermissionIntent);
            return;
        } else {
            Log.d(TAG, "Successfully connected to " + productName);
        }
        while(true) {
            byte[] bytesIn = new byte[usbEndpoint.getMaxPacketSize()];
            int result = connection.bulkTransfer(usbEndpoint, bytesIn, bytesIn.length, BUFFER_SIZE);
            if (result < 0) {
                Log.d(TAG, "Usb read result is -1, ending loop");
                break;
            }
            String dataRead = bytesToHex(bytesIn);
            int currHeartRate = Integer.parseInt(dataRead.charAt(6) + "" + dataRead.charAt(7), 16);
            int currSpo2 = Integer.parseInt(dataRead.charAt(8) + "" + dataRead.charAt(9), 16);
            int currBP = Integer.parseInt(dataRead.charAt(4) + "" + dataRead.charAt(5), 16);
            Log.d(TAG, currHeartRate + " " + currSpo2 + " " + currBP);
        }
    }

    private UsbEndpoint getBulkInEndpoint(UsbDevice device) {
        UsbEndpoint inEndpoint = null;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbInterface = device.getInterface(i);
            for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                if (usbInterface.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN) {
                    inEndpoint = usbInterface.getEndpoint(j);
                    if (inEndpoint != null && inEndpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        Log.d(TAG, "BulkIn endpoint found. ");
                        break;
                    }
                }
            }
        }
        return inEndpoint;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
