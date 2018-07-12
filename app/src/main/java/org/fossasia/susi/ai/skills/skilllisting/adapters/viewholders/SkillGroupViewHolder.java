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
public class SkillGroupViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.group_parent)
    public LinearLayout groupParent;
    @BindView(R.id.group)
    public TextView groupName;
    @BindView(R.id.ic_arrow)
    public ImageView arrowIcon;

    public SkillGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}