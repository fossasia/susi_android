package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by meeera on 2/9/17.
 */

public class HorizontalCellViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.txt)
    public TextView txt;

    public HorizontalCellViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
