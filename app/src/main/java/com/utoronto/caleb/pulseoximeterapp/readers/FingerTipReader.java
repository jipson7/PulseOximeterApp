package com.utoronto.caleb.pulseoximeterapp.devices.readers;

import android.content.Context;

import com.utoronto.caleb.pulseoximeterapp.devices.Device;
import com.utoronto.caleb.pulseoximeterapp.devices.UsbDataHandler;
import com.utoronto.caleb.pulseoximeterapp.storage.DataKeys;

import java.util.HashMap;
import java.util.Map;

public class FingerTipReader extends AbstractDeviceReader {

    private String TAG = "FINGERTIP_READER";

    public static final boolean RECORD_BP = false;

    public FingerTipReader(String deviceName, Context context, UsbDataHandler handler) {
        super(deviceName, context, handler);
    }

    @Override
    public void saveData(byte[] bytes) {
        Map<String, Object> dataMap = new HashMap<>();
        String dataRead = bytesToHex(bytes);

        int hr = Integer.parseInt(dataRead.charAt(6) + "" + dataRead.charAt(7), 16);
        boolean hrValid = (127 != hr);
        if (hrValid)
            dataMap.put(DataKeys.HR, hr);

        int oxygen = Integer.parseInt(dataRead.charAt(8) + "" + dataRead.charAt(9), 16);
        boolean oxygenValid = (127 != oxygen);
        if (oxygenValid)
            dataMap.put(DataKeys.OXYGEN, oxygen);

        int bp = Integer.parseInt(dataRead.charAt(4) + "" + dataRead.charAt(5), 16);
        boolean bpValid = (111 != bp);
        if(bpValid && RECORD_BP)
            dataMap.put(DataKeys.BP, bp);

        if (hrValid || oxygenValid || bpValid)
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