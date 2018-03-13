package com.utoronto.caleb.pulseoximeterapp;

/**
 * Created by caleb on 2018-03-12.
 */

public enum Device {
    FINGERTIP ("USBUART", "FingertipReader");

    private final String name;
    private final String description;
    Device(String name, String desc) {
        this.name = name;
        this.description = desc;
    }

    public boolean nameEquals(String otherName) {
        return name.equals(otherName);
    }

    public String getDescription() {
        return description;
    }
}
