package org.fossasia.susi.ai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 * Created by saurabh on 7/10/16.
 */

public class MapViewHolder extends MessageViewHolder {
    @BindView(R.id.text)
    public TextView text;
    @BindView(R.id.timestamp)
    public TextView timestampTextView;
    @BindView(R.id.map_image)
    public ImageView mapImage;
    @BindView(R.id.chatMessageView)
    public ChatMessageView chatMessageView;
    @BindView(R.id.location_pointer)
    public  ImageView pointer;


    public MapViewHolder(View itemView , ClickListener listener) {
        super(itemView , listener);
        ButterKnife.bind(this, itemView);
    }
}
