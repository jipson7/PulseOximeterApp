package ca.utoronto.caleb.pulseoximeterdevices;


import java.util.Map;

public interface UsbDataHandler {
    void handleIncomingData(Device device, Map<String, Object> data);
    void endUsbConnections();
}
