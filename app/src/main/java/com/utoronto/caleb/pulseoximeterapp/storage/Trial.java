package com.utoronto.caleb.pulseoximeterapp.storage;

import java.util.Date;

public class Trial {
    public long start;
    public String date;

    public Trial() {
        start = System.currentTimeMillis();
        date = (new Date(start)).toString();
    }
}
