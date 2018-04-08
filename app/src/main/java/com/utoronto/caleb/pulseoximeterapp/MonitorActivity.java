package com.utoronto.caleb.pulseoximeterapp;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.utoronto.caleb.pulseoximeterapp.visualization.DataVisualizer;

import java.util.ArrayList;

public class MonitorActivity extends Activity implements DataVisualizer {
    private final String TAG = "MONITOR_ACTIVITY";

    public static final String ACTION_MONITOR = "com.utoronto.caleb.pulseoximeterapp.action.MONITOR";
    public static final String ACTION_STOP_MONITOR = "com.utoronto.caleb.pulseoximeterapp.action.STOP_MONITOR";

    Intent mMonitorServiceIntent = null;
    private MonitorService mMonitorService;
    private boolean mBound = false;
    ArrayList<String> mUsbDeviceNames;
    private BluetoothDevice mBluetoothDevice;
    private UsbDevice mFingertipDevice;
    private UsbDevice mFloraDevice;


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_STOP_MONITOR.equals(action)) {
                Log.d(TAG, "Broadcast to end monitoring received");
                endMonitoring();
            }
        }
    };


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MonitorService.MonitorBinder binder = (MonitorService.MonitorBinder) service;
            mMonitorService = binder.getService();
            mMonitorService.setDataVisualizer(MonitorActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service disconnected.");
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        registerReceiver(mReceiver, new IntentFilter(ACTION_STOP_MONITOR));
        Bundle extras = getIntent().getExtras();
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
        //TODO remove
//        startMonitorService();
//        bindMonitorService();
    }

    private void startMonitorService() {
        Log.d(TAG, "Starting Monitor service with " + mUsbDeviceNames.size() + " devices.");
        mMonitorServiceIntent = new Intent(this, MonitorService.class);
        mMonitorServiceIntent.setAction(ACTION_MONITOR);
        //TODO remove
        //mMonitorServiceIntent.putStringArrayListExtra(MainActivity.USB_DEVICE_PARAM, mUsbDeviceNames);
        startService(mMonitorServiceIntent);
    }

    private void bindMonitorService() {
        bindService(mMonitorServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void btnClickEndMonitoring(View v) {
        endMonitoring();
    }

    private void endMonitoring(){
        Log.d(TAG, "Stopping Monitor service and subtasks.");
        if (mMonitorServiceIntent != null) {
            stopService(mMonitorServiceIntent);
        }
        finish();
    }

    public void closeWindow(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(mServiceConnection);
        mBound = false;
    }

    @Override
    public void updateUI(String device, int spo2) {
        Log.d(TAG,spo2 + " " + device);
    }
}
