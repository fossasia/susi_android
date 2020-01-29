package org.fossasia.susi.ai.skills.groupwiseskills.adapters.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotterknife.bindView
import org.fossasia.susi.ai.R

/**
 * ViewHolder for drawing skill item layout.
 */
class SkillViewHolder(
    itemView: View,
    private val listener: ClickListener?
) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val skillImage: ImageView by bindView(R.id.skill_image)
    val skillName: TextView by bindView(R.id.skill_name)
    val skillAuthorName: TextView by bindView(R.id.skill_author_name)
    val skillExample: TextView by bindView(R.id.skill_example)
    val skillRating: RatingBar by bindView(R.id.skill_rating)
    val skillTotalRatings: TextView by bindView(R.id.skill_total_ratings)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        listener?.onItemClicked(adapterPosition)
    }

    interface ClickListener {
        fun onItemClicked(position: Int)
    }
}
