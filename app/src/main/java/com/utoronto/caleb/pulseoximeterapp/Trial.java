package com.utoronto.caleb.pulseoximeterapp;

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
