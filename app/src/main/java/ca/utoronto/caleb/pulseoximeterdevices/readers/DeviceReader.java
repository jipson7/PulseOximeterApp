package ca.utoronto.caleb.pulseoximeterdevices.readers;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import ca.utoronto.caleb.pulseoximeterdevices.Device;
import ca.utoronto.caleb.pulseoximeterdevices.UsbDataHandler;

public abstract class DeviceReader extends Thread {

    private UsbSerialDevice mSerial;

    volatile boolean running = true;

    public UsbDataHandler mHandler;

    public abstract void saveData(byte[] bytes);

    public DeviceReader(Device device, Context context, UsbDataHandler handler) {
        this.mHandler = handler;
        this.mSerial = getDeviceSerial(device.getUsb(), context);
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

    private void monitorDevice() {
        mSerial.open();
        mSerial.read(mCallback);
    }

    private UsbSerialDevice getDeviceSerial(UsbDevice device, Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        UsbDeviceConnection connection = usbManager.openDevice(device);
        return UsbSerialDevice.createUsbSerialDevice(device, connection);
    }

    public void stopMonitor() {
        this.running = false;
        this.interrupt();
    }

}
