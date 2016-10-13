package org.fossasia.susi.ai.adapters.recyclerAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.activities.MainActivity;
import org.fossasia.susi.ai.adapters.viewHolders.ChatViewHolder;
import org.fossasia.susi.ai.adapters.viewHolders.LinkPreviewViewHolder;
import org.fossasia.susi.ai.adapters.viewHolders.MapViewHolder;
import org.fossasia.susi.ai.helper.AndroidHelper;
import org.fossasia.susi.ai.helper.MapHelper;
import org.fossasia.susi.ai.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by
 * --Vatsal Bajpai on
 * --25/09/16 at
 * --9:49 PM
 */

public class ChatFeedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int USER_MESSAGE = 0;
    public static final int SUSI_MESSAGE = 1;
    public static final int USER_IMAGE = 2;
    public static final int SUSI_IMAGE = 3;
    public static final int MAP = 4;
    private static final int USER_WITHLINK = 5;
    private static final int SUSI_WITHLINK = 6;

    public int highlightMessagePosition = -1;
    public String query = "";
    public MainActivity str;
    private Context currContext;
    private RealmResults<ChatMessage> itemList;
    private Activity activity;
    private String TAG = ChatFeedRecyclerAdapter.class.getSimpleName();
    private RecyclerView recyclerView;

    public ChatFeedRecyclerAdapter(Activity activity, final Context curr_context, final RealmResults<ChatMessage> itemList) {
        this.itemList = itemList;
        this.currContext = curr_context;
        this.activity = activity;
        itemList.addChangeListener(new RealmChangeListener<RealmResults<ChatMessage>>() {
            @Override
            public void onChange(RealmResults<ChatMessage> element) {
                if (!itemList.isEmpty()) {
                    notifyItemInserted(ChatFeedRecyclerAdapter.this.itemList.size() - 1);
                    if (recyclerView != null) {
                        recyclerView.smoothScrollToPosition(ChatFeedRecyclerAdapter.this.itemList.size() - 1);
                    }
                }
            }
        });

    }

    public static List<String> extractLinks(String text) {
        List<String> links = new ArrayList<String>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            links.add(url);
        }

        return links;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view;
        switch (viewType) {
            case USER_MESSAGE:
                view = inflater.inflate(R.layout.item_user_message, viewGroup, false);
                return new ChatViewHolder(view, USER_MESSAGE);
            case SUSI_MESSAGE:
                view = inflater.inflate(R.layout.item_susi_message, viewGroup, false);
                return new ChatViewHolder(view, SUSI_MESSAGE);
            case USER_IMAGE:
                view = inflater.inflate(R.layout.item_user_image, viewGroup, false);
                return new ChatViewHolder(view, USER_IMAGE);
            case SUSI_IMAGE:
                view = inflater.inflate(R.layout.item_susi_image, viewGroup, false);
                return new ChatViewHolder(view, SUSI_IMAGE);
            case MAP:
                view = inflater.inflate(R.layout.item_susi_map, viewGroup, false);
                return new MapViewHolder(view);
            case USER_WITHLINK:
                view = inflater.inflate(R.layout.item_user_link_preview, viewGroup, false);
                return new LinkPreviewViewHolder(view);
            case SUSI_WITHLINK:
                view = inflater.inflate(R.layout.item_susi_link_preview, viewGroup, false);
                return new LinkPreviewViewHolder(view);
            default:
                view = inflater.inflate(R.layout.item_user_message, viewGroup, false);
                return new ChatViewHolder(view, USER_MESSAGE);
        }

    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = itemList.get(position);

        if (item.isMap()) return MAP;
        else if (item.isMine() && item.isHavingLink()) return USER_WITHLINK;
        else if (!item.isMine() && item.isHavingLink()) return SUSI_WITHLINK;
        else if (item.isMine() && !item.isImage()) return USER_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return SUSI_MESSAGE;
        else if (item.isMine() && item.isImage()) return USER_IMAGE;
        else return SUSI_IMAGE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatViewHolder) {
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            handleItemEvents(chatViewHolder, position);
        }
        if (holder instanceof MapViewHolder) {
            MapViewHolder mapViewHolder = (MapViewHolder) holder;
            handleItemEvents(mapViewHolder, position);
        }
        if (holder instanceof LinkPreviewViewHolder) {
            LinkPreviewViewHolder linkPreviewViewHolder = (LinkPreviewViewHolder) holder;
            handleItemEvents(linkPreviewViewHolder, position);
        }
       /* if (highlightMessagePosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#3e6182"));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }*/
    }

    private void handleItemEvents(final ChatViewHolder chatViewHolder, final int position) {
        final ChatMessage model = itemList.get(position);
        if (model != null) {
            try {
                switch (getItemViewType(position)) {
                    case USER_MESSAGE:
                        chatViewHolder.chatTextView.setText(model.getContent());
                        chatViewHolder.timeStamp.setText(model.getTimeStamp());
                        chatViewHolder.chatTextView.setTag(chatViewHolder);
                        chatViewHolder.chatMessage.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(final View view) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
                                builder.setTitle("Message");
                                builder.setItems(new CharSequence[]
                                                {"1. Copy Text", "2. Delete"},
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        String str = chatViewHolder.chatTextView.getText().toString();
                                                        setClipboard(currContext,str);

                                                        Snackbar.make(view, "Copied", Snackbar.LENGTH_LONG).show();

                                                        break;
                                                    case 1:
                                                        chatViewHolder.chatMessage.removeAllViews();
                                                        break;

                                                }
                                            }
                                        });
                                builder.create().show();

                                return false;
                            }
                        });
                        if(highlightMessagePosition==position)
                        {
                            String text = chatViewHolder.chatTextView.getText().toString();
                            SpannableString modify = new SpannableString(text);
                            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(modify);
                            while (matcher.find()) {
                                int startIndex = matcher.start();
                                int endIndex = matcher.end();
                                modify.setSpan(new BackgroundColorSpan(Color.parseColor("#2b3c4e")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            chatViewHolder.chatTextView.setText(modify);

                        }
                        chatViewHolder.timeStamp.setTag(chatViewHolder);
                        break;
                    case SUSI_MESSAGE:
                        chatViewHolder.chatTextView.setText(model.getContent());
                        chatViewHolder.timeStamp.setText(model.getTimeStamp());
                        chatViewHolder.chatTextView.setTag(chatViewHolder);
                        chatViewHolder.chatMessage.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(final View view) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
                                builder.setTitle("Message");
                                builder.setItems(new CharSequence[]
                                                {"1. Copy Text", "2. Delete"},
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        String str = chatViewHolder.chatTextView.getText().toString();
                                                        setClipboard(currContext,str);

                                                        Snackbar.make(view, "Copied", Snackbar.LENGTH_LONG).show();
                                                        break;
                                                    case 1:
                                                        chatViewHolder.chatMessage.removeAllViews();
                                                        break;

                                                }
                                            }
                                        });
                                builder.create().show();

                                return false;
                            }
                        });
                        if(highlightMessagePosition==position)
                        {
                            String text = chatViewHolder.chatTextView.getText().toString();
                            SpannableString modify = new SpannableString(text);
                            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(modify);
                            while (matcher.find()) {
                                int startIndex = matcher.start();
                                int endIndex = matcher.end();
                                modify.setSpan(new BackgroundColorSpan(Color.parseColor("#2b3c4e")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            chatViewHolder.chatTextView.setText(modify);

                        }
                        chatViewHolder.timeStamp.setTag(chatViewHolder);
                        break;
                    case USER_IMAGE:
                    case SUSI_IMAGE:
                    default:
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleItemEvents(final MapViewHolder mapViewHolder, final int position) {

        final ChatMessage model = itemList.get(position);
        mapViewHolder.chatMessages.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
                builder.setTitle("Message");
                builder.setItems(new CharSequence[]
                                {"1. Copy Text", "2. Delete"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        String str = mapViewHolder.text.getText().toString();
                                        setClipboard(currContext,str);
                                        Snackbar.make(view, "Copied", Snackbar.LENGTH_LONG).show();
                                        break;
                                    case 1:
                                        mapViewHolder.chatMessages.removeAllViews();
                                        break;

                                }
                            }
                        });
                builder.create().show();

                return false;
            }
        });
        if (model != null) {
            try {
                final MapHelper mapHelper = new MapHelper(model.getContent());
                mapViewHolder.text.setText(mapHelper.getDisplayText());
                mapViewHolder.timestampTextView.setText(model.getTimeStamp());
                Glide.with(currContext).load(mapHelper.getMapURL()).into(mapViewHolder.mapImage);
                mapViewHolder.mapImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * Open in Google Maps if installed, otherwise open browser.
                         */
                        if (AndroidHelper.isGoogleMapsInstalled(currContext) && mapHelper.isParseSuccessful()) {
                            Uri gmmIntentUri = Uri.parse(String.format("geo:%s,%s?z=%s", mapHelper.getLattitude(), mapHelper.getLongitude(), mapHelper.getZoom()));
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage(AndroidHelper.GOOGLE_MAPS_PKG);
                            currContext.startActivity(mapIntent);
                        } else {
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                            mapIntent.setData(Uri.parse(mapHelper.getWebLink()));
                            currContext.startActivity(mapIntent);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleItemEvents(final LinkPreviewViewHolder linkPreviewViewHolder, final int position) {

        final ChatMessage model = itemList.get(position);
        linkPreviewViewHolder.text.setText(model.getContent());
        linkPreviewViewHolder.timestampTextView.setText(model.getTimeStamp());
        LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
            @Override
            public void onPre() {
                linkPreviewViewHolder.previewImageView.setVisibility(View.GONE);
                linkPreviewViewHolder.descriptionTextView.setVisibility(View.GONE);
                linkPreviewViewHolder.titleTextView.setVisibility(View.GONE);
                linkPreviewViewHolder.previewLayout.setVisibility(View.GONE);
            }

            @Override
            public void onPos(final SourceContent sourceContent, boolean b) {

                linkPreviewViewHolder.previewLayout.setVisibility(View.VISIBLE);
                linkPreviewViewHolder.previewImageView.setVisibility(View.VISIBLE);
                linkPreviewViewHolder.descriptionTextView.setVisibility(View.VISIBLE);
                linkPreviewViewHolder.titleTextView.setVisibility(View.VISIBLE);

                linkPreviewViewHolder.titleTextView.setText(sourceContent.getTitle());
                linkPreviewViewHolder.descriptionTextView.setText(sourceContent.getDescription());

                final List<String> imageList = sourceContent.getImages();
                if (imageList == null || imageList.size() == 0) {
                    linkPreviewViewHolder.previewImageView.setVisibility(View.GONE);
                } else {
                    Glide.with(activity)
                            .load(imageList.get(0))
                            .centerCrop()
                            .into(linkPreviewViewHolder.previewImageView);
                }

                linkPreviewViewHolder.previewLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri webpage = Uri.parse(sourceContent.getFinalUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(currContext.getPackageManager()) != null) {
                            currContext.startActivity(intent);
                        }
                    }
                });

            }
        };

        if (model != null) {

            List<String> urlList = extractLinks(model.getContent());
            String url = urlList.get(0);
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                url = "http://" + url;
            }
            TextCrawler textCrawler = new TextCrawler();
            textCrawler.makePreview(linkPreviewCallback, url);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    private void setClipboard(Context context,String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)currContext.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

    }
}