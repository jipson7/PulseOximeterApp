package com.utoronto.caleb.pulseoximeterapp;

import android.content.Context;

public class FingerTipReader extends CustomDeviceReader {

    private String TAG = "FINGERTIP_READER";

    public FingerTipReader(String deviceName, Context context, UsbDataHandler handler) {
        super(deviceName, context, handler);
    }

    @Override
    public void saveData(byte[] bytes) {
        long millis = System.currentTimeMillis();
        String dataRead = bytesToHex(bytes);
        int hr = Integer.parseInt(dataRead.charAt(6) + "" + dataRead.charAt(7), 16);
        int spo2 = Integer.parseInt(dataRead.charAt(8) + "" + dataRead.charAt(9), 16);
        int bp = Integer.parseInt(dataRead.charAt(4) + "" + dataRead.charAt(5), 16);
        mHandler.handleIncomingData(hr, spo2, bp, millis, Device.FINGERTIP);
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
