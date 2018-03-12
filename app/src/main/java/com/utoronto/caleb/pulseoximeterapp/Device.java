package com.utoronto.caleb.pulseoximeterapp;

/**
 * Created by caleb on 2018-03-12.
 */

public enum Device {
    FINGERTIP ("USBUART");

    private final String name;
    Device(String name) {
        this.name = name;
    }

    public boolean nameEquals(String otherName) {
        return name.equals(otherName);
    }
}
