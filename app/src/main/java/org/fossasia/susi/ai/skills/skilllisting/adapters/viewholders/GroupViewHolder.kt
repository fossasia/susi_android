package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import org.fossasia.susi.ai.R

import kotterknife.bindView

class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val groupName: TextView by bindView(R.id.groupName)
    val skillList: RecyclerView by bindView(R.id.skill_list)
}
