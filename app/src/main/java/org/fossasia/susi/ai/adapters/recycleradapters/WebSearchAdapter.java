package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewholders.SearchResultHolder;
import org.fossasia.susi.ai.model.WebSearchModel;

import java.util.List;

/**
 * Created by chiragw15 on 2/6/17.
 */

public class WebSearchAdapter extends RecyclerView.Adapter<SearchResultHolder> {
    public static final String TAG = SearchResultsAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private List<WebSearchModel> searchResults;

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

            if (iconUrl != null) {
                holder.previewImageView.setVisibility(View.VISIBLE);
                Log.v(TAG , iconUrl);

                Glide.with(context).load(iconUrl).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.previewImageView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                    {
                        return false;
                    }
                }).into(holder.previewImageView);

            }else {
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

