package org.fossasia.susi.ai.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.squareup.picasso.Picasso;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.model.WebLink;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * <h1>Link preview view holder</h1>
 *
 * Created by better_clever on 12/10/16.
 */
public class LinkPreviewViewHolder extends MessageViewHolder{

    @BindView(R.id.text)
    public TextView text;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;
    @BindView(R.id.link_preview_image)
    public ImageView previewImageView;
    @BindView(R.id.link_preview_title)
    public TextView titleTextView;
    @BindView(R.id.link_preview_description)
    public TextView descriptionTextView;
    @BindView(R.id.timestamp)
    public TextView timestampTextView;
    @BindView(R.id.preview_layout)
    public LinearLayout previewLayout;
    @Nullable @BindView(R.id.received_tick)
    public ImageView receivedTick;

    private Realm realm;
    private String url;
    private String TAG = ChatFeedRecyclerAdapter.class.getSimpleName();

    /**
     * Instantiates a new Link preview view holder.
     *
     * @param itemView the item view
     * @param listener the listener
     */
    public LinkPreviewViewHolder(View itemView , ClickListener listener) {
        super(itemView, listener);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this,itemView);
    }

    /**
     * Inflate Link Preview
     *
     * @param model the ChatMessage object
     * @param currContext the Context
     * @param highlightMessagePosition the highlighted message position
     * @param position the position
     * @param query the query
     */
    public void setView(final ChatMessage model, final Context currContext, final ChatFeedRecyclerAdapter recyclerAdapter, int highlightMessagePosition, final int position, String query) {
        text.setText(model.getContent());
        timestampTextView.setText(model.getTimeStamp());
        if (model.getWebLinkData() == null) {
            LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                @Override
                public void onPre() {
                    previewImageView.setVisibility(View.GONE);
                    descriptionTextView.setVisibility(View.GONE);
                    titleTextView.setVisibility(View.GONE);
                    previewLayout.setVisibility(View.GONE);
                }

                @Override
                public void onPos(final SourceContent sourceContent, boolean b) {
                    if(!PrefManager.hasTokenExpired() || PrefManager.getBoolean(Constant.ANONYMOUS_LOGGED_IN, false)) {
                        realm.beginTransaction();
                        Realm realm = Realm.getDefaultInstance();
                        WebLink link = realm.createObject(WebLink.class);

                        if (sourceContent != null) {

                            if (!sourceContent.getDescription().isEmpty()) {
                                Log.d(TAG, "onPos: " + sourceContent.getDescription());
                                previewLayout.setVisibility(View.VISIBLE);
                                descriptionTextView.setVisibility(View.VISIBLE);
                                descriptionTextView.setText(sourceContent.getDescription());
                            }

                            if (!sourceContent.getTitle().isEmpty()) {
                                Log.d(TAG, "onPos: " + sourceContent.getTitle());
                                previewLayout.setVisibility(View.VISIBLE);
                                titleTextView.setVisibility(View.VISIBLE);
                                titleTextView.setText(sourceContent.getTitle());
                            }

                            link.setBody(sourceContent.getDescription());
                            link.setHeadline(sourceContent.getTitle());
                            link.setUrl(sourceContent.getUrl());
                            url = sourceContent.getFinalUrl();

                            final List<String> imageList = sourceContent.getImages();

                            if (imageList == null || imageList.size() == 0) {
                                previewImageView.setVisibility(View.GONE);
                                link.setImageURL("");
                            } else {
                                previewImageView.setVisibility(View.VISIBLE);
                                Picasso.with(currContext).load(imageList.get(0))
                                        .fit().centerCrop()
                                        .into(previewImageView);
                                link.setImageURL(imageList.get(0));
                            }
                        }

                        model.setWebLinkData(link);
                        realm.copyToRealmOrUpdate(model);
                        realm.commitTransaction();
                    }
                }
            };

            if (model != null) {
                List<String> urlList = ChatFeedRecyclerAdapter.extractLinks(model.getContent());
                StringBuilder url = new StringBuilder(urlList.get(0));
                StringBuilder http = new StringBuilder("http://");
                StringBuilder https = new StringBuilder("https://");
                if (!(url.toString().startsWith(http.toString()) || url.toString().startsWith(https.toString()))) {
                    url = http.append(url.toString());
                }
                TextCrawler textCrawler = new TextCrawler();
                textCrawler.makePreview(linkPreviewCallback, url.toString());
            }
        } else {

            if(!model.getWebLinkData().getHeadline().isEmpty()) {
                Log.d(TAG, "onPos: " + model.getWebLinkData().getHeadline());
                titleTextView.setText(model.getWebLinkData().getHeadline());
            } else {
                titleTextView.setVisibility(View.GONE);
                Log.d(TAG, "handleItemEvents: " + "isEmpty");
            }

            if(!model.getWebLinkData().getBody().isEmpty()) {
                Log.d(TAG, "onPos: " + model.getWebLinkData().getHeadline());
                descriptionTextView.setText(model.getWebLinkData().getBody());
            } else {
                descriptionTextView.setVisibility(View.GONE);
                Log.d(TAG, "handleItemEvents: " + "isEmpty");
            }

            if(model.getWebLinkData().getHeadline().isEmpty() && model.getWebLinkData().getBody().isEmpty()) {
                previewLayout.setVisibility(View.GONE);
            }

            Log.i(TAG, model.getWebLinkData().getImageURL());
            if (!model.getWebLinkData().getImageURL().equals("")) {
                Picasso.with(currContext).load(model.getWebLinkData().getImageURL())
                        .fit().centerCrop()
                        .into(previewImageView);
            } else {
                previewImageView.setVisibility(View.GONE);
            }

            url = model.getWebLinkData().getUrl();
        }

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerAdapter.selectedItems.size() != 0)
                    recyclerAdapter.toggleSelectedItem(position);
            }
        });

        previewLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (recyclerAdapter.actionMode == null) {
                    recyclerAdapter.actionMode = ((AppCompatActivity) currContext).startSupportActionMode(recyclerAdapter.actionModeCallback);
                }
                recyclerAdapter.toggleSelectedItem(position);

                return true;
            }
        });

        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (recyclerAdapter.actionMode == null) {
                    recyclerAdapter.actionMode = ((AppCompatActivity) currContext).startSupportActionMode(recyclerAdapter.actionModeCallback);
                }

                recyclerAdapter.toggleSelectedItem(position);

                return true;
            }
        });

        previewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerAdapter.selectedItems.size() == 0) {
                    Uri webpage = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(currContext.getPackageManager()) != null) {
                        currContext.startActivity(intent);
                    }
                } else {
                    recyclerAdapter.toggleSelectedItem(position);
                }
            }
        });

        if (highlightMessagePosition == position) {
            String texts = text.getText().toString();
            SpannableString modify = new SpannableString(texts);
            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(modify);
            while (matcher.find()) {
                int startIndex = matcher.start();
                int endIndex = matcher.end();
                modify.setSpan(new BackgroundColorSpan(Color.parseColor("#ffff00")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            text.setText(modify);
        }
    }
}
