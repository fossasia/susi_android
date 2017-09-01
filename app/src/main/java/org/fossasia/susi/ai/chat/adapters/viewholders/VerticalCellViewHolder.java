package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by meeera on 2/9/17.
 */

public class VerticalCellViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.recyclerView)
    public RecyclerView mRecyclerView;

    public VerticalCellViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
