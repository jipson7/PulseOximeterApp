package com.utoronto.caleb.pulseoximeterapp.devices;


import java.util.Map;

public interface UsbDataHandler {
    void handleIncomingData(Device device, Map<String, Object> data);
    void endUsbConnections();
}
