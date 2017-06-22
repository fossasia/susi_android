package org.fossasia.susi.ai.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by meeera on 17/6/17.
 */

public class RssViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.link_preview_image)
    public ImageView linkPreviewImageView;
    @BindView(R.id.link_preview_title)
    public TextView linkTitleTextView;
    @BindView(R.id.link_preview_description)
    public TextView linkDescriptionTextView;
    @BindView(R.id.link_preview_layout)
    public LinearLayout linkPreviewLayout;
    @BindView(R.id.link_preview_text_layout)
    public LinearLayout previewTextLayout;
    @BindView(R.id.link)
    public TextView linkTextView;
    @BindView(R.id.title)
    public TextView titleTextView;
    @BindView(R.id.description)
    public TextView descriptionTextView;
    @BindView(R.id.parent_layout)
    public LinearLayout parentLayout;

    public RssViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
