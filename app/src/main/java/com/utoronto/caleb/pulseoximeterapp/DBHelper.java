package com.utoronto.caleb.pulseoximeterapp;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBHelper {

    static final String TAG = "DBHELPER";

    Trial mTrial;

    String collectionName = "trials";

    DatabaseReference mTrialsRef;

    public DBHelper() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mTrialsRef = database.getReference(collectionName);
        //TODO replace defaults
        String name = "defaultTrial";
        String description = "defaultDesc";
        mTrial = new Trial(name, description);
    }

    public void saveData(int hr, int spo2, int bp, long timestamp, Device device) {
        //TODO check trial and save data
    }

    public void endSession() {
        mTrial.end();
    }

    public void testSave() {
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
    }
}
