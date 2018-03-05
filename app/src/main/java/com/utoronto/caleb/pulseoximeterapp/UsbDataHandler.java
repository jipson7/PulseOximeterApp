package com.utoronto.caleb.pulseoximeterapp;


public interface UsbDataHandler {
    public void handleIncomingData(int hr, int spo2, int bp);
}
