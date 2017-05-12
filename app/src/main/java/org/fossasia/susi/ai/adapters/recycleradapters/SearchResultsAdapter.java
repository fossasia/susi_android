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

import com.bumptech.glide.Glide;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewholders.SearchResultHolder;
import org.fossasia.susi.ai.rest.model.Datum;

import java.util.List;

/**
 * Created by saurabh on 19/11/16.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultHolder> {
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
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchResultHolder(inflater.inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final SearchResultHolder holder, int position) {
        Datum datum = datumList.get(position);
        if (datum != null) {
            holder.title.setText(Html.fromHtml(datum.getTitle()));
            holder.description.setText(Html.fromHtml(datum.getDescription()));
            if (!TextUtils.isEmpty(datum.getLink())) {
                LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        holder.previewImageView.setVisibility(View.GONE);
                        holder.descriptionTextView.setVisibility(View.GONE);
                        holder.titleTextView.setVisibility(View.GONE);
                        holder.previewLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPos(final SourceContent sourceContent, boolean b) {
                        holder.previewLayout.setVisibility(View.VISIBLE);
                        holder.previewImageView.setVisibility(View.VISIBLE);
                        holder.descriptionTextView.setVisibility(View.VISIBLE);
                        holder.titleTextView.setVisibility(View.VISIBLE);
                        holder.titleTextView.setText(sourceContent.getTitle());
                        holder.descriptionTextView.setText(sourceContent.getDescription());

                        final List<String> imageList = sourceContent.getImages();
                        if (imageList == null || imageList.size() == 0) {
                            holder.previewImageView.setVisibility(View.GONE);
                        } else {
                            Glide.with(context).load(imageList.get(0))
                                    .centerCrop()
                                    .into(holder.previewImageView);
                        }

                        holder.previewLayout.setOnClickListener(new View.OnClickListener() {
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
            holder.title.setText(null);
            holder.description.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        return datumList == null ? 0 : datumList.size();
    }
}
