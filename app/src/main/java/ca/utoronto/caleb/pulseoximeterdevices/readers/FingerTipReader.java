package ca.utoronto.caleb.pulseoximeterdevices.readers;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import ca.utoronto.caleb.pulseoximeterdevices.Device;
import ca.utoronto.caleb.pulseoximeterdevices.UsbDataHandler;
import ca.utoronto.caleb.pulseoximeterdevices.storage.DataKeys;

public class FingerTipReader extends DeviceReader {

    private String TAG = "FINGERTIP_READER";

    final boolean RECORD_BP = false;

    private Device mDevice;

    public FingerTipReader(Device device, Context context, UsbDataHandler handler) {
        super(device, context, handler);
        mDevice = device;
    }

    @Override
    public void saveData(byte[] bytes) {
        Map<String, Object> dataMap = new HashMap<>();
        String dataRead = bytesToHex(bytes);

        int hr, oxygen, bp;

        try {
            hr = Integer.parseInt(dataRead.charAt(6) + "" + dataRead.charAt(7), 16);
            oxygen = Integer.parseInt(dataRead.charAt(8) + "" + dataRead.charAt(9), 16);
            bp = Integer.parseInt(dataRead.charAt(4) + "" + dataRead.charAt(5), 16);
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, "Malformed data received.");
            return;
        }
        boolean hrValid = (127 != hr);
        if (hrValid)
            dataMap.put(DataKeys.HR, hr);

        boolean oxygenValid = (127 != oxygen);
        if (oxygenValid)
            dataMap.put(DataKeys.OXYGEN, oxygen);

        boolean bpValid = (111 != bp) && RECORD_BP;
        if(bpValid)
            dataMap.put(DataKeys.BP, bp);

        if (hrValid || oxygenValid || bpValid) {
            mHandler.handleIncomingData(mDevice, dataMap);
        }
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
