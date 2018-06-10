package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.adapters.viewholders.SearchResultHolder;
import org.fossasia.susi.ai.data.model.WebSearchModel;

import java.util.List;

import timber.log.Timber;

/**
 * <h1>Adapter to display horizontal list of web search results.</h1>
 * <p>
 * Created by chiragw15 on 2/6/17.
 */
public class WebSearchAdapter extends RecyclerView.Adapter<SearchResultHolder> {

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

    @NonNull
    @Override
    public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchResultHolder(inflater.inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchResultHolder holder, int position) {
        holder.descriptionTextView.setVisibility(View.GONE);
        holder.titleTextView.setVisibility(View.GONE);
        holder.previewImageView.setVisibility(View.GONE);
        WebSearchModel webSearch = searchResults.get(position);
        if (webSearch != null) {
            String title = webSearch.getHeadline();
            String text = webSearch.getBody();
            String iconUrl = webSearch.getImageURL();
            final String linkurl = webSearch.getUrl();

            if (text != null) {
                holder.descriptionTextView.setText(text);
                holder.descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                holder.descriptionTextView.setVisibility(View.GONE);
            }

            if (title != null) {
                holder.titleTextView.setText(title);
                holder.titleTextView.setVisibility(View.VISIBLE);
            } else {
                holder.titleTextView.setVisibility(View.GONE);
            }

            if (iconUrl != null && !iconUrl.isEmpty()) {
                holder.previewImageView.setVisibility(View.VISIBLE);
                Timber.v(iconUrl);
                Picasso.with(context).load(iconUrl)
                        .into(holder.previewImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Timber.d("image loaded successfully");
                            }

                            @Override
                            public void onError() {
                                holder.previewImageView.setVisibility(View.GONE);
                            }
                        });

            } else {
                holder.previewImageView.setVisibility(View.GONE);
            }

            /*
              redirects to the url of the preview link through chrome custom tabs
             */
            holder.previewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (linkurl != null) {
                        Uri webpage = Uri.parse(linkurl);
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();  //custom tabs intent builder
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(context, webpage); //launching through custom tabs
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

