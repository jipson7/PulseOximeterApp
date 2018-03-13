package com.utoronto.caleb.pulseoximeterapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MonitorActivity extends Activity {
    private final String TAG = "MONITOR_ACTIVITY";

    Intent mMonitorServiceIntent = null;
    private MonitorService mMonitorService;
    private boolean mBound = false;
    ArrayList<String> mDeviceNames;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MonitorService.MonitorBinder binder = (MonitorService.MonitorBinder) service;
            mMonitorService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service disconnected.");
            mBound = false;
            MonitorActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        mDeviceNames = getIntent().getStringArrayListExtra(MainActivity.DEVICE_PARAM);
        startMonitorService();
        bindMonitorService();
    }

    private void startMonitorService() {
        Log.d(TAG, "Starting Monitor service with " + mDeviceNames.size() + " devices.");
        mMonitorServiceIntent = new Intent(this, MonitorService.class);
        mMonitorServiceIntent.setAction(MonitorService.ACTION_MONITOR);
        mMonitorServiceIntent.putStringArrayListExtra(MainActivity.DEVICE_PARAM, mDeviceNames);
        startService(mMonitorServiceIntent);
    }

    private void bindMonitorService() {
        bindService(mMonitorServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void endMonitoring(View v) {
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
        unbindService(mServiceConnection);
        mBound = false;
    }
}
