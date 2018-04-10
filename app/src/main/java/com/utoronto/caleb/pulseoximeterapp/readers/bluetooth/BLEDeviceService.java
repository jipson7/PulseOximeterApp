package com.utoronto.caleb.pulseoximeterapp.readers.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.utoronto.caleb.pulseoximeterapp.MainActivity;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class BLEDeviceService extends Service {

    private final static String TAG = "BLE_DEVICE_SERVICE";

    private Handler bleHandler;
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private DataParser mDataParser;
    private Semaphore available;

    private static final UUID NRF52_SERVICE = UUID
            .fromString("6E400000-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID NRF52_DATA_NOTIFICATION = UUID
            .fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID NRF52_CONFIG_RW = UUID
            .fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID CONFIG_DESCRIPTOR = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");

    private byte reg_slave_type = 0;
    private byte reg_len = 0;
    private byte reg_addr = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "BLE Service starting...");
        mDataParser = new DataParser();
        bleHandler = new Handler();
        mDevice = intent.getExtras().getParcelable(MainActivity.BLUETOOTH_DEVICE_PARAM);
        mBluetoothGatt = mDevice.connectGatt(this, false, mGattCallback);
        return super.onStartCommand(intent, flags, startId);
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            Log.d(TAG, "Connection State Change: " + status + " -> "
                    + newState + ":" +connectionState(newState));
            if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();

            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                gatt.disconnect();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered: " + status);
            available = new Semaphore(1, true);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (serviceCheck()) {
                    Log.d(TAG, "Service found starting sensors");
                    byte[] configData = FWAPI.setNRF_ENABLE_SENSORS(FWAPI.START);
                    writeConfig(configData);
                    enableNotification(true);
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            byte readData[] = characteristic.getValue();
            //displayPacketBin("onCharacteristicRead", characteristic, readData);
            if ( characteristic.getUuid().equals(NRF52_CONFIG_RW) ) {
                if ( readData[0] == FWAPI.MSG_REGVAL)
                {
                    readData[5] = reg_slave_type;
                    readData[6] = reg_addr;
                    readData[7] = reg_len;
                }
                announceNRF52_CONFIG_Read(readData);
                unLockComm();
            }
            else if ( characteristic.getUuid().equals(NRF52_DATA_NOTIFICATION) )
            {
                byte[] copiedData = Arrays.copyOf(readData, 20);
                announceNRF52_DATA_NOTIFICATION(copiedData);
            }

            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            //Log.d(TAG, "onCharacteristicWrite: " + status);
            unLockComm();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            byte[] copiedData = characteristic.getValue();
            if ( characteristic.getUuid().equals(NRF52_DATA_NOTIFICATION) )
                announceNRF52_DATA_NOTIFICATION(copiedData);
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "onDescriptorWrite:" + status);
            unLockComm();
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }
    };

    public void enableNotification(boolean enable)
    {
        final boolean fEnable = enable;
        if ( serviceCheck() )
        {
            Log.d(TAG, "Enabling Notifications");
            bleHandler.post(new Runnable() {
                @Override
                public void run() {
                    BluetoothGattCharacteristic nrf52_data_notification;
                    try {
                        lockComm();
                        nrf52_data_notification = mBluetoothGatt.getService(NRF52_SERVICE).getCharacteristic(NRF52_DATA_NOTIFICATION);
                        mBluetoothGatt.setCharacteristicNotification(nrf52_data_notification, fEnable);

                        BluetoothGattDescriptor desc = nrf52_data_notification.getDescriptor(CONFIG_DESCRIPTOR);
                        desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(desc);
                        Log.d(TAG, "Wrote Descriptor to enable notifications");
                    } catch ( NullPointerException e) {
                        unLockComm();
                        e.printStackTrace();
                        Log.e(TAG, "enableNotification Error");
                    }
                }
            });

        }
        else
        {
            Log.d(TAG, "Unable to enable notifications.");
        }
    }


    private boolean serviceCheck()
    {

        boolean ret;
        try {
            mBluetoothGatt.getService(NRF52_SERVICE).getCharacteristic(NRF52_CONFIG_RW);
            ret = true;
            Log.d(TAG, "Service Found");
        } catch ( NullPointerException e) {
            e.printStackTrace();
            ret = false;
            Log.e(TAG, "Service Check Error");
        }
        return ret;
    }

    public void writeConfig(byte[] configData)
    {
        final byte[] cData = configData;

        if ( serviceCheck() )
        {
            bleHandler.post(new Runnable() {
                @Override
                public void run() {
                    BluetoothGattCharacteristic nrf52_config_rw;
                    try {
                        lockComm();
                        nrf52_config_rw = mBluetoothGatt.getService(NRF52_SERVICE).getCharacteristic(NRF52_CONFIG_RW);
                        nrf52_config_rw.setValue(cData);
                        mBluetoothGatt.writeCharacteristic(nrf52_config_rw);

                        String cDataValue = "writeConfig:";
                        for ( int i = 0; i < cData.length; i++ ) {
                            cDataValue += String.format("%02x", cData[i]) + " ";
                        }
                        Log.d("CONFIG", cDataValue);
                    } catch ( NullPointerException e) {
                        unLockComm();
                        e.printStackTrace();
                        Log.e(TAG, "writeConfig Error");
                    }
                }
            });

        }
        else
        {
            Log.d(TAG, "Unable to write config, service not connected.");
        }
    }

    private void announceNRF52_CONFIG_Read(byte[] packet) {
        Log.d(TAG, "Config Read Notification");
    }

    private void announceNRF52_DATA_NOTIFICATION(byte[] packet) {
        mDataParser.parseNRF52_DATA_NOTIFICATION(packet);
    }


    private void lockComm()
    {
        try {
            Log.d(TAG, "Acquiring communication lock.");
            available.acquire();
            Log.d(TAG, "Communication lock acquired.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to acquire communication lock.");
        }
    }

    private void unLockComm()
    {
        available.release();
        Log.d(TAG, "Communication Lock Released");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "BLE Service Destroyed");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
