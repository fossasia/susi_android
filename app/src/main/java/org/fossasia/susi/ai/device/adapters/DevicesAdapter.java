package org.fossasia.susi.ai.device.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.device.DevicePresenter;
import org.fossasia.susi.ai.device.contract.IDevicePresenter;

import java.util.ArrayList;
import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    private List<String> devices = new ArrayList<>();
    private IDevicePresenter devicePresenter;

    public DevicesAdapter(List<String> devices, IDevicePresenter devicePresenter) {
        this.devices = devices;
        this.devicePresenter = devicePresenter;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_layout, parent, false);
        return new DeviceViewHolder(v, (DevicePresenter) devicePresenter);
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
