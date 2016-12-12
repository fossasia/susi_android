package org.fossasia.susi.ai.adapters.viewholders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 * Created by mayank on 11-12-2016.
 */

public class WebSearchHolder extends MessageViewHolder{

    @BindView(R.id.web_text)
    public TextView text;
    @BindView(R.id.chatMessageView)
    public ChatMessageView chatMessageView;
    @BindView(R.id.web_link_preview_image)
    public ImageView previewImageView;
    @BindView(R.id.web_link_preview_title)
    public TextView titleTextView;
    @BindView(R.id.web_link_preview_description)
    public TextView descriptionTextView;
    @BindView(R.id.web_timestamp)
    public TextView timestampTextView;
    @BindView(R.id.web_preview_layout)
    public LinearLayout previewLayout;
    @Nullable
    @BindView(R.id.received_tick)
    public ImageView receivedTick;

    public WebSearchHolder(View itemView , MessageViewHolder.ClickListener listener) {
        super(itemView, listener);
        ButterKnife.bind(this,itemView);
    }

}
