package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.adapters.viewholders.VerticalCellViewHolder;

import java.util.List;

import timber.log.Timber;

/*
 A RecyclerAdapter to inflate all the elements inside each Table type response
  */

public class VerticalRecyclerAdapter extends RecyclerView.Adapter<VerticalCellViewHolder> {

    private List<String> cols;
    private List<String> data;

    public VerticalRecyclerAdapter(List<String> cols, List<String> data) {
        this.cols = cols;
        this.data = data;
    }

    @NonNull
    @Override
    public VerticalCellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_recylcer_item, parent, false);
        return new VerticalCellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalCellViewHolder holder, int position) {
        String info = data.get(position);
        holder.column.setText(cols.get(position));
        if (Patterns.WEB_URL.matcher(info).matches()) {
            Timber.d(info);
            holder.linkData.setVisibility(View.VISIBLE);
            holder.data.setVisibility(View.GONE);
            holder.linkData.setText(info);
        } else {
            holder.data.setVisibility(View.VISIBLE);
            holder.linkData.setVisibility(View.GONE);
            holder.data.setText(data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return cols.size();
    }
}
