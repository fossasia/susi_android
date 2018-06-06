package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerticalCellViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.column)
    public TextView column;
    @BindView(R.id.data_item)
    public TextView data;

    public VerticalCellViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
