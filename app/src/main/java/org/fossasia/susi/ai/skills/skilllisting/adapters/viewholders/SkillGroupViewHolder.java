package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by arundhati24 on 12/07/2018.
 */
public class SkillGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.group_parent)
    public LinearLayout groupParent;
    @BindView(R.id.group)
    public TextView groupName;
    @BindView(R.id.ic_arrow)
    public ImageView arrowIcon;

    private ClickListener listener;
    private int adapterOffset;

    public SkillGroupViewHolder(View itemView, int adapterOffset, ClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listener = clickListener;
        itemView.setOnClickListener(this);
        this.adapterOffset = adapterOffset;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onItemClicked(getAdapterPosition() - adapterOffset);
        }
    }

    public interface ClickListener {
        void onItemClicked(int position);
    }
}