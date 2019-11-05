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

class SelectionDialogListAdapter(
    context: Context,
    private val list: List<Pair<String, Drawable>>
) :
    ArrayAdapter<Pair<String, Drawable>>(context, R.layout.item_selection_dialog_list, list) {

    internal class ViewHolder {
        var option: TextView? = null
        var icon: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view: View? = null

        if (convertView == null) {
            val context = this.context
            if (context is Activity) view = context.layoutInflater.inflate(R.layout.item_selection_dialog_list, null)
            val viewHolder = ViewHolder()
            val textView = view?.findViewById<TextView>(R.id.option)
            viewHolder.option = textView
            val imageView = view?.findViewById<ImageView>(R.id.icon)
            viewHolder.icon = imageView
            view?.tag = viewHolder
        } else {
            view = convertView
        }

        val holder = view?.tag
        if (holder is ViewHolder) {
            holder.option?.text = list[position].first
            holder.icon?.setImageDrawable(list[position].second)
        }
        return view
    }
}
