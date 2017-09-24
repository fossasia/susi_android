package org.fossasia.susi.ai.skills.skilldetails.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by chiragw15 on 27/8/17.
 */

public class SkillExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.text)
    public TextView example;

    private SkillExampleViewHolder.ClickListener listener;

    public SkillExampleViewHolder(View itemView, SkillExampleViewHolder.ClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listener = clickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onItemClicked(getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onItemClicked(int position);
    }
}
