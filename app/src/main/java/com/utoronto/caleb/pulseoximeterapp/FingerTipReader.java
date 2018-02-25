package com.utoronto.caleb.pulseoximeterapp;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

/**
 * Created by caleb on 2018-02-24.
 */

public class FingerTipReader implements Runnable {

    private String TAG = "FINGERTIP_READER";

    private String mDeviceName;

    private UsbManager mUsbManager = null;

    Context mContext;

    public FingerTipReader(String deviceName, Context context) {
        this.mDeviceName = deviceName;
        this.mContext = context;
    }

    @Override
    public void run() {
        if (mUsbManager == null) {
            mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        }
        Log.d(TAG, "FingerTipReader Thread Launched.");
    }

    private void monitorDevice(UsbDevice device) {
        int BUFFER_SIZE = 500;
        UsbEndpoint usbEndpoint = getBulkInEndpoint(device);
        UsbDeviceConnection connection = this.mUsbManager.openDevice(device);

        while(true) {
            byte[] bytesIn = new byte[usbEndpoint.getMaxPacketSize()];
            int result = connection.bulkTransfer(usbEndpoint, bytesIn, bytesIn.length, BUFFER_SIZE);
            if (result < 0) {
                Log.d(TAG, "Usb read result is -1, ending loop");
                break;
            }
            String dataRead = bytesToHex(bytesIn);
            int currHeartRate = Integer.parseInt(dataRead.charAt(6) + "" + dataRead.charAt(7), 16);
            int currSpo2 = Integer.parseInt(dataRead.charAt(8) + "" + dataRead.charAt(9), 16);
            int currBP = Integer.parseInt(dataRead.charAt(4) + "" + dataRead.charAt(5), 16);
            Log.d(TAG, currHeartRate + " " + currSpo2 + " " + currBP);
        }
    }


    private UsbEndpoint getBulkInEndpoint(UsbDevice device) {
        UsbEndpoint inEndpoint = null;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbInterface = device.getInterface(i);
            for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                if (usbInterface.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN) {
                    inEndpoint = usbInterface.getEndpoint(j);
                    if (inEndpoint != null && inEndpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        Log.d(TAG, "BulkIn endpoint found. ");
                        break;
                    }
                }
            }
        }
        return inEndpoint;
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
}
