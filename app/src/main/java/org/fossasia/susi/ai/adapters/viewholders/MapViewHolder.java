package org.fossasia.susi.ai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <h1>Map view holder</h1>
 *
 * Created by saurabh on 7/10/16.
 */
public class MapViewHolder extends MessageViewHolder {

    @BindView(R.id.map_image)
    public ImageView mapImage;
    @BindView(R.id.location_pointer)
    public  ImageView pointer;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;

    /**
     * Instantiates a new Map view holder.
     *
     * @param itemView the item view
     * @param listener the listener
     */
    public MapViewHolder(View itemView , ClickListener listener) {
        super(itemView , listener);
        ButterKnife.bind(this, itemView);
    }
}
