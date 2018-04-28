package ca.utoronto.caleb.pulseoximeterdevices;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import ca.utoronto.caleb.pulseoximeterdevices.storage.DataKeys;


class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceInfoAdapter.ViewHolder> {

    private final DeviceReadingMap<Device, Map<String, Object>> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name, o2, hr, red, ir;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.device_label);
            o2 = v.findViewById(R.id.device_reading_o2);
            hr = v.findViewById(R.id.device_reading_hr);
            red = v.findViewById(R.id.device_reading_red_led);
            ir = v.findViewById(R.id.device_reading_ir_led);
        }

        public void setDisplay(Map.Entry<Device, Map<String, Object>> entry) {
            Device device = entry.getKey();
            Map<String, Object> data = entry.getValue();
            name.setText(device.getName());
            Object o2_value = data.get(DataKeys.OXYGEN);
            Object hr_value =  data.get(DataKeys.HR);
            Object red_value = data.get(DataKeys.RED);
            Object ir_value = data.get(DataKeys.IR);
            if (o2_value != null) {
                o2.setText(String.valueOf((int) o2_value));
            }
            if (hr_value != null) {
                hr.setText(String.valueOf((int) hr_value));
            }
            if (red_value != null) {
                red.setText(String.valueOf((int) red_value));
            }
            if (ir_value != null) {
                ir.setText(String.valueOf((int) ir_value));
            }
        }
    }


    public DeviceInfoAdapter(DeviceReadingMap<Device, Map<String, Object>> mDeviceDataMap) {
        mDataset = mDeviceDataMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_data, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setDisplay(mDataset.getEntry(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
