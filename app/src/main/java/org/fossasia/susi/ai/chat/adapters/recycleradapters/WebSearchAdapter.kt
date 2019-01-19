package org.fossasia.susi.ai.chat.adapters.recycleradapters

import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.squareup.picasso.Picasso

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.viewholders.SearchResultHolder
import org.fossasia.susi.ai.data.model.WebSearchModel

import timber.log.Timber
import java.lang.Exception

/**
 * <h1>Adapter to display horizontal list of web search results.</h1>\
 */
class WebSearchAdapter(private val context: Context, private val searchResults: List<WebSearchModel?>?) : RecyclerView.Adapter<SearchResultHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultHolder {
        return SearchResultHolder(inflater.inflate(R.layout.search_item, parent, false))
    }

    override fun onBindViewHolder(holder: SearchResultHolder, position: Int) {
        holder.descriptionTextView.visibility = View.GONE
        holder.titleTextView.visibility = View.GONE
        holder.previewImageView.visibility = View.GONE
        val webSearch = searchResults?.get(position)
        if (webSearch != null) {
            val title = webSearch.headline
            val text = webSearch.body
            val iconUrl = webSearch.imageURL
            val linkurl = webSearch.url

            if (text != null) {
                holder.descriptionTextView.text = text
                holder.descriptionTextView.visibility = View.VISIBLE
            } else {
                holder.descriptionTextView.visibility = View.GONE
            }

            if (title != null) {
                holder.titleTextView.text = title
                holder.titleTextView.visibility = View.VISIBLE
            } else {
                holder.titleTextView.visibility = View.GONE
            }

            if (iconUrl != null && !iconUrl.isEmpty()) {
                holder.previewImageView.visibility = View.VISIBLE
                Timber.v(iconUrl)
                Picasso.get()
                        .load(iconUrl)
                        .into(holder.previewImageView, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                Timber.d("image loaded successfully")
                            }

                            override fun onError(exception: Exception) {
                                holder.previewImageView.visibility = View.GONE
                            }
                        })
            } else {
                holder.previewImageView.visibility = View.GONE
            }

            /*
              redirects to the url of the preview link through chrome custom tabs
             */
            holder.previewLayout.setOnClickListener {
                if (linkurl != null) {
                    val webpage = Uri.parse(linkurl)
                    val builder = CustomTabsIntent.Builder() //custom tabs intent builder
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(context, webpage) //launching through custom tabs
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return searchResults?.size ?: 0
    }
}
