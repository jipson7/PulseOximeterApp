package com.utoronto.caleb.pulseoximeterapp;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public enum Device {
    FINGERTIP ("USBUART", "FingerTipDevice"),
    MAX30102 ("Flora", "MAX30102"),
    BLUETOOTH_SENSOR("OS58 Demo", "MAX86140");

    private final String deviceName;
    private final String description;

    Device(String name, String desc) {
        this.deviceName = name;
        this.description = desc;
    }

    public boolean is(String otherName) {
        if (otherName == null) {
            return false;
        }
        return deviceName.equals(otherName);
    }

    public boolean is(BluetoothDevice device) {
        String otherName = device.getName();
        return this.is(otherName);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", deviceName);
        data.put("description", description);
        return data;
    }

    @Override
    public String toString() {
        return this.deviceName;
    }
}