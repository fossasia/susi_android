package org.fossasia.susi.ai.chat.adapters.recycleradapters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Pair
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import org.fossasia.susi.ai.R

/**
 * Adapter for displaying list of actions in a dialog box when message is long pressed.
 *
 *
 * Created by chiragw15 on 31/7/17.
 */

class SelectionDialogListAdapter(context: Context, private val list: List<Pair<String, Drawable>>) : ArrayAdapter<Pair<String, Drawable>>(context, R.layout.item_selection_dialog_list, list) {
    private val context: Activity = context as Activity

    internal class ViewHolder {
        var option: TextView? = null
        var icon: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View

        if (convertView == null) {
            val inflator = context.layoutInflater
            view = inflator.inflate(R.layout.item_selection_dialog_list, null)
            val viewHolder = ViewHolder()
            viewHolder.option = view.findViewById<View>(R.id.option) as TextView
            viewHolder.icon = view.findViewById<View>(R.id.icon) as ImageView
            view.tag = viewHolder
        } else {
            view = convertView
        }

        val holder = view.tag as ViewHolder
        holder.option?.text = list[position].first
        holder.icon?.setImageDrawable(list[position].second)
        return view
    }
}
