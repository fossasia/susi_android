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

import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.squareup.picasso.Picasso;

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
    private Context context;
    private List<Datum> datumList;

    /**
     * Instantiates a new Search results adapter.
     *
     * @param context   the context
     * @param datumList the datum list
     */
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
        Datum datum = datumList.get(position);
        if (datum != null) {
            if( datum.getTitle() == null || datum.getTitle().isEmpty()) {
                holder.titleTextView.setVisibility(View.GONE);
            } else {
                holder.titleTextView.setVisibility(View.VISIBLE);
                holder.titleTextView.setText(Html.fromHtml(datum.getTitle()));
            }
            if(datum.getDescription().isEmpty()) {
                holder.descriptionTextView.setVisibility(View.GONE);
            } else {
                holder.descriptionTextView.setVisibility(View.VISIBLE);
                holder.descriptionTextView.setText(Html.fromHtml(datum.getDescription()));
            }
            holder.linkTextView.setText(datum.getLink());
            if (!TextUtils.isEmpty(datum.getLink())) {
                LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        holder.linkPreviewImageView.setVisibility(View.GONE);
                        holder.linkDescriptionTextView.setVisibility(View.GONE);
                        holder.linkTitleTextView.setVisibility(View.GONE);
                        holder.linkPreviewLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPos(final SourceContent sourceContent, boolean b) {
                        holder.linkPreviewImageView.setVisibility(View.VISIBLE);
                        holder.linkDescriptionTextView.setVisibility(View.VISIBLE);
                        holder.linkTitleTextView.setVisibility(View.VISIBLE);
                        holder.linkPreviewLayout.setVisibility(View.VISIBLE);
                        holder.linkTitleTextView.setText(sourceContent.getTitle());
                        holder.linkDescriptionTextView.setText(sourceContent.getDescription());
                        final List<String> imageList = sourceContent.getImages();
                        if (imageList == null || imageList.size() == 0) {
                            holder.linkPreviewImageView.setVisibility(View.GONE);
                        } else {
                            Picasso.with(context).load(imageList.get(0))
                                    .fit().centerCrop()
                                    .into(holder.linkPreviewImageView);
                        }

                        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri webpage = Uri.parse(sourceContent.getFinalUrl());
                                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                if (intent.resolveActivity(context.getPackageManager()) != null) {
                                    context.startActivity(intent);
                                }
                            }
                        });

                    }
                };
                TextCrawler textCrawler = new TextCrawler();
                textCrawler.makePreview(linkPreviewCallback, datum.getLink());
            }
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
