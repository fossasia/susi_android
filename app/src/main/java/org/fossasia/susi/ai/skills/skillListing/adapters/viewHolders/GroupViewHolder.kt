package org.fossasia.susi.ai.skills.skillListing.adapters.viewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.fossasia.susi.ai.R

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.groupName)
    var groupName: TextView? = null
    @BindView(R.id.skill_list)
    var skillList: RecyclerView? = null

    init {
        ButterKnife.bind(this, itemView)
    }
}