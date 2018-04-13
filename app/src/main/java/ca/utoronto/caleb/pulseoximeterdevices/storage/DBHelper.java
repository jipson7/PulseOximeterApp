package ca.utoronto.caleb.pulseoximeterdevices.storage;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ca.utoronto.caleb.pulseoximeterdevices.Device;

public class DBHelper {

    static final String TAG = "DBHELPER";

    Trial mTrial;

    String collectionName = "trials";

    FirebaseFirestore db;

    DocumentReference mTrialRef;

    Map<Device, CollectionReference> mDataRefs;

    public DBHelper() {
        mDataRefs = new HashMap<>();
        db = FirebaseFirestore.getInstance();
    }

    public void saveData(Device device, Map<String, Object> data) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        CollectionReference dataRef = mDataRefs.get(device);

        if (dataRef == null) {
            DocumentReference deviceRef = mTrialRef.collection("devices").document();
            deviceRef.set(device.toMap());
            dataRef = deviceRef.collection("data");
            mDataRefs.put(device, dataRef);
        }

        dataRef.document(timestamp).set(data);
    }

    public void endSession() {
        mTrialRef.update("end", System.currentTimeMillis());
    }

    public void setupTrial(String trialDesc) {
        mTrial = new Trial(trialDesc);
        mTrialRef = db.collection(collectionName).document();
        mTrialRef.set(mTrial.toMap());
    }
}
