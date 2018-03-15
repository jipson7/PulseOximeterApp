package com.utoronto.caleb.pulseoximeterapp.storage;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utoronto.caleb.pulseoximeterapp.devices.Device;

import java.util.Map;

public class DBHelper {

    static final String TAG = "DBHELPER";

    Trial mTrial;

    String collectionName = "trials";

    FirebaseFirestore db;

    DocumentReference mTrialRef;

    public DBHelper() {
        setupTrial();
    }

    private void setupTrial() {
        db = FirebaseFirestore.getInstance();
        //TODO replace defaults
        String name = "defaultTrial";
        String description = "defaultDesc";
        mTrial = new Trial(name, description);
        mTrialRef = db.collection(collectionName).document();
        mTrialRef.set(mTrial);
    }

    public void saveData(Device device, Map<String, Object> data) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        mTrialRef.collection("devices")
                .document(device.getDescription())
                .collection("data")
                .document(String.valueOf(timestamp))
                .set(data);
    }

    public void endSession() {
        mTrialRef.update("end", System.currentTimeMillis());
    }
}
