package org.fossasia.susi.ai.adapters.viewholders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayank on 11-12-2016.
 */

public class WebSearchHolder extends MessageViewHolder{

    @BindView(R.id.text)
    public TextView text;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;
    @BindView(R.id.web_link_preview_image)
    public ImageView previewImageView;
    @BindView(R.id.web_link_preview_title)
    public TextView titleTextView;
    @BindView(R.id.web_link_preview_description)
    public TextView descriptionTextView;
    @BindView(R.id.timestamp)
    public TextView timestampTextView;
    @BindView(R.id.web_preview_layout)
    public LinearLayout previewLayout;
    @Nullable
    @BindView(R.id.received_tick)
    public ImageView receivedTick;
    @BindView(R.id.message_star)
    public ImageView messageStar;

    public WebSearchHolder(View itemView , MessageViewHolder.ClickListener listener) {
        super(itemView, listener);
        ButterKnife.bind(this,itemView);
    }
}
