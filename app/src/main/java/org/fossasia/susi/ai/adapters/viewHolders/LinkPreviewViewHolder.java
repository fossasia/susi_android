package org.fossasia.susi.ai.adapters.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by better_clever on 12/10/16.
 */

public class LinkPreviewViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.text)
    public TextView text;
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

    public LinkPreviewViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

}
