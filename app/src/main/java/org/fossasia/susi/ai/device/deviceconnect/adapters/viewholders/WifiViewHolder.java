package org.fossasia.susi.ai.device.deviceconnect.adapters.viewholders;

import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.data.UtilModel;
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiViewHolder extends RecyclerView.ViewHolder {

    public @BindView(R.id.wifi_name)
    TextView wifiName;
    public @BindView(R.id.layout_wifi)
    LinearLayout layout;

    protected DeviceConnectPresenter devicePresenter;

    public WifiViewHolder(View itemView, DeviceConnectPresenter devicePresenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.devicePresenter = devicePresenter;
    }

    public @OnClick(R.id.layout_wifi)
    void onClick() {
        UtilModel utilModel;
        utilModel = new UtilModel(itemView.getContext());
        final View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.get_password, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
        alertDialog.setTitle(utilModel.getString(R.string.enter_password) + wifiName.getText().toString());
        alertDialog.setCancelable(false);

        final EditText password = view.findViewById(R.id.edt_pass);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                devicePresenter.makeWifiRequest(wifiName.getText().toString(), password.getText().toString());
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();
    }
}
