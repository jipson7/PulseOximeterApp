package com.utoronto.caleb.pulseoximeterapp.devices;


public enum Device {
    //TODO convert to class to save info about device
    FINGERTIP ("USBUART", "FingertipReader"),
    MAX30102 ("Flora", "MAX30102");

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
