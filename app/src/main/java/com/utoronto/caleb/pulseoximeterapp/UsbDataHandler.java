package com.utoronto.caleb.pulseoximeterapp;


import java.util.Map;

public interface UsbDataHandler {
    public void handleIncomingData(Device device, Map<String, Object> data);
    public void endUsbConnections();
}
