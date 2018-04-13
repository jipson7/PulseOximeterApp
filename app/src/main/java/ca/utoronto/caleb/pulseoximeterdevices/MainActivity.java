package ca.utoronto.caleb.pulseoximeterdevices;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends Activity implements DescriptionRequester {

    public static final String TAG = "PULSE_OXIMETER_DEVICES";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    UsbManager mUsbManager;

    ArrayList<Device> mDevices;

    public static final String DEVICE_LIST_PARAM =
            "com.utoronto.caleb.pulseoximeterapp.param.DEVICE_LIST_PARAMETER";

    private PendingIntent mPermissionIntent;

    private static final String ACTION_USB_PERMISSION = "com.utoronto.caleb.pulseoximeterapp.action.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            Toast.makeText(MainActivity.this, R.string.device_granted, Toast.LENGTH_LONG).show();
                            addDevice(device);
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device" + device);
                        finish();
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.d(TAG, "USB DEVICE ATTACHED");
                if (device != null) {
                    addDevice(device);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.d(TAG, "Usb device DETACHED");
                setupUsbDevices();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDevices = new ArrayList<>();
        setupRecyclerView();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        setupUsbPermissionHandler();
        setupUsbDevices();
        FirebaseApp.initializeApp(this);
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.device_list_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DeviceAdapter(mDevices, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupUsbPermissionHandler() {
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
    }

    private void setupUsbDevices() {
        mDevices.clear();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator it = deviceList.values().iterator();
        while (it.hasNext()) {
            UsbDevice usbDevice = (UsbDevice) it.next();
            addDevice(usbDevice);
            it.remove();
        }
    }

    private void addDevice(UsbDevice usbDevice) {
        Device device = new Device(usbDevice);
        if (device.isValid()) {
            if (!this.mUsbManager.hasPermission(usbDevice)) {
                this.mUsbManager.requestPermission(usbDevice, this.mPermissionIntent);
            } else {
                mDevices.add(device);
                mAdapter.notifyDataSetChanged();
            }
        }
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
        if (mDevices.size() > 0) {
            intent.putParcelableArrayListExtra(DEVICE_LIST_PARAM, mDevices);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, R.string.no_devices, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }

    @Override
    public void requestUserDescription(final Device device) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a description of this device.");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String desc = input.getText().toString();
                if (!desc.trim().isEmpty()) {
                    device.setUserDescription(desc);
                    MainActivity.this.mAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}