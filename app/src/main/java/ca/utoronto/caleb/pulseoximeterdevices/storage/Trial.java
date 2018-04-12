package ca.utoronto.caleb.pulseoximeterdevices.storage;

import java.util.Date;

public class Trial {
    public String desc;
    public long start;
    public String date;

    public Trial(String desc) {
        start = System.currentTimeMillis();
        date = (new Date(start)).toString();
        this.desc = desc;
    }
}
