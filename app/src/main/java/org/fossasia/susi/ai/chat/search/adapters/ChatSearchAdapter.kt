package org.fossasia.susi.ai.chat.search.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.search.SearchDataFormat

class ChatSearchAdapter(private val searchDataList: ArrayList<SearchDataFormat>) : RecyclerView.Adapter<ChatSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat_search, viewGroup, false)
        return ViewHolder(view)
        }

    override fun getItemCount(): Int {
        return searchDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        if (searchDataList[p1].isMine == true) {
            holder.user_message_date.setBackgroundColor(Color.LTGRAY)
            holder.user_message.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.user_message_date.setBackgroundColor(Color.WHITE)
            holder.user_message.setBackgroundColor(Color.WHITE)
        }
        holder.user_message.text = searchDataList[p1].content
        holder.user_message_date.text = searchDataList[p1].date
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var user_message: TextView
        internal var user_message_date: TextView

        init {
            user_message = itemView.findViewById(R.id.search_message)
            user_message_date = itemView.findViewById(R.id.search_message_date)
        }
    }
}
