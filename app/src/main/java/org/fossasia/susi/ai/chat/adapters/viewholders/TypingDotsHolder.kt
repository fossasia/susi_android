package org.fossasia.susi.ai.chat.adapters.viewholders

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotterknife.bindView
import org.fossasia.susi.ai.R
import pl.tajchert.waitingdots.DotsTextView

class TypingDotsHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val dotsTextView: DotsTextView by bindView(R.id.dots)
    val backgroundLayout: LinearLayout by bindView(R.id.background_layout)
}
