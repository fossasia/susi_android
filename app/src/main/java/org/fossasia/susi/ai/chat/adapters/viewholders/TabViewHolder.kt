package org.fossasia.susi.ai.chat.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotterknife.bindView
import org.fossasia.susi.ai.R

class TabViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val view: androidx.recyclerview.widget.RecyclerView by bindView(R.id.parentLayout)
}
