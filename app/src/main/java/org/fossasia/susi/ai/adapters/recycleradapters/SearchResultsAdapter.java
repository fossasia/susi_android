package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewholders.RssViewHolder;
import org.fossasia.susi.ai.rest.responses.susi.Datum;

import java.util.List;

/**
 * <h1>Adapter to display horizontal list of RSS results.</h1>
 *
 * Created by saurabh on 19/11/16.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<RssViewHolder> {
    public static final String TAG = SearchResultsAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private List<Datum> datumList;
    private Context context;
    /**
     * Instantiates a new Search results adapter.
     *
     * @param context   the context
     * @param datumList the datum list
     */
    public SearchResultsAdapter(Context context, List<Datum> datumList) {
        this.datumList = datumList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RssViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RssViewHolder(inflater.inflate(R.layout.rss_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RssViewHolder holder, int position) {
        final Datum datum = datumList.get(position);
        if (datum != null) {
            if( datum.getTitle() == null || datum.getTitle().isEmpty()) {
                holder.titleTextView.setVisibility(View.GONE);
            } else {
                holder.titleTextView.setVisibility(View.VISIBLE);
                holder.titleTextView.setText(Html.fromHtml(datum.getTitle()));
            }
            if(datum.getDescription() == null || datum.getDescription().isEmpty()) {
                holder.descriptionTextView.setVisibility(View.GONE);
            } else {
                holder.descriptionTextView.setVisibility(View.VISIBLE);
                holder.descriptionTextView.setText(Html.fromHtml(datum.getDescription()));
            }
            holder.linkTextView.setText(datum.getLink());
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri webpage = Uri.parse(datum.getLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                }
            });

        } else {
            holder.titleTextView.setText(null);
            holder.descriptionTextView.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        return datumList == null ? 0 : datumList.size();
    }
}
