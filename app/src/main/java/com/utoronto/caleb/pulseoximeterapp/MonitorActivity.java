package com.utoronto.caleb.pulseoximeterapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MonitorActivity extends Activity {
    private final String TAG = "MONITOR_ACTIVITY";

    public static final String ACTION_MONITOR = "com.utoronto.caleb.pulseoximeterapp.action.MONITOR";
    public static final String ACTION_STOP_MONITOR = "com.utoronto.caleb.pulseoximeterapp.action.STOP_MONITOR";

    Intent mMonitorServiceIntent = null;
    private MonitorService mMonitorService;
    private boolean mBound = false;
    ArrayList<String> mDeviceNames;


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
        mDeviceNames = getIntent().getStringArrayListExtra(MainActivity.DEVICE_PARAM);
        startMonitorService();
        bindMonitorService();
    }

    private void startMonitorService() {
        Log.d(TAG, "Starting Monitor service with " + mDeviceNames.size() + " devices.");
        mMonitorServiceIntent = new Intent(this, MonitorService.class);
        mMonitorServiceIntent.setAction(ACTION_MONITOR);
        mMonitorServiceIntent.putStringArrayListExtra(MainActivity.DEVICE_PARAM, mDeviceNames);
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
}
