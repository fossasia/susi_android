package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotterknife.bindView
import org.fossasia.susi.ai.R

class GroupViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val groupName: TextView by bindView(R.id.groupName)
    val skillList: androidx.recyclerview.widget.RecyclerView by bindView(R.id.skill_list)
}
