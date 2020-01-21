package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotterknife.bindView
import org.fossasia.susi.ai.R

class SkillViewHolder(itemView: View, private val listener: ClickListener?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val previewImageView: ImageView by bindView(R.id.skill_preview_image)
    val skillPreviewTitle: TextView by bindView(R.id.skill_preview_title)
    val skillPreviewExample: TextView by bindView(R.id.skill_preview_example)
    val skillRatingBar: RatingBar by bindView(R.id.cv_rating_bar)
    val totalRatings: TextView by bindView(R.id.cv_total_ratings)

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
