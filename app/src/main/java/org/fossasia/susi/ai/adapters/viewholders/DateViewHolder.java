package org.fossasia.susi.ai.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <h1>Date view holder</h1>
 *
 * Created by chiragw15 on 11/12/16.
 */
public class DateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.date)
    public TextView textDate;

    /**
     * Instantiates a new Date view holder.
     *
     * @param itemView the item view
     */
    public DateViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
