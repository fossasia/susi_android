package org.fossasia.susi.ai.device.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.speakerName)
    TextView speakerName;
    @BindView(R.id.speakerSetUp)
    TextView setUp;

    public DeviceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }


}
