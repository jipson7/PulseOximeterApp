package com.utoronto.caleb.pulseoximeterapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MonitorService extends IntentService {

    private final String TAG = "MONITOR_SERVICE";

    public static final String ACTION_MONITOR = "com.utoronto.caleb.pulseoximeterapp.action.MONITOR";

    public MonitorService() {
        super("MonitorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MONITOR.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                monitor();
            }
        }
    }

    private void monitor() {
        Log.d(TAG, "Begin monitoring.");
        stopSelf();
    }
}
