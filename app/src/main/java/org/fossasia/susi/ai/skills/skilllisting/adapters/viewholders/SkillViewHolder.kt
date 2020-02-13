package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_skill_details.*
import kotterknife.bindView
import org.fossasia.susi.ai.R

class SkillViewHolder(itemView: View, private val listener: ClickListener?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val previewImageView: ImageView by bindView(R.id.skill_preview_image)
    val skillPreviewTitle: TextView by bindView(R.id.skill_preview_title)
    val skillPreviewExample: TextView by bindView(R.id.skill_preview_example)
    val skillRatingBar: RatingBar by bindView(R.id.cv_rating_bar)
    val totalRatings: TextView by bindView(R.id.cv_total_ratings)
    val skillItemTryButton: Button by bindView(R.id.skillItemTryButton)

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
