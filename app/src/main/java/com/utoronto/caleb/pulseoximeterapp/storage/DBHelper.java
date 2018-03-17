package com.utoronto.caleb.pulseoximeterapp.storage;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utoronto.caleb.pulseoximeterapp.devices.Device;

import java.util.HashMap;
import java.util.Map;

public class DBHelper {

    static final String TAG = "DBHELPER";

    Trial mTrial;

    String collectionName = "trials";

    FirebaseFirestore db;

    DocumentReference mTrialRef;

    Map<Device, CollectionReference> mDataRefs;

    public DBHelper() {
        mDataRefs = new HashMap<>();
        setupTrial();
    }

    private void setupTrial() {
        db = FirebaseFirestore.getInstance();
        mTrial = new Trial();
        mTrialRef = db.collection(collectionName).document();
        mTrialRef.set(mTrial);
    }

    public void saveData(Device device, Map<String, Object> data) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        CollectionReference dataRef = mDataRefs.get(device);

        if (dataRef == null) {
            DocumentReference deviceRef = mTrialRef.collection("devices").document();
            deviceRef.set(device);
            dataRef = deviceRef.collection("data");
            mDataRefs.put(device, dataRef);
        }

        dataRef.document(timestamp).set(data);
    }

    public void endSession() {
        mTrialRef.update("end", System.currentTimeMillis());
    }
}
