package ca.utoronto.caleb.pulseoximeterdevices;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    ArrayList<Device> mDevices;
    DescriptionRequester descRequest;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, desc, userDesc;
        public Device device;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            name = v.findViewById(R.id.device_name);
            desc = v.findViewById(R.id.device_desc);
            userDesc = v.findViewById(R.id.device_userdesc);
        }

        public void setDevice(Device d) {
            device = d;
            name.setText(d.getName());
            desc.setText(d.getDescription());
            String userDesc = d.getUserDescription();
            if (userDesc != null) {
                this.userDesc.setText(userDesc);
            }
        }

        @Override
        public void onClick(View v) {
            DeviceAdapter.this.descRequest.requestUserDescription(device);
        }
    }

    public DeviceAdapter(ArrayList<Device> devices, DescriptionRequester requester) {
        mDevices = devices;
        descRequest = requester;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device d = mDevices.get(position);
        holder.setDevice(d);
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }
}
