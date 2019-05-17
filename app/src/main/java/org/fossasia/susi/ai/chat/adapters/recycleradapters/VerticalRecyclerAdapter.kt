package org.fossasia.susi.ai.chat.adapters.recycleradapters

import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.viewholders.VerticalCellViewHolder
import timber.log.Timber

/**
 * A RecyclerAdapter to inflate all the elements inside each Table type response
 */
class VerticalRecyclerAdapter(
    private val context: Context,
    private val cols: List<String?>,
    private val data: List<String?>
) :
    RecyclerView.Adapter<VerticalCellViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalCellViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_recylcer_item, parent, false)
        return VerticalCellViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerticalCellViewHolder, position: Int) {
        val info = data[position]
        holder.column.text = cols[position]
        holder.linkData.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(info))
        }
        if (Patterns.WEB_URL.matcher(info).matches()) {
            Timber.d(info)
            holder.linkData.visibility = View.VISIBLE
            holder.data.visibility = View.GONE
            holder.linkData.text = Html.fromHtml("<a href=$info>$info</a>")
        } else {
            holder.data.visibility = View.VISIBLE
            holder.linkData.visibility = View.GONE
            holder.data.text = data[position]
        }
    }

    override fun getItemCount(): Int {
        return cols.size
    }
}
