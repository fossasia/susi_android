package org.fossasia.susi.ai.device.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.device.DevicePresenter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.speakerName)
    TextView speakerName;
    @BindView(R.id.speakerSetUp)
    TextView setUp;
    protected DevicePresenter devicePresenter;

    public DeviceViewHolder(View itemView, DevicePresenter devicePresenter) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.devicePresenter = devicePresenter;
    }

    @OnClick(R.id.speakerSetUp) void onClick() {
        String SSID = speakerName.getText().toString();
        devicePresenter.connectToDevice(SSID);
    }

}
