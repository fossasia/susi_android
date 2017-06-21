package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.squareup.picasso.Picasso;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewholders.SearchResultHolder;
import org.fossasia.susi.ai.rest.responses.susi.Datum;

import java.util.List;

import io.realm.Realm;

/**
 * Created by saurabh on 19/11/16.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultHolder> {
    public static final String TAG = SearchResultsAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private List<Datum> datumList;
    private Realm realm;

    public SearchResultsAdapter(Context context, List<Datum> datumList) {
        this.context = context;
        this.datumList = datumList;
        inflater = LayoutInflater.from(context);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchResultHolder(inflater.inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final SearchResultHolder holder, int position) {
        Datum datum = datumList.get(position);
        if (datum != null) {
            holder.titleTextView.setText(Html.fromHtml(datum.getTitle()));
            holder.descriptionTextView.setText(Html.fromHtml(datum.getDescription()));
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
                            Picasso.with(context).load(imageList.get(0))
                                    .fit().centerCrop()
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

                        holder.previewLayout.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Log.d(TAG, "onLongClick: Clicked");
                                AlertDialog.Builder d = new AlertDialog.Builder(context);
                                d.setMessage("Delete message?").
                                        setCancelable(false).
                                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                clear();
                                                Toast.makeText(context, R.string.message_deleted, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = d.create();
                                alert.show();
                                Button cancel = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                cancel.setTextColor(Color.BLUE);
                                Button delete = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                delete.setTextColor(Color.RED);
                                return false;
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

    private void clear() {
        int size = this.datumList.size();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                datumList.clear();
            }
        });
        notifyItemRangeRemoved(0, size);
    }
}
