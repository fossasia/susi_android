package org.fossasia.susi.ai.chat.search.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.categories.MapCategoryFormat

class MapCategoryAdapter(private val mapCategoryList: ArrayList<MapCategoryFormat>) : RecyclerView.Adapter<MapCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_susi_map, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mapCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        // Inflate the map
        val imageUrl = mapCategoryList[p1].url
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_susi)
                .into(holder.map)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var map: ImageView

        init {
            map = itemView.findViewById(R.id.map_image)
        }
    }
}