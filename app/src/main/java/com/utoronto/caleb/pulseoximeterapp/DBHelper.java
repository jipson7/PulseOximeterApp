package com.utoronto.caleb.pulseoximeterapp;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DBHelper {

    static final String TAG = "DBHELPER";

    Trial mTrial;

    String collectionName = "trials";

    DatabaseReference mTrialsRef;
    DatabaseReference hrRef;
    DatabaseReference oxygenRef;
    DatabaseReference bpRef;

    public DBHelper() {
        setupTrial();
    }

    private void setupTrial() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mTrialsRef = database.getReference(collectionName).push();
        //TODO replace defaults
        String name = "defaultTrial";
        String description = "defaultDesc";
        mTrial = new Trial(name, description);
        mTrialsRef.setValue(mTrial);
        DatabaseReference dataRef = mTrialsRef.child("data");
        hrRef = dataRef.child("hr");
        oxygenRef = dataRef.child("oxygen");
        bpRef = dataRef.child("bp");
    }

    public void saveData(int hr, int spo2, int bp, long timestamp, Device device) {
        hrRef.child(device.getDescription())
                .child(String.valueOf(timestamp))
                .setValue(hr);

        oxygenRef.child(device.getDescription())
                .child(String.valueOf(timestamp))
                .setValue(spo2);

        bpRef.child(device.getDescription())
                .child(String.valueOf(timestamp))
                .setValue(bp);
    }

    public void endSession() {
        Map<String, Object> trialUpdate = new HashMap<>();
        trialUpdate.put("end", System.currentTimeMillis());
        mTrialsRef.updateChildren(trialUpdate);
    }

/*    public void testSave() {
        Log.d(TAG, "ATTEMPTING TO SAVE DATA.");
        mTrialsRef.push().setValue(mTrial, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG,"Data could not be saved " + databaseError.getMessage());
                } else {
                    Log.d(TAG,"Data saved successfully.");
                }
            }
        });
    }*/
}
