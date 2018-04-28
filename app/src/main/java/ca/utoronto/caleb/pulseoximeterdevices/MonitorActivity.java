package ca.utoronto.caleb.pulseoximeterdevices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

public class MonitorActivity extends Activity implements DataVisualizer {
    private final String TAG = "MONITOR_ACTIVITY";

    public static final String ACTION_MONITOR = "com.utoronto.caleb.pulseoximeterapp.action.MONITOR";
    public static final String ACTION_STOP_MONITOR = "com.utoronto.caleb.pulseoximeterapp.action.STOP_MONITOR";
    public static final String EXTRA_TRIAL_DESCRIPTION = "com.utoronto.caleb.pulseoximeterapp.param.TRIAL_DESC";

    Intent mMonitorServiceIntent = null;
    private MonitorService mMonitorService;
    private boolean mBound = false;
    private Bundle mExtras;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mDeviceInfoAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_STOP_MONITOR.equals(action)) {
                Log.d(TAG, "Broadcast to end monitoring received");
                endMonitoring();
            }
        }
    };


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MonitorService.MonitorBinder binder = (MonitorService.MonitorBinder) service;
            mMonitorService = binder.getService();
            mMonitorService.setDataVisualizer(MonitorActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service disconnected.");
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        registerReceiver(mReceiver, new IntentFilter(ACTION_STOP_MONITOR));
        mExtras = getIntent().getExtras();
        createMonitorServiceIntent();
        requestTrialDescription();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mRecyclerView =  findViewById(R.id.device_data_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mDeviceInfoAdapter = new DeviceInfoAdapter(mDeviceDataMap);
        mRecyclerView.setAdapter(mDeviceInfoAdapter);
    }

    private void createMonitorServiceIntent() {
        Log.d(TAG, "Starting Monitor service.");
        mMonitorServiceIntent = new Intent(this, MonitorService.class);
        mMonitorServiceIntent.setAction(ACTION_MONITOR);
        mMonitorServiceIntent.putExtras(mExtras);
    }

    private void startAndBindService() {
        startService(mMonitorServiceIntent);
        bindService(mMonitorServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void btnClickEndMonitoring(View v) {
        endMonitoring();
    }

    private void endMonitoring(){
        Log.d(TAG, "Stopping Monitor service and subtasks.");
        if (mMonitorServiceIntent != null) {
            stopService(mMonitorServiceIntent);
        }
        finish();
    }

    public void closeWindow(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        mBound = false;
    }


    private DeviceReadingMap<Device, Map<String, Object>> mDeviceDataMap = new DeviceReadingMap<>();
    @Override
    public void updateUI(Device device, Map<String, Object> data) {
        mDeviceDataMap.put(device, data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceInfoAdapter.notifyDataSetChanged();
            }
        });
    }

    private void requestTrialDescription() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a description of this experiment/trial.");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String desc = input.getText().toString();
                mMonitorServiceIntent.putExtra(EXTRA_TRIAL_DESCRIPTION, desc);
                startAndBindService();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(MonitorActivity.this, R.string.trial_required, Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.show();
    }
}
