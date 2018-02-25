package com.utoronto.caleb.pulseoximeterapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MonitorService extends IntentService {

    private final String TAG = "MONITOR_SERVICE";

    public static final String ACTION_MONITOR = "com.utoronto.caleb.pulseoximeterapp.action.MONITOR";

    public static final String DEVICE_PARAM = "com.utoronto.caleb.pulseoximeterapp.param.DEVICE_PARAMETER";

    private ArrayList<String> mDeviceNames;

    private UsbManager mUsbManager = null;

    public MonitorService() {
        super("MonitorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mUsbManager == null) {
            mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        }
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_MONITOR)) {
                mDeviceNames = intent.getStringArrayListExtra(DEVICE_PARAM);
                monitor();
            }
        }
    }

    private void monitor() {
        Log.d(TAG, "Begin monitoring.");
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        for (String deviceName: mDeviceNames) {
            UsbDevice device = deviceList.get(deviceName);
            switch (device.getProductName()) {
                case "USBUART":
                    FingerTipReader fingerTipReader = new FingerTipReader(deviceName, this);
                    new Thread(fingerTipReader).start();
            }
        }
        //stopSelf();
    }

}
