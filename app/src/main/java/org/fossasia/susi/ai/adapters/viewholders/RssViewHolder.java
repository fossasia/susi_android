package org.fossasia.susi.ai.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by meeera on 10/6/17.
 */

public class RssViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.rss_view_title)
    public TextView titleTextView;
    @BindView(R.id.rss_view_description)
    public TextView descriptionTextView;
    @BindView(R.id.rss_view_layout)
    public LinearLayout previewLayout;

    public RssViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

