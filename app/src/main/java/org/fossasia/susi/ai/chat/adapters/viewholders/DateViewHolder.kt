package org.fossasia.susi.ai.chat.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotterknife.bindView
import org.fossasia.susi.ai.R

class DateViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val textDate: TextView by bindView(R.id.date)
}
