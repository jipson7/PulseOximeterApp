package ca.utoronto.caleb.pulseoximeterdevices.storage;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ca.utoronto.caleb.pulseoximeterdevices.Device;

public class DBHelper {

    static final String TAG = "DBHELPER";

    DatabaseReference db;

    DatabaseReference mTrialDataRef;

    Map<Device, DatabaseReference> mDataRefs;

    String mTrialKey;

    public DBHelper() {
        mDataRefs = new HashMap<>();
        db = FirebaseDatabase.getInstance().getReference();
    }

    public void saveData(Device device, Map<String, Object> data) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        data.put("timestamp", timestamp);

        DatabaseReference dataRef = mDataRefs.get(device);

        if (dataRef == null) {
            DatabaseReference deviceRef = mTrialDataRef.child("devices").push();
            deviceRef.setValue(device.toMap());
            dataRef = deviceRef.child("data");
            mDataRefs.put(device, dataRef);
        }

        dataRef.push().setValue(data);
    }

    public void endSession() {
        Long end =  System.currentTimeMillis();
        db.child("/trials/" + mTrialKey).child("end").setValue(end);
        db.child("/trials-data/" + mTrialKey).child("end").setValue(end);
    }

    public void setupTrial(String trialDesc) {
        Map<String, Object> trialData = (new Trial(trialDesc)).toMap();
        mTrialDataRef = db.child("trials-data").push();
        mTrialKey = mTrialDataRef.getKey();
        Map<String, Object> updates = new HashMap<>();
        updates.put("/trials-data/" + mTrialKey, trialData);
        updates.put("/trials/" + mTrialKey, trialData);
        db.updateChildren(updates);
    }
}
