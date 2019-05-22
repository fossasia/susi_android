package org.fossasia.susi.ai.chat.categories.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.categories.MediaCategoryFormat

class MediaCategoryAdapter(private val mediaCategoryList: ArrayList<MediaCategoryFormat>) : RecyclerView.Adapter<MediaCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_media, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mediaCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.message_date.text = mediaCategoryList[p1].date

        val imageUrl = mediaCategoryList[p1].content_url
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_susi)
                .into(holder.message_image)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var message_image: ImageView
        internal var message_date: TextView

        init {
            message_date = itemView.findViewById(R.id.timestamp)
            message_image = itemView.findViewById(R.id.media_image)
        }
    }
}