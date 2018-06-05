package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

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

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return (data.size()/column.size())+1;
    }
}