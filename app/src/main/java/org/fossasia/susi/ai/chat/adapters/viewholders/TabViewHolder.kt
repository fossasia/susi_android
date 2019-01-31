package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View

import org.fossasia.susi.ai.R

import kotterknife.bindView

class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val view: RecyclerView by bindView(R.id.parentLayout)
}
