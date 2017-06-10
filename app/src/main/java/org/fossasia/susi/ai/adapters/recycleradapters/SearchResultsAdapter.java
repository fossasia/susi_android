package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewholders.RssViewHolder;
import org.fossasia.susi.ai.rest.responses.susi.Datum;

import java.util.List;

/**
 * Created by saurabh on 19/11/16.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<RssViewHolder> {
    public static final String TAG = SearchResultsAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private List<Datum> datumList;

    public SearchResultsAdapter(Context context, List<Datum> datumList) {
        this.context = context;
        this.datumList = datumList;
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
            holder.titleTextView.setText(Html.fromHtml(datum.getTitle()));
            holder.descriptionTextView.setText(Html.fromHtml(datum.getDescription()));
            holder.previewLayout.setOnClickListener(new View.OnClickListener() {
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
