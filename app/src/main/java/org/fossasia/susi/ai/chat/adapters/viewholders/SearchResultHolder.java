package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <h1>Search result view holder</h1>
 *
 * Created by saurabh on 19/11/16.
 */
public class SearchResultHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.link_preview_image)
    public ImageView previewImageView;
    @BindView(R.id.link_preview_title)
    public TextView titleTextView;
    @BindView(R.id.link_preview_description)
    public TextView descriptionTextView;
    @BindView(R.id.preview_layout)
    public LinearLayout previewLayout;
    @BindView(R.id.link_preview_text_layout)
    public LinearLayout previewTextLayout;

    /**
     * Instantiates a new Search result holder.
     *
     * @param itemView the item view
     */
    public SearchResultHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
