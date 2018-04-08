package com.utoronto.caleb.pulseoximeterapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.utoronto.caleb.pulseoximeterapp.readers.BLEDeviceReader;
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

    private UsbManager mUsbManager = null;

    private UsbDevice mFingertipDevice;
    private UsbDevice mFloraDevice;
    private BluetoothDevice mBluetoothDevice;

    private FingerTipReader mFingerTipReader = null;
    private FloraReader mFloraReader = null;
    private BLEDeviceReader mBLEDeviceReader = null;

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
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        mBluetoothDevice = extras.getParcelable(MainActivity.BLUETOOTH_DEVICE_PARAM);
                        if (mBluetoothDevice != null) {
                            Log.d(TAG, "Device Received: " + Device.BLUETOOTH_SENSOR);
                        }
                        mFingertipDevice = extras.getParcelable(MainActivity.FINGERTIP_DEVICE_PARAM);
                        if (mFingertipDevice != null) {
                            Log.d(TAG, "Device Received: " + Device.FINGERTIP);
                        }
                        mFloraDevice = extras.getParcelable(MainActivity.FLORA_DEVICE_PARAM);
                        if (mFloraDevice != null) {
                            Log.d(TAG, "Device Received: " + Device.MAX30102);
                        }
                    }
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

        if (mFingerTipReader == null || !mFingerTipReader.isAlive()) {
            mFingerTipReader = new FingerTipReader(mFingertipDevice.getDeviceName(), this, this);
            mFingerTipReader.start();
        }

        if (mFloraReader == null || !mFloraReader.isAlive()) {
            mFloraReader = new FloraReader(mFloraDevice.getDeviceName(), this, this);
            mFloraReader.start();
        }

        if (mBLEDeviceReader == null || !mBLEDeviceReader.isAlive()) {
            //TODO start BLE reading
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
        if (mFloraReader != null) {
            mFloraReader.stopMonitor();
        }
        if (mBLEDeviceReader != null) {
            mBLEDeviceReader.stopMonitor();
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
