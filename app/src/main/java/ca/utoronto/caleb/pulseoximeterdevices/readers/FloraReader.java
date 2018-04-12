package ca.utoronto.caleb.pulseoximeterdevices.readers;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ca.utoronto.caleb.pulseoximeterdevices.Device;
import ca.utoronto.caleb.pulseoximeterdevices.UsbDataHandler;
import ca.utoronto.caleb.pulseoximeterdevices.storage.DataKeys;


public class FloraReader extends DeviceReader {

    private String TAG = "FLORA_READER";

    private Device mDevice;

    public FloraReader(Device device, Context context, UsbDataHandler handler) {
        super(device, context, handler);
        mDevice = device;
    }

    @Override
    public void saveData(byte[] bytes) {
        Map<String, Object> data = new HashMap<>();
        String jsonString = new String(bytes);
        if (jsonString.trim().isEmpty()){
            return;
        }
        try {

            JSONObject json = new JSONObject(jsonString);

            boolean hrValid = (1 == json.getInt("HRValid"));
            if (hrValid)
                data.put(DataKeys.HR, json.getInt("HR"));

            boolean oxygenValid = (1 == json.getInt("SPO2Valid"));
            if (oxygenValid)
                data.put(DataKeys.OXYGEN, json.getInt("SPO2"));

            if (oxygenValid || hrValid) {
                Log.d(TAG, "Flora Sensor data valid.");
                // Add lights if at least one sensor is getting valid results
                data.put(DataKeys.RED, json.getInt("red"));
                data.put(DataKeys.IR, json.getInt("ir"));
                mHandler.handleIncomingData(mDevice, data);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error in Flora Data");
        }
    }
}
