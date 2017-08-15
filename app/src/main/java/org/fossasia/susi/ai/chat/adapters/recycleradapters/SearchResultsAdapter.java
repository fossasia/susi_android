package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.adapters.viewholders.RssViewHolder;
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

    @Override
    public RssViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RssViewHolder(inflater.inflate(R.layout.rss_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RssViewHolder holder, int position) {
        Datum datum = datumList.get(position);
        if (datum != null) {
            if( datum.getTitle() != null && !datum.getTitle().isEmpty()) {
                holder.titleTextView.setText(Html.fromHtml(datum.getTitle()));
            }
            if(datum.getDescription() != null && !datum.getDescription().isEmpty()) {
                holder.descriptionTextView.setText(Html.fromHtml(datum.getDescription()));
            }
            holder.linkTextView.setText(datum.getLink());
        }
    }

    @Override
    public int getItemCount() {
        return datumList == null ? 0 : datumList.size();
    }
}
