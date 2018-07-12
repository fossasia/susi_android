package org.fossasia.susi.ai.device.deviceconnect;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiViewHolder extends RecyclerView.ViewHolder {

    public @BindView(R.id.wifi_name)
    TextView wifiName;

    protected DeviceConnectPresenter devicePresenter;

    public WifiViewHolder(View itemView, DeviceConnectPresenter devicePresenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.devicePresenter = devicePresenter;
    }
}
