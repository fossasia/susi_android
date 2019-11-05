package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotterknife.bindView
import org.fossasia.susi.ai.R

/**
 * ViewHolder for drawing skill group item layout.
 */
class SkillGroupViewHolder(
    itemView: View,
    private val adapterOffset: Int,
    private val listener: ClickListener?
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val groupParent: LinearLayout by bindView(R.id.group_parent)
    val groupName: TextView by bindView(R.id.group)
    val arrowIcon: ImageView by bindView(R.id.ic_arrow)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        listener?.onItemClicked(adapterPosition - adapterOffset)
    }

    interface ClickListener {
        fun onItemClicked(position: Int)
    }
}
