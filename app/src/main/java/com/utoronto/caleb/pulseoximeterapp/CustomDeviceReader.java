package com.utoronto.caleb.pulseoximeterapp;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;

public abstract class CustomDeviceReader extends Thread {

    private UsbSerialDevice mSerial;

    volatile boolean running = true;

    public UsbDataHandler mHandler;

    public abstract void saveData(byte[] bytes);

    public CustomDeviceReader() {}

    public CustomDeviceReader(String deviceName, Context context, UsbDataHandler handler) {
        this.mHandler = handler;
        this.mSerial = getDeviceSerial(deviceName, context);
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {

        @Override
        public void onReceivedData(byte[] bytes)
        {
            if (!running || bytes.length == 0) {
                mSerial.close();
                mHandler.endUsbConnections();
                return;
            }
            saveData(bytes);
        }

    };

    @Override
    public void run() {
        monitorDevice();
    }

    public void monitorDevice() {
        mSerial.open();
        mSerial.read(mCallback);
    }

    public UsbSerialDevice getDeviceSerial(String deviceName, Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        UsbDevice device = deviceList.get(deviceName);
        UsbDeviceConnection connection = usbManager.openDevice(device);
        return UsbSerialDevice.createUsbSerialDevice(device, connection);
    }

    public void stopMonitor() {
        this.running = false;
        this.interrupt();
    }

}
