package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.adapters.viewholders.RssViewHolder;
import org.fossasia.susi.ai.rest.responses.susi.Datum;

import java.util.List;

/**
 * <h1>Adapter to display horizontal list of RSS results.</h1>
 * <p>
 * Created by saurabh on 19/11/16.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<RssViewHolder> {
    private LayoutInflater inflater;
    private List<Datum> datumList;

    /**
     * Instantiates a new Search results adapter.
     *
     * @param context   the context
     * @param datumList the datum list
     */
    public SearchResultsAdapter(Context context, List<Datum> datumList) {
        this.datumList = datumList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RssViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RssViewHolder(inflater.inflate(R.layout.rss_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RssViewHolder holder, int position) {
        Datum datum = datumList.get(position);
        if (datum != null) {
            if (!TextUtils.isEmpty(datum.getLink())) {
                if (!TextUtils.isEmpty(datum.getTitle())) {
                    holder.titleTextView.setText(Html.fromHtml(datum.getTitle()));
                }
                if (!TextUtils.isEmpty(datum.getDescription())) {
                    holder.descriptionTextView.setText(Html.fromHtml(datum.getDescription()));
                }
                holder.linkTextView.setText(datum.getLink());
            }
        }
    }

    @Override
    public int getItemCount() {
        return datumList == null ? 0 : datumList.size();
    }
}
