package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.parentLayout)
    public LinearLayout linearLayout;

    public TabViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
