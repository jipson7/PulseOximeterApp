package com.utoronto.caleb.pulseoximeterapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.utoronto.caleb.pulseoximeterapp.readers.FingerTipReader;
import com.utoronto.caleb.pulseoximeterapp.readers.FloraReader;
import com.utoronto.caleb.pulseoximeterapp.storage.DBHelper;
import com.utoronto.caleb.pulseoximeterapp.storage.DataKeys;
import com.utoronto.caleb.pulseoximeterapp.visualization.DataVisualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MonitorService extends Service implements UsbDataHandler {

    private final String TAG = "MONITOR_SERVICE";

    public static final int MONITOR_NOTIFICATION_ID = 1234;

    private boolean isMonitoring = false;

    private ArrayList<String> mDeviceNames = null;
    private UsbManager mUsbManager = null;
    private FingerTipReader mFingerTipReader = null;
    private FloraReader mFloraReader = null;

    private DataVisualizer mDataVisualizer = null;
    private DBHelper mDBHelper = new DBHelper();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private final IBinder mBinder = new MonitorBinder();

    public void setDataVisualizer(DataVisualizer dataVisualizer) {
        mDataVisualizer = dataVisualizer;
    }

    public class MonitorBinder extends Binder {
        MonitorService getService() {
            return MonitorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mDataVisualizer = null;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Running onStart");
        if (!isMonitoring) {
            isMonitoring = true;
            if (mUsbManager == null) {
                mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            }
            createNotification();
            if (intent != null) {
                final String action = intent.getAction();
                if (action.equals(MonitorActivity.ACTION_MONITOR)) {
                    //TODO
                    //mDeviceNames = intent.getStringArrayListExtra(MainActivity.USB_DEVICE_PARAM);
                    monitor();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotification() {
        //TODO ensure this notification behaves the same in API 23 as 26
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
            String name = device.getProductName();
            if (Device.FINGERTIP.is(name)) {
                if (mFingerTipReader == null || !mFingerTipReader.isAlive()) {
                    mFingerTipReader = new FingerTipReader(deviceName, this, this);
                    mFingerTipReader.start();
                }
            } else if (Device.MAX30102.is(name)) {
                if (mFloraReader == null || !mFloraReader.isAlive()) {
                    mFloraReader = new FloraReader(deviceName, this, this);
                    mFloraReader.start();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        endUsbConnections();
        Log.d(TAG, "Monitor service stopped.");
    }

    @Override
    public void handleIncomingData(Device device, Map<String, Object> data) {
        if (mDataVisualizer != null) {
            Object oxygen = data.get(DataKeys.OXYGEN);
            if (oxygen != null) {
                mDataVisualizer.updateUI(device.toString(), (int) oxygen);
            }
        }
        mDBHelper.saveData(device, data);
    }

    @Override
    public void endUsbConnections() {
        Log.d(TAG, "Ending USB Monitoring Threads.");
        if (mFingerTipReader != null) {
            mFingerTipReader.stopMonitor();
        }
        endMonitoring();
    }

    private void endMonitoring() {
        mDBHelper.endSession();
        isMonitoring = false;
        Intent i = new Intent(MonitorActivity.ACTION_STOP_MONITOR);
        sendBroadcast(i);
        stopForeground(true);
        stopSelf();
    }
}
