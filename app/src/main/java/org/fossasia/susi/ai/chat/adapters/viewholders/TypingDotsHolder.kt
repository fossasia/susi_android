package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout

import org.fossasia.susi.ai.R

import kotterknife.bindView
import pl.tajchert.waitingdots.DotsTextView

class TypingDotsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val dotsTextView: DotsTextView by bindView(R.id.dots)
    val backgroundLayout: LinearLayout by bindView(R.id.background_layout)
}
