package org.fossasia.susi.ai.adapters.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saurabh on 7/10/16.
 */

public class MapViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text)
    public TextView text;
    @BindView(R.id.map_image)
    public ImageView mapImage;

    public MapViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
