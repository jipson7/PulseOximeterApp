package com.utoronto.caleb.pulseoximeterapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MonitorService extends Service {

    private final String TAG = "MONITOR_SERVICE";

    public static final int MONITOR_NOTIFICATION_ID = 1234;

    public static final String ACTION_MONITOR = "com.utoronto.caleb.pulseoximeterapp.action.MONITOR";

    public static final String DEVICE_PARAM = "com.utoronto.caleb.pulseoximeterapp.param.DEVICE_PARAMETER";

    private ArrayList<String> mDeviceNames;

    private FingerTipReader mFingerTipReader;

    private UsbManager mUsbManager = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification();
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
        return super.onStartCommand(intent, flags, startId);
    }



    private void createNotification() {
        //Go to this intent if Notification is clicked
        Intent intent = new Intent(this, MonitorActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //Create notification
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setContentIntent(pIntent)
                .setTicker(getText(R.string.ticker_text))
                .build();
        startForeground(MONITOR_NOTIFICATION_ID, notification);

    }


    private void monitor() {
        Log.d(TAG, "Begin monitoring.");
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        for (String deviceName: mDeviceNames) {
            UsbDevice device = deviceList.get(deviceName);
            switch (device.getProductName()) {
                case "USBUART":
                    mFingerTipReader = new FingerTipReader(deviceName, this);
                    mFingerTipReader.start();
            }
        }
    }

    private void killThreads() {
        Log.d(TAG, "Killing threads.");
        if (mFingerTipReader != null) {
            mFingerTipReader.stopMonitor();
        }
    }

    private void cleanup() {
        Log.d(TAG, "Stopping service");
        killThreads();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        cleanup();
    }
}
