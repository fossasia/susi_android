package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class MessageViewHolder(itemView: View, private val listener: ClickListener?)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    override fun onClick(view: View) {
        listener?.onItemClicked(adapterPosition)
    }

    override fun onLongClick(view: View): Boolean {
        return listener != null && listener.onItemLongClicked(adapterPosition)
    }

    /**
     * The interface Click listener.
     */
    interface ClickListener {
        /**
         * On item clicked.
         *
         * @param position the position
         */
        fun onItemClicked(position: Int)

        /**
         * On item long clicked boolean.
         *
         * @param position the position
         * @return the boolean
         */
        fun onItemLongClicked(position: Int): Boolean
    }
}
