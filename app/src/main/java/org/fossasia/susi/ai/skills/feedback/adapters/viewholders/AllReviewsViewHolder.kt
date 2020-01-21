package org.fossasia.susi.ai.skills.feedback.adapters.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotterknife.bindView
import org.fossasia.susi.ai.R

class AllReviewsViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val itemFeedback: LinearLayout by bindView(R.id.itemFeedback)
    val avatar: ImageView by bindView(R.id.avatar)
    val feedbackEmail: TextView by bindView(R.id.tvEmail)
    val feedbackDate: TextView by bindView(R.id.tvDate)
    val feedback: TextView by bindView(R.id.tvFeedback)
}
