package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
    androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

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
