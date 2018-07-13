package org.fossasia.susi.ai.device.deviceconnect.adapters.recycleradapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectPresenter;
import org.fossasia.susi.ai.device.deviceconnect.adapters.viewholders.DeviceViewHolder;
import org.fossasia.susi.ai.device.deviceconnect.adapters.viewholders.WifiViewHolder;
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectPresenter;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> itemList;
    private IDeviceConnectPresenter devicePresenter;
    private int viewCode;

    public DevicesAdapter(List<String> itemList, IDeviceConnectPresenter devicePresenter, int viewCode) {
        this.itemList = itemList;
        this.devicePresenter = devicePresenter;
        this.viewCode = viewCode;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewCode == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_layout, parent, false);
            return new DeviceViewHolder(v, (DeviceConnectPresenter) devicePresenter);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wifi_item, parent, false);
            return new WifiViewHolder(v, (DeviceConnectPresenter) devicePresenter);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DeviceViewHolder) {
            DeviceViewHolder viewHolder = (DeviceViewHolder) holder;
            String ssid = itemList.get(position);
            viewHolder.speakerName.setText(ssid);
        } else if (holder instanceof WifiViewHolder) {
            WifiViewHolder viewHolder = (WifiViewHolder) holder;
            viewHolder.wifiName.setText(itemList.get(position));

        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
