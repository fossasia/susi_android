package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by chiragw15 on 18/8/17.
 */

public class GroupViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.groupName)
    public TextView groupName;
    @BindView(R.id.skill_list)
    public RecyclerView skillList;

    public GroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
