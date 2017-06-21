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
 * <h1>Link preview view holder</h1>
 *
 * Created by better_clever on 12/10/16.
 */
public class LinkPreviewViewHolder extends MessageViewHolder{

    @BindView(R.id.text)
    public TextView text;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;
    @BindView(R.id.link_preview_image)
    public ImageView previewImageView;
    @BindView(R.id.link_preview_title)
    public TextView titleTextView;
    @BindView(R.id.link_preview_description)
    public TextView descriptionTextView;
    @BindView(R.id.timestamp)
    public TextView timestampTextView;
    @BindView(R.id.preview_layout)
    public LinearLayout previewLayout;
    @Nullable @BindView(R.id.received_tick)
    public ImageView receivedTick;

    /**
     * Instantiates a new Link preview view holder.
     *
     * @param itemView the item view
     * @param listener the listener
     */
    public LinkPreviewViewHolder(View itemView , ClickListener listener) {
        super(itemView, listener);
        ButterKnife.bind(this,itemView);
    }

}
