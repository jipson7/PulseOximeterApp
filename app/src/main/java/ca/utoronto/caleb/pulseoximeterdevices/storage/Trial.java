package ca.utoronto.caleb.pulseoximeterdevices.storage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Trial {
    public String desc;
    public long start;
    public String date;

    public Trial(String desc) {
        start = System.currentTimeMillis();
        date = (new Date(start)).toString();
        this.desc = desc;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("desc", desc);
        data.put("date", date);
        data.put("start", start);
        return data;
    }
}
