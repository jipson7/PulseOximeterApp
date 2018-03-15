package com.utoronto.caleb.pulseoximeterapp;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class FingerTipReader extends CustomDeviceReader {

    private String TAG = "FINGERTIP_READER";

    public FingerTipReader(String deviceName, Context context, UsbDataHandler handler) {
        super(deviceName, context, handler);
    }

    @Override
    public void saveData(byte[] bytes) {
        Map<String, Object> dataMap = new HashMap<>();
        String dataRead = bytesToHex(bytes);
        int hr = Integer.parseInt(dataRead.charAt(6) + "" + dataRead.charAt(7), 16);
        boolean hrValid = (127 != hr);
        int oxygen = Integer.parseInt(dataRead.charAt(8) + "" + dataRead.charAt(9), 16);
        boolean oxygenValid = (127 != oxygen);
        int bp = Integer.parseInt(dataRead.charAt(4) + "" + dataRead.charAt(5), 16);
        boolean bpValid = (112 != bp);
        dataMap.put(DataKeys.HR, hr);
        dataMap.put(DataKeys.HR_VALID, hrValid);
        dataMap.put(DataKeys.OXYGEN, oxygen);
        dataMap.put(DataKeys.OXYGEN_VALID, oxygenValid);
        dataMap.put(DataKeys.BP, bp);
        dataMap.put(DataKeys.BP_VALID, bpValid);
        mHandler.handleIncomingData(Device.FINGERTIP, dataMap);
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
