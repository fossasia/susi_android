package org.fossasia.susi.ai.device.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    protected  @BindView(R.id.speakerName)
    TextView speakerName;
    protected  @BindView(R.id.speakerSetUp)
    TextView setUp;
    protected DeviceConnectPresenter devicePresenter;

    public DeviceViewHolder(View itemView, DeviceConnectPresenter devicePresenter) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.devicePresenter = devicePresenter;
    }

    protected  @OnClick(R.id.speakerSetUp) void onClick() {
        String SSID = speakerName.getText().toString();
        devicePresenter.connectToDevice(SSID);
    }

}
