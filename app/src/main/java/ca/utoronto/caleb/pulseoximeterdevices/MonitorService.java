package ca.utoronto.caleb.pulseoximeterdevices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import ca.utoronto.caleb.pulseoximeterdevices.readers.DeviceReader;
import ca.utoronto.caleb.pulseoximeterdevices.readers.FingerTipReader;
import ca.utoronto.caleb.pulseoximeterdevices.readers.FloraReader;
import ca.utoronto.caleb.pulseoximeterdevices.storage.DBHelper;


public class MonitorService extends Service implements UsbDataHandler {

    private final String TAG = "MONITOR_SERVICE";

    private boolean isMonitoring = false;

    private UsbManager mUsbManager;

    private ArrayList<Device> mDevices;
    private ArrayList<DeviceReader> mReaders;

    private DataVisualizer mDataVisualizer;
    private DBHelper mDBHelper = new DBHelper();

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
            if (intent != null) {
                final String action = intent.getAction();
                if (action.equals(MonitorActivity.ACTION_MONITOR)) {
                    Bundle extras = intent.getExtras();
                    mDevices = extras.getParcelableArrayList(MainActivity.DEVICE_LIST_PARAM);
                    String trialDesc = extras.getString(MonitorActivity.EXTRA_TRIAL_DESCRIPTION);
                    mDBHelper.setupTrial(trialDesc);
                    monitor();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void monitor() {
        Log.d(TAG, "Begin monitoring.");
        mReaders = new ArrayList<>();
        for (Device device: mDevices) {
            DeviceReader reader;
            if (device.isType(Device.FINGERTIP_DEVICE.USB_NAME)) {
                reader = new FingerTipReader(device, this, this);
                reader.start();
                mReaders.add(reader);
            } else if (device.isType(Device.FLORA_DEVICE.USB_NAME)) {
                reader = new FloraReader(device, this, this);
                reader.start();
                mReaders.add(reader);
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
        Log.d(TAG, "Data incoming to service");
        if (mDataVisualizer != null) {
            mDataVisualizer.updateUI(device, data);
        }
        mDBHelper.saveData(device, data);
    }

    @Override
    public void endUsbConnections() {
        Log.d(TAG, "Ending USB Monitoring Threads.");
        for (DeviceReader reader: mReaders) {
            reader.stopMonitor();
        }
        endMonitoring();
    }

    private void endMonitoring() {
        mDBHelper.endSession();
        isMonitoring = false;
        Intent i = new Intent(MonitorActivity.ACTION_STOP_MONITOR);
        sendBroadcast(i);
        stopSelf();
    }
}
