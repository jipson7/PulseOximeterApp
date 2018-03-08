package com.utoronto.caleb.pulseoximeterapp;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;

/**
 * Created by caleb on 2018-02-24.
 */

public class FingerTipReader extends Thread {

    private String TAG = "FINGERTIP_READER";

    private String mDeviceName;

    private UsbManager mUsbManager = null;

    private Context mContext;

    private UsbSerialDevice mSerial;

    volatile boolean running = true;

    private UsbDataHandler mHandler;


    public FingerTipReader(String deviceName, Context context, UsbDataHandler handler) {
        this.mDeviceName = deviceName;
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void run() {
        Log.d(TAG, "FingerTipReader Runnable Launched.");
        if (mUsbManager == null) {
            mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        }
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        UsbDevice device = deviceList.get(mDeviceName);
        monitorDevice(device);
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {

        @Override
        public void onReceivedData(byte[] arg0)
        {
            if (!running || arg0.length == 0) {
                mSerial.close();
                mHandler.endUsbConnections();
                return;
            }
            String dataRead = bytesToHex(arg0);
            int hr = Integer.parseInt(dataRead.charAt(6) + "" + dataRead.charAt(7), 16);
            int spo2 = Integer.parseInt(dataRead.charAt(8) + "" + dataRead.charAt(9), 16);
            int bp = Integer.parseInt(dataRead.charAt(4) + "" + dataRead.charAt(5), 16);
            mHandler.handleIncomingData(hr, spo2, bp);
        }

    };

    private void monitorDevice(UsbDevice device) {
        UsbDeviceConnection connection = this.mUsbManager.openDevice(device);
        mSerial = UsbSerialDevice.createUsbSerialDevice(device, connection);
        mSerial.open();
        mSerial.read(mCallback);
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public void stopMonitor() {
        this.running = false;
    }
}
