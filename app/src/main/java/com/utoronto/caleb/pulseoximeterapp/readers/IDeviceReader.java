package com.utoronto.caleb.pulseoximeterapp.readers;

public interface IDeviceReader {
    void stopMonitor();
    void saveData(byte[] bytes);
}
