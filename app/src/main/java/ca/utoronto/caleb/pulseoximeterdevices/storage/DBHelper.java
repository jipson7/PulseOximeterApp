package ca.utoronto.caleb.pulseoximeterdevices.storage;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ca.utoronto.caleb.pulseoximeterdevices.Device;

public class DBHelper {

    static final String TAG = "DBHELPER";

    Trial mTrial;

    String trialsList = "trials";

    FirebaseDatabase db;

    DatabaseReference mTrialRef;

    Map<Device, DatabaseReference> mDataRefs;

    public DBHelper() {
        mDataRefs = new HashMap<>();
        db = FirebaseDatabase.getInstance();
    }

    public void saveData(Device device, Map<String, Object> data) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        data.put("timestamp", timestamp);

        DatabaseReference dataRef = mDataRefs.get(device);

        if (dataRef == null) {
            DatabaseReference deviceRef = mTrialRef.child("devices").push();
            deviceRef.setValue(device.toMap());
            dataRef = deviceRef.child("data");
            mDataRefs.put(device, dataRef);
        }

        dataRef.push().setValue(data);
    }

    public void endSession() {
        Map<String, Object> timeUpdate = new HashMap<>();
        timeUpdate.put("end", System.currentTimeMillis());
        mTrialRef.updateChildren(timeUpdate);
    }

    public void setupTrial(String trialDesc) {
        mTrial = new Trial(trialDesc);
        mTrialRef = db.getReference(trialsList).push();
        mTrialRef.setValue(mTrial.toMap());
    }
}
