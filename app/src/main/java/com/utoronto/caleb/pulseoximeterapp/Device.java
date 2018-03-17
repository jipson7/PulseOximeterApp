package com.utoronto.caleb.pulseoximeterapp;

import java.util.HashMap;
import java.util.Map;

public enum Device {
    FINGERTIP ("USBUART", "FingerTipDevice"),
    MAX30102 ("Flora", "MAX30102");

    private final String name;
    private final String description;

    private Device(String name, String desc) {
        this.name = name;
        this.description = desc;
    }

    public boolean is(String otherName) {
        return name.equals(otherName);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        return data;
    }

    @Override
    public String toString() {
        return this.name;
    }
}