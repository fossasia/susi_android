package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import org.fossasia.susi.ai.R

import kotterknife.bindView

class VerticalCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val column: TextView by bindView(R.id.column)
    val data: TextView by bindView(R.id.data_item)
    val linkData: TextView by bindView(R.id.data_item_link)
}
