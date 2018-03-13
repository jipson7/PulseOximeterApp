package com.utoronto.caleb.pulseoximeterapp;

import com.google.firebase.database.FirebaseDatabase;

public class DBHelper {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public DBHelper() {
        //TODO Setup trial (use class) here.
    }

    public void saveData(int hr, int spo2, int bp, long timestamp, Device device) {
        //TODO check trial and save data
    }

    public void endSession() {
        //TODO cleanup
    }
}
