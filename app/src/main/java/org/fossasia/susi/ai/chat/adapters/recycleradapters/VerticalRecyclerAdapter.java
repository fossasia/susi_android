package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.content.Context;
import android.support.customtabs.CustomTabsIntent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
    private Context context;

    public VerticalRecyclerAdapter(Context context, List<String> cols, List<String> data) {
        this.cols = cols;
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public VerticalCellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_recylcer_item, parent, false);
        return new VerticalCellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalCellViewHolder holder, int position) {
        final String info = data.get(position);
        holder.column.setText(cols.get(position));
        holder.linkData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(info));
            }
        });
        if (Patterns.WEB_URL.matcher(info).matches()) {
            Timber.d(info);
            holder.linkData.setVisibility(View.VISIBLE);
            holder.data.setVisibility(View.GONE);
            holder.linkData.setText(Html.fromHtml("<a href=" + info + ">" + info + "</a>"));
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
