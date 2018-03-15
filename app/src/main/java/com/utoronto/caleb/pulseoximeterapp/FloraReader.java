package com.utoronto.caleb.pulseoximeterapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class FloraReader extends CustomDeviceReader {

    private String TAG = "FLORA_READER";

    public FloraReader(String deviceName, Context context, UsbDataHandler handler) {
        super(deviceName, context, handler);
    }

    @Override
    public void saveData(byte[] bytes) {
        String jsonString = new String(bytes);
        if (jsonString.trim().isEmpty()){
            return;
        }
        try {
            JSONObject json = new JSONObject(jsonString);
            int red = json.getInt("red");
            int ir = json.getInt("ir");
            int hr = json.getInt("HR");
            boolean hrValid = (1 == json.getInt("HRValid"));
            int oxygen = json.getInt("SPO2");
            boolean oxygenValid = (1 == json.getInt("SPO2Valid"));
        } catch (JSONException e) {
            e.printStackTrace();
            stopMonitor();
        }
    }
}
