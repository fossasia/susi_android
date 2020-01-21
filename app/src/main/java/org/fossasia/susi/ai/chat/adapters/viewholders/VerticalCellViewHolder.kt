package org.fossasia.susi.ai.chat.adapters.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotterknife.bindView
import org.fossasia.susi.ai.R

class VerticalCellViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val column: TextView by bindView(R.id.column)
    val data: TextView by bindView(R.id.data_item)
    val linkData: TextView by bindView(R.id.data_item_link)
}
