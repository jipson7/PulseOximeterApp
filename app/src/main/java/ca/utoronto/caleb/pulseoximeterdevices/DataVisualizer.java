package ca.utoronto.caleb.pulseoximeterdevices;


import java.util.Map;

public interface DataVisualizer {
    void updateUI(Device device, Map<String, Object> data);
}
