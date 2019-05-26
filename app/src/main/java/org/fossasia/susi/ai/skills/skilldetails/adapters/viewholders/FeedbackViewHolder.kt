package org.fossasia.susi.ai.skills.skilldetails.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.ButterKnife
import kotterknife.bindView
import org.fossasia.susi.ai.R

/**
 * ViewHolder for drawing feed back item layout.
 */
class FeedbackViewHolder(
    itemView: View,
    private val listener: FeedbackViewHolder.ClickListener?
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val itemFeedback: LinearLayout by bindView(R.id.itemFeedback)
    val avatar: ImageView by bindView(R.id.avatar)
    val feedbackEmail: TextView by bindView(R.id.tvEmail)
    val feedbackDate: TextView by bindView(R.id.tvDate)
    val feedback: TextView by bindView(R.id.tvFeedback)
    val seeAllReviews: LinearLayout by bindView(R.id.seeAllReviews)

    init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (listener != null) {
            val position = adapterPosition
            if (position == 3) {
                listener.onItemClicked(position)
            }
        }
    }

    interface ClickListener {
        fun onItemClicked(position: Int)
    }
}
