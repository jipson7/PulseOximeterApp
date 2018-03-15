package com.utoronto.caleb.pulseoximeterapp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Trial {
    public String name;
    public String description;
    public long start;

    public Trial(){}

    public Trial(String name, String description) {
        this.name = name;
        this.description = description;
        this.start = System.currentTimeMillis();
    }
}
