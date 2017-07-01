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
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.model.WebLink;
import org.fossasia.susi.ai.rest.responses.susi.Datum;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

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
    private ChatMessage model;
    private RealmList<WebLink> searchResult;
    private List<SourceContent> sourceContentList = new ArrayList<>();
    private Realm realm;
    private int countno;

    /**
     * Instantiates a new Search results adapter.
     *
     * @param context   the context
     * @param chatMessage the ChatMessage object
     */
    public SearchResultsAdapter(Context context, ChatMessage chatMessage, int count) {
        this.context = context;
        datumList = new ArrayList<>();
        model = chatMessage;
        realm = Realm.getDefaultInstance();
        searchResult = new RealmList<>();
        if (count == -1) {
            countno = model.getDatumRealmList().size();
            datumList = model.getDatumRealmList();
        } else {
            countno = count;
            for (int i = 0; i < count; i++) {
                datumList.add(model.getDatumRealmList().get(i));
            }
        }
        if (model.getWebLinkRealmList().size() == 0) {
            for (int i = 0; i < countno; i++) {
                final Datum datum = datumList.get(i);
                final int flag = i;
                if (!TextUtils.isEmpty(datum.getLink())) {
                    LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                        @Override
                        public void onPre() {
                            //preview is loading
                        }

                        @Override
                        public void onPos(final SourceContent sourceContent, boolean b) {
                            sourceContentList.add(sourceContent);
                            if (flag == countno - 1) {
                                setRealm();
                            }
                        }
                    };
                    TextCrawler textCrawler = new TextCrawler();
                    textCrawler.makePreview(linkPreviewCallback, datum.getLink());
                }
            }
        }
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RssViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RssViewHolder(inflater.inflate(R.layout.rss_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RssViewHolder holder, final int position) {
        Datum datum = datumList.get(position);

        if (datum != null) {
            holder.titleTextView.setText(Html.fromHtml(datum.getTitle()));
            if (datum.getDescription().isEmpty()) {
                holder.descriptionTextView.setVisibility(View.GONE);
            } else {
                holder.descriptionTextView.setVisibility(View.VISIBLE);
                holder.descriptionTextView.setText(Html.fromHtml(datum.getDescription()));
            }
            holder.linkTextView.setText(datum.getLink());
            if (model.getWebLinkRealmList().size() != 0) {
                if (!model.getWebLinkRealmList().get(position).getHeadline().isEmpty()) {
                    holder.linkTitleTextView.setVisibility(View.VISIBLE);
                    holder.linkPreviewLayout.setVisibility(View.VISIBLE);
                    holder.linkTitleTextView.setText(model.getWebLinkRealmList().get(position).getHeadline());
                } else {
                    holder.linkTitleTextView.setVisibility(View.GONE);
                    holder.linkPreviewLayout.setVisibility(View.GONE);
                }

                if(!model.getWebLinkRealmList().get(position).getBody().isEmpty()) {
                    holder.linkDescriptionTextView.setVisibility(View.VISIBLE);
                    holder.linkPreviewLayout.setVisibility(View.VISIBLE);
                    holder.linkDescriptionTextView.setText(model.getWebLinkRealmList().get(position).getBody());
                } else {
                    holder.linkDescriptionTextView.setVisibility(View.GONE);
                    holder.linkPreviewLayout.setVisibility(View.GONE);
                }

                if(model.getWebLinkRealmList().get(position).getHeadline().isEmpty()&&model.getWebLinkRealmList().get(position).getBody().isEmpty()) {
                    holder.linkPreviewLayout.setVisibility(View.GONE);
                }

                if (model.getWebLinkRealmList().get(position).getImageURL().equals("")) {
                    holder.linkPreviewImageView.setVisibility(View.GONE);
                } else if (model.getWebLinkRealmList().get(position).getImageURL().isEmpty()) {
                    holder.linkPreviewImageView.setVisibility(View.GONE);
                } else {
                    holder.linkPreviewImageView.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(model.getWebLinkRealmList().get(position).getImageURL())
                            .fit().centerCrop()
                            .into(holder.linkPreviewImageView);
                }

                holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri webpage = Uri.parse(model.getWebLinkRealmList().get(position).getUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        }
                    }
                });

            } else {
                holder.linkPreviewImageView.setVisibility(View.GONE);
                holder.linkDescriptionTextView.setVisibility(View.GONE);
                holder.linkTitleTextView.setVisibility(View.GONE);
                holder.linkPreviewLayout.setVisibility(View.GONE);
            }

        } else {
            holder.titleTextView.setText(null);
            holder.descriptionTextView.setText(null);
        }
    }

    /**
     * Uploading preview data in db
     */

    public void setRealm() {
        realm.beginTransaction();
        for (int i = 0; i < countno; i++) {
            Realm realm = Realm.getDefaultInstance();
            final WebLink link = realm.createObject(WebLink.class);
            link.setHeadline(sourceContentList.get(i).getTitle());
            link.setBody(sourceContentList.get(i).getDescription());
            link.setUrl(sourceContentList.get(i).getUrl());
            final List<String> imageList = sourceContentList.get(i).getImages();
            if (imageList == null || imageList.size() == 0) {
                link.setImageURL("");
            } else {
                link.setImageURL(imageList.get(0));
            }
            searchResult.add(link);
        }
        model.setWebLinkRealmList(searchResult);
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
    }


    @Override
    public int getItemCount() {
        return datumList == null ? 0 : datumList.size();
    }
}
