package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewholders.SearchResultHolder;
import org.fossasia.susi.ai.data.model.WebSearchModel;

import java.util.List;

/**
 * <h1>Adapter to display horizontal list of web search results.</h1>
 *
 * Created by chiragw15 on 2/6/17.
 */
public class WebSearchAdapter extends RecyclerView.Adapter<SearchResultHolder> {

    public static final String TAG = SearchResultsAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private List<WebSearchModel> searchResults;

    /**
     * Instantiates a new Web search adapter.
     *
     * @param context       the context
     * @param searchResults the search results
     */
    public WebSearchAdapter(Context context, List<WebSearchModel> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchResultHolder(inflater.inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final SearchResultHolder holder, int position) {
        holder.descriptionTextView.setVisibility(View.GONE);
        holder.titleTextView.setVisibility(View.GONE);
        holder.previewImageView.setVisibility(View.GONE);
        WebSearchModel webSearch = searchResults.get(position);
        if (webSearch != null) {
            String title = webSearch.getHeadline();
            String text = webSearch.getBody();
            String iconUrl = webSearch.getImageURL();
            final String linkurl = webSearch.getUrl();

            if(text!=null) {
                holder.descriptionTextView.setText(text);
                holder.descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                holder.descriptionTextView.setVisibility(View.GONE);
            }

            if(title!=null) {
                holder.titleTextView.setText(title);
                holder.titleTextView.setVisibility(View.VISIBLE);
            } else {
                holder.titleTextView.setVisibility(View.GONE);
            }

            if (iconUrl != null && !iconUrl.isEmpty()) {
                holder.previewImageView.setVisibility(View.VISIBLE);
                Log.v(TAG , iconUrl);
                Picasso.with(context).load(iconUrl)
                        .into(holder.previewImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Sucess","image loaded successfully");
                            }

                            @Override
                            public void onError() {
                                holder.previewImageView.setVisibility(View.GONE);
                            }
                        });

            } else {
                holder.previewImageView.setVisibility(View.GONE);
            }

            holder.previewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (linkurl != null) {
                        Uri webpage = Uri.parse(linkurl);

                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return searchResults == null ? 0 : searchResults.size();
    }
}

