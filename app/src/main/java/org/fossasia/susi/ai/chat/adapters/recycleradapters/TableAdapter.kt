package org.fossasia.susi.ai.chat.adapters.recycleradapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.ArrayList
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.viewholders.TabViewHolder

/*
*   A RecyclerAdapter to inflate the list of the table responses
* */

class TableAdapter(
    private val column: List<String?>,
    private val data: List<String?>
) :
    RecyclerView.Adapter<TabViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        context = parent.context
        val tableItem = LayoutInflater.from(context).inflate(R.layout.table_item, parent, false)
        return TabViewHolder(tableItem)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        val smallData = ArrayList<String?>()
        for (columnFlag in column.indices) {
            smallData.add(data[column.size * position + columnFlag])
        }

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.view.layoutManager = manager
        val verticalRecyclerAdapter = VerticalRecyclerAdapter(context, column, smallData)
        holder.view.adapter = verticalRecyclerAdapter
    }

    override fun getItemCount(): Int {
        return data.size / column.size
    }
}
