package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.adapters.viewholders.VerticalCellViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meeera on 31/8/17.
 */

public class TableAdapter extends RecyclerView.Adapter<VerticalCellViewHolder> {
    private List<String> data, column;
    private Context context;

    public TableAdapter(List<String> column, List<String> data) {
        this.column = column;
        this.data = data;
    }

    @Override
    public VerticalCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      context = parent.getContext();
        switch (viewType) {
            default: {
                View v1 = LayoutInflater.from(context).inflate(R.layout.horizontal_recyclerview, parent, false);
                return new VerticalCellViewHolder(v1);
            }
        }
    }

    @Override
    public void onBindViewHolder(VerticalCellViewHolder holder, int position) {
        List<String>  horizontaldata = new ArrayList<>();
                if(position == 0){
                    for (int columnflag = 0; columnflag < column.size(); columnflag++) {
                        horizontaldata.add(column.get(columnflag));
                    }
                } else {
                    for (int columnflag = 0; columnflag < column.size(); columnflag++) {
                        horizontaldata.add(data.get(column.size() * (position - 1 )+ columnflag));
                    }
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                holder.mRecyclerView.setLayoutManager(layoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                        layoutManager.getOrientation());
                holder.mRecyclerView.addItemDecoration(dividerItemDecoration);
                HorizontalTableAdapter adapter = new HorizontalTableAdapter(horizontaldata, position);
                holder.mRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return (data.size()/column.size())+1;
    }
}
