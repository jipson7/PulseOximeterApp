package com.utoronto.caleb.pulseoximeterapp.devices.readers;

import android.content.Context;

import com.utoronto.caleb.pulseoximeterapp.devices.Device;
import com.utoronto.caleb.pulseoximeterapp.devices.UsbDataHandler;
import com.utoronto.caleb.pulseoximeterapp.storage.DataKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FloraReader extends AbstractDeviceReader {

    private String TAG = "FLORA_READER";

    public FloraReader(String deviceName, Context context, UsbDataHandler handler) {
        super(deviceName, context, handler);
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
            data.put(DataKeys.RED, json.getInt("red"));
            data.put(DataKeys.IR, json.getInt("ir"));
            data.put(DataKeys.HR, json.getInt("HR"));
            data.put(DataKeys.HR_VALID, (1 == json.getInt("HRValid")));
            data.put(DataKeys.OXYGEN, json.getInt("SPO2"));
            data.put(DataKeys.OXYGEN_VALID, (1 == json.getInt("SPO2Valid")));
            mHandler.handleIncomingData(Device.MAX30102, data);
        } catch (JSONException e) {
            e.printStackTrace();
            stopMonitor();
        }
    }
}
