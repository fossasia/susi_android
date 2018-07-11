package org.fossasia.susi.ai.device.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectPresenter;
import org.fossasia.susi.ai.device.contract.IDeviceConnectPresenter;

import java.util.ArrayList;
import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    private List<String> devices;
    private IDeviceConnectPresenter devicePresenter;

    public DevicesAdapter(List<String> devices, IDeviceConnectPresenter devicePresenter) {
        this.devices = devices;
        this.devicePresenter = devicePresenter;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_layout, parent, false);
        return new DeviceViewHolder(v, (DeviceConnectPresenter) devicePresenter);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        String ssid = devices.get(position);
        holder.speakerName.setText(ssid);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
