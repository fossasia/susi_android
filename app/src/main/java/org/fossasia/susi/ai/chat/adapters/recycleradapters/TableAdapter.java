package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.adapters.viewholders.TabViewHolder;

import java.util.ArrayList;
import java.util.List;

/*
*   A RecyclerAdapter to inflate the list of the table responses
* */

public class TableAdapter extends RecyclerView.Adapter<TabViewHolder> {
    private List<String> data, column;
    private Context context;

    public TableAdapter(List<String> column, List<String> data) {
        this.column = column;
        this.data = data;
    }

    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.table_item, parent, false);
        return new TabViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
        List<String> smallData = new ArrayList<>();
        for (int columnflag = 0; columnflag < column.size(); columnflag++) {
            smallData.add(data.get(column.size() * position + columnflag));
        }

        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.view.setLayoutManager(manager);
        VerticalRecyclerAdapter verticalRecyclerAdapter = new VerticalRecyclerAdapter(context, column, smallData);
        holder.view.setAdapter(verticalRecyclerAdapter);
    }

    @Override
    public int getItemCount() {
        return data.size() / column.size();
    }
}