package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
*  A view for the individual item of the recyclerlist
* */

public class TabViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.parentLayout)
    public RecyclerView view;

    public TabViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
