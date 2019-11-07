package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotterknife.bindView
import org.fossasia.susi.ai.R

class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val groupName: TextView by bindView(R.id.groupName)
    val skillList: RecyclerView by bindView(R.id.skill_list)
}
