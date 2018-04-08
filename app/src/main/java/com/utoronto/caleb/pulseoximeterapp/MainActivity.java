package com.utoronto.caleb.pulseoximeterapp;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends Activity {

    private final String TAG = "MAIN_ACTIVITY";

    UsbManager mUsbManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice = null;
    private Handler mHandler;
    private static final int BLUETOOTH_SCAN_PERIOD = 60000; // 10 seconds

    public static final String USB_DEVICE_PARAM =
            "com.utoronto.caleb.pulseoximeterapp.param.USB_DEVICE_PARAMETER";
    public static final String BLUETOOTH_DEVICE_PARAM =
            "com.utoronto.caleb.pulseoximeterapp.param.BLUETOOTH_DEVICE_PARAMETER";

    private final int REQUEST_ENABLE_BT = 1111;
    private final int REQUEST_LOCATION = 2222;

    private Switch mFingertipSwitch;
    private Switch mFloraSwitch;
    private Switch mBluetoothSwitch;

    private PendingIntent mPermissionIntent;


    private static final String ACTION_USB_PERMISSION = "com.utoronto.caleb.pulseoximeterapp.action.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            Toast.makeText(MainActivity.this, R.string.device_granted, Toast.LENGTH_LONG).show();
                            startMonitorActivity();
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + device);
                        finish();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mHandler = new Handler();
        setupUsbPermissionHandler();
        setupBluetooth();
        FirebaseApp.initializeApp(this);
        setupDeviceSwitches();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                startScanner(true);
            }
        }
    }

    private void setupBluetooth() {
        //Request Location
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        //Request Bluetooth
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        startScanner(true);
    }

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if ((device.getName() != null) && Device.BLUETOOTH_SENSOR.is(device.getName())); {
                // Found Device
                Log.d(TAG, "Found Bluetooth Device");
                startScanner(false);
                mBluetoothDevice = device;
            }
        }
    };

    private void startScanner(final boolean enable) {
        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                }
            }, BLUETOOTH_SCAN_PERIOD);
            bluetoothLeScanner.startScan(mLeScanCallback);
        } else {
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    private void setupDeviceSwitches() {
        mFingertipSwitch = findViewById(R.id.switch_fingertip);
        mFloraSwitch = findViewById(R.id.switch_flora);
        mBluetoothSwitch = findViewById(R.id.switch_bluetooth);
    }

    private void setupUsbPermissionHandler() {
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    public void onClickLogUsbDevices(View v) {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Log.d(TAG, deviceList.size() + " devices found.");
        Iterator it = deviceList.values().iterator();
        while (it.hasNext()) {
            UsbDevice device = (UsbDevice) it.next();
            String deviceName = device.getDeviceName();
            Log.d(TAG, "Device Name: " + deviceName);
            int vendorID = device.getVendorId();
            int productID = device.getProductId();
            Log.d(TAG, "Vendor ID: " + vendorID + ", Product ID: " + productID);
            String productName = device.getProductName();
            Log.d(TAG, "Product Name: " + productName);
            String manufacturerName = device.getManufacturerName();
            Log.d(TAG, "Manufacturer Name: " + manufacturerName);
            String serial = device.getSerialNumber();
            Log.d(TAG, "Serial No: " + device.getSerialNumber());
        }
    }

    public void onClickMonitorBtn(View v) {
        startMonitorActivity();
    }

    private void startMonitorActivity() {

        Intent intent = new Intent(this, MonitorActivity.class);
        ArrayList<String> usbDeviceNames = new ArrayList<>();

        if (mFingertipSwitch.isChecked()) {
            String name = getDeviceName(Device.FINGERTIP);
            if (name == null) {
                Log.e(TAG, "Unable to locate: " + Device.FINGERTIP.toString());
                return;
            }
            usbDeviceNames.add(name);
        }
        if (mFloraSwitch.isChecked()) {
            String name = getDeviceName(Device.MAX30102);
            if (name == null) {
                Log.e(TAG, "Unable to locate: " + Device.MAX30102.toString());
                return;
            }
            usbDeviceNames.add(name);
        }
        if (mBluetoothSwitch.isChecked()) {
            if (mBluetoothDevice == null) {
                Log.e(TAG, "Unable to locate: " + Device.BLUETOOTH_SENSOR.toString());
                return;
            }
            intent.putExtra(BLUETOOTH_DEVICE_PARAM, mBluetoothDevice);
        }
        intent.putStringArrayListExtra(USB_DEVICE_PARAM, usbDeviceNames);
        startActivity(intent);
    }

    private String getDeviceName(Device d) {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator it = deviceList.values().iterator();
        while (it.hasNext()) {
            UsbDevice device = (UsbDevice) it.next();
            String name = device.getProductName();
            if (d.is(name)) {
                if (!this.mUsbManager.hasPermission(device)) {
                    this.mUsbManager.requestPermission(device, this.mPermissionIntent);
                    return null;
                }
                return device.getDeviceName();
            }
            it.remove();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }
}
