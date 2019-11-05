package org.fossasia.susi.ai.chat.adapters.recycleradapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.viewholders.RssViewHolder
import org.fossasia.susi.ai.rest.responses.susi.Datum

/**
 * <h1>Adapter to display horizontal list of RSS results.</h1>
 */

class SearchResultsAdapter(context: Context, private val datumList: List<Datum?>?) :
    RecyclerView.Adapter<RssViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RssViewHolder {
        return RssViewHolder(inflater.inflate(R.layout.rss_item, parent, false))
    }

    override fun onBindViewHolder(holder: RssViewHolder, position: Int) {
        val datum = datumList?.get(position)
        if (datum != null) {
            if (!TextUtils.isEmpty(datum.link)) {
                if (!TextUtils.isEmpty(datum.title)) {
                    holder.titleTextView.text = Html.fromHtml(datum.title)
                }
                if (!TextUtils.isEmpty(datum.description)) {
                    holder.descriptionTextView.text = Html.fromHtml(datum.description)
                }
                holder.linkTextView.text = datum.link
            }
        }
    }

    override fun getItemCount(): Int {
        return datumList?.size ?: 0
    }
}
