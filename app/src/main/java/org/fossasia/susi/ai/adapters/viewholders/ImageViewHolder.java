package org.fossasia.susi.ai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayanktripathi on 22/06/17.
 */

public class ImageViewHolder extends MessageViewHolder {

    @BindView(R.id.susi_image)
    public ImageView susiImage;

    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;

    @BindView(R.id.timestamp)
    public TextView timestampTextView;

    /**
     * Instantiates a new Message view holder.
     *
     * @param itemView      the item view
     * @param clickListener the click listener
     */

    public ImageViewHolder(View itemView, ClickListener clickListener) {
        super(itemView, clickListener);
        ButterKnife.bind(this, itemView);
    }
}
