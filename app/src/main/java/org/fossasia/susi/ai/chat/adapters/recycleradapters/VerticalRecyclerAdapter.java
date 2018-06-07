package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.adapters.viewholders.VerticalCellViewHolder;
import java.util.List;

// A RecyclerAdapter to inflate all the elements inside each Table type response

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
        holder.column.setText(cols.get(position));
        holder.data.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return cols.size();
    }
}
