package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.activities.ImportantMessages;
import org.fossasia.susi.ai.adapters.viewholders.ChatViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.DateViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.LinkPreviewViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.MapViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.MessageViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.PieChartViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.SearchResultsListHolder;
import org.fossasia.susi.ai.adapters.viewholders.TypingDotsHolder;
import org.fossasia.susi.ai.adapters.viewholders.ZeroHeightHolder;
import org.fossasia.susi.ai.helper.AndroidHelper;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.MapHelper;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.model.MapData;
import org.fossasia.susi.ai.model.WebLink;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pl.tajchert.sample.DotsTextView;

/**
 * Created by
 * --Vatsal Bajpai on
 * --25/09/16 at
 * --9:49 PM
 */

public class ChatFeedRecyclerAdapter extends SelectableAdapter implements MessageViewHolder.ClickListener {
    public static final int USER_MESSAGE = 0;
    public static final int SUSI_MESSAGE = 1;
    public static final int USER_IMAGE = 2;
    public static final int SUSI_IMAGE = 3;
    private static final int MAP = 4;
    private static final int PIECHART = 7;
    private static final int USER_WITHLINK = 5;
    private static final int SUSI_WITHLINK = 6;
    private static final int DOTS = 8;
    private static final int NULL_HOLDER = 9;
    private static final int SEARCH_RESULT = 10;
    private static final int WEB_SEARCH = 11;
    private static final int DATE_VIEW = 12;
    private final RequestManager glide;
    public int highlightMessagePosition = -1;
    public String query = "";
    private Context currContext;
    private Realm realm;
    private int lastMsgCount;
    private String TAG = ChatFeedRecyclerAdapter.class.getSimpleName();
    private RecyclerView recyclerView;
    private MessageViewHolder.ClickListener clickListener;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private SparseBooleanArray selectedItems;
    private AppCompatActivity currActivity;
    private Toast toast;
    // For typing dots from Susi
    private TypingDotsHolder dotsHolder;
    private ZeroHeightHolder nullHolder;
    private boolean isSusiTyping = false;

    public ChatFeedRecyclerAdapter(RequestManager glide, @NonNull Context context, @Nullable OrderedRealmCollection<ChatMessage> data, boolean autoUpdate) {
        super(context, data, autoUpdate);
        this.glide = glide;
        this.clickListener = this;
        currContext = context;
        currActivity = (AppCompatActivity) context;
        lastMsgCount = getItemCount();
        selectedItems = new SparseBooleanArray();
        RealmChangeListener<RealmResults> listener = new RealmChangeListener<RealmResults>() {
            @Override
            public void onChange(RealmResults elements) {
                //only scroll if new is added.
                if (lastMsgCount < getItemCount()) {
                    scrollToBottom();
                }
                lastMsgCount = getItemCount();
            }
        };
        if (data instanceof RealmResults) {
            RealmResults realmResults = (RealmResults) data;
            realmResults.addChangeListener(listener);
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_waiting_dots, null);
        dotsHolder = new TypingDotsHolder(view);
        DotsTextView dots = dotsHolder.dotsTextView;
        dots.start();
        View view1 = inflater.inflate(R.layout.item_without_height, null);
        nullHolder = new ZeroHeightHolder(view1);
    }

    private static List<String> extractLinks(String text) {
        List<String> links = new ArrayList<>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            links.add(url);
        }
        return links;
    }

    public void showDots() {
        isSusiTyping = true;
    }

    public void hideDots() {
        isSusiTyping = false;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
        realm.close();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view;
        switch (viewType) {
            case USER_MESSAGE:
                view = inflater.inflate(R.layout.item_user_message, viewGroup, false);
                return new ChatViewHolder(view, clickListener, USER_MESSAGE);
            case SUSI_MESSAGE:
                view = inflater.inflate(R.layout.item_susi_message, viewGroup, false);
                return new ChatViewHolder(view, clickListener, SUSI_MESSAGE);
            case USER_IMAGE:
                view = inflater.inflate(R.layout.item_user_image, viewGroup, false);
                return new ChatViewHolder(view, clickListener, USER_IMAGE);
            case SUSI_IMAGE:
                view = inflater.inflate(R.layout.item_susi_image, viewGroup, false);
                return new ChatViewHolder(view, clickListener, SUSI_IMAGE);
            case MAP:
                view = inflater.inflate(R.layout.item_susi_map, viewGroup, false);
                return new MapViewHolder(view, clickListener);
            case USER_WITHLINK:
                view = inflater.inflate(R.layout.item_user_link_preview, viewGroup, false);
                return new LinkPreviewViewHolder(view, clickListener);
            case SUSI_WITHLINK:
                view = inflater.inflate(R.layout.item_susi_link_preview, viewGroup, false);
                return new LinkPreviewViewHolder(view, clickListener);
            case PIECHART:
                view = inflater.inflate(R.layout.item_susi_piechart, viewGroup, false);
                return new PieChartViewHolder(view, clickListener);
            case SEARCH_RESULT:
                view = inflater.inflate(R.layout.search_list, viewGroup, false);
                return new SearchResultsListHolder(view,clickListener,realm,currContext);
            case WEB_SEARCH:
                view = inflater.inflate(R.layout.search_list, viewGroup, false);
                return new SearchResultsListHolder(view,clickListener,realm,currContext);
            case DATE_VIEW:
                view = inflater.inflate(R.layout.date_view,viewGroup, false);
                return new DateViewHolder(view);
            case DOTS:
                return dotsHolder;
            case NULL_HOLDER:
                return nullHolder;
            default:
                view = inflater.inflate(R.layout.item_user_message, viewGroup, false);
                return new ChatViewHolder(view, clickListener, USER_MESSAGE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);

        if (item.getId() == -404) return DOTS;
        else if (item.getId() == -405) return NULL_HOLDER;
        else if (item.isDate()) return DATE_VIEW;
        else if (item.isMine() && item.isHavingLink()) return USER_WITHLINK;
        else if (!item.isMine() && item.isHavingLink()) return SUSI_WITHLINK;
        else if (item.isMine() && !item.isHavingLink()) return USER_MESSAGE;

        switch(item.getActionType()) {
            case Constant.ANCHOR :
                return SUSI_MESSAGE;
            case Constant.ANSWER :
                return SUSI_MESSAGE;
            case Constant.MAP :
                return MAP;
            case  Constant.WEBSEARCH :
                return WEB_SEARCH;
            case Constant.RSS :
                return SEARCH_RESULT;
            case  Constant.PIECHART :
                return PIECHART;
            default:
                return SUSI_MESSAGE;
        }
    }

    @Override
    public int getItemCount() {
        if (getData() != null && getData().isValid()) {
            return getData().size() + 1;
        }
        return 0;
    }

    @Nullable
    @Override
    public ChatMessage getItem(int index) {
        if (getData() != null && getData().isValid()) {
            if (index == getData().size()) {
                if (isSusiTyping) {
                    return new ChatMessage(-404, "", "", false, false, false, "", null, "");
                }
                return new ChatMessage(-405, "", "", false, false, false, "", null, "");
            }
            return getData().get(index);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatViewHolder) {
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            handleItemEvents(chatViewHolder, position);
        } else if (holder instanceof MapViewHolder) {
            MapViewHolder mapViewHolder = (MapViewHolder) holder;
            handleItemEvents(mapViewHolder, position);
        } else if (holder instanceof PieChartViewHolder) {
            PieChartViewHolder pieChartViewHolder = (PieChartViewHolder) holder;
            handleItemEvents(pieChartViewHolder, position);
        } else if (holder instanceof LinkPreviewViewHolder) {
            LinkPreviewViewHolder linkPreviewViewHolder = (LinkPreviewViewHolder) holder;
            handleItemEvents(linkPreviewViewHolder, position);
        } else if (holder instanceof SearchResultsListHolder && getItemViewType(position) == SEARCH_RESULT) {
            SearchResultsListHolder searchResultsListHolder = (SearchResultsListHolder) holder;
            handleItemEvents(searchResultsListHolder, position,false);
        } else if (holder instanceof SearchResultsListHolder && getItemViewType(position) == WEB_SEARCH){
            SearchResultsListHolder searchResultsListHolder = (SearchResultsListHolder) holder;
            handleItemEvents(searchResultsListHolder, position, true);
        } else if (holder instanceof DateViewHolder) {
            DateViewHolder dateViewHolder = (DateViewHolder) holder;
            handleItemEvents(dateViewHolder, position);
        }
    }

    private void handleItemEvents(DateViewHolder dateViewHolder, int position){
        dateViewHolder.textDate.setText(getData().get(position).getDate());
    }

    private void handleItemEvents(final SearchResultsListHolder searchResultsListHolder,final int position, boolean isClientSearch) {
        searchResultsListHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
        final ChatMessage model = getData().get(position);
        searchResultsListHolder.setView(model,isClientSearch);
    }

    private void handleItemEvents(final ChatViewHolder chatViewHolder, final int position) {
        final ChatMessage model = getData().get(position);
        int type = getItemViewType(position);
        chatViewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
        chatViewHolder.setView(type,position,query,highlightMessagePosition,model,chatViewHolder);
    }

    private void handleItemEvents(final MapViewHolder mapViewHolder, final int position) {
        final ChatMessage model = getData().get(position);
        mapViewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));

        if (model != null) {
            try {
                final MapHelper mapHelper = new MapHelper(new MapData(model.getLatitude(),model.getLongitude(),model.getZoom()));
                mapViewHolder.pointer.setVisibility(View.GONE);
                Log.v(TAG, mapHelper.getMapURL());
                Glide.with(currContext).load(mapHelper.getMapURL()).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                    {
                        mapViewHolder.pointer.setVisibility(View.VISIBLE);

                        return false;
                    }
                }).into(mapViewHolder.mapImage);

                mapViewHolder.mapImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                          Open in Google Maps if installed, otherwise open browser.
                        */
                        if(selectedItems.size() == 0) {
                            Intent mapIntent;
                            if (AndroidHelper.isGoogleMapsInstalled(currContext) && mapHelper.isParseSuccessful()) {
                                Uri gmmIntentUri = Uri.parse(String.format("geo:%s,%s?z=%s", model.getLatitude(), model.getLongitude(), model.getZoom()));
                                mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage(AndroidHelper.GOOGLE_MAPS_PKG);
                            } else {
                                mapIntent = new Intent(Intent.ACTION_VIEW);
                                mapIntent.setData(Uri.parse(mapHelper.getWebLink()));
                            }
                            currContext.startActivity(mapIntent);
                        } else {
                            toggleSelectedItem(position);
                        }
                    }
                });

                mapViewHolder.mapImage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (actionMode == null) {
                            actionMode = ((AppCompatActivity) currContext).startSupportActionMode(actionModeCallback);
                        }

                        toggleSelectedItem(position);

                        return true;
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleItemEvents(final LinkPreviewViewHolder linkPreviewViewHolder, final int position) {
        final ChatMessage model = getData().get(position);
        linkPreviewViewHolder.messageStar.setVisibility( (model.isImportant()) ? View.VISIBLE : View.GONE);
        linkPreviewViewHolder.text.setText(model.getContent());
        linkPreviewViewHolder.timestampTextView.setText(model.getTimeStamp());
        linkPreviewViewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));

        if (model.getWebLinkData() == null) {
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
                    realm.beginTransaction();
                    Realm realm = Realm.getDefaultInstance();
                    WebLink link = realm.createObject(WebLink.class);

                    linkPreviewViewHolder.previewLayout.setVisibility(View.VISIBLE);
                    linkPreviewViewHolder.previewImageView.setVisibility(View.VISIBLE);
                    linkPreviewViewHolder.descriptionTextView.setVisibility(View.VISIBLE);
                    linkPreviewViewHolder.titleTextView.setVisibility(View.VISIBLE);
                    linkPreviewViewHolder.titleTextView.setText(sourceContent.getTitle());
                    linkPreviewViewHolder.descriptionTextView.setText(sourceContent.getDescription());

                    link.setBody(sourceContent.getDescription());
                    link.setHeadline(sourceContent.getTitle());
                    link.setUrl(sourceContent.getUrl());

                    final List<String> imageList = sourceContent.getImages();
                    if (imageList == null || imageList.size() == 0) {
                        linkPreviewViewHolder.previewImageView.setVisibility(View.GONE);
                        link.setImageURL("");
                    } else {
                        glide.load(imageList.get(0))
                                .centerCrop()
                                .into(linkPreviewViewHolder.previewImageView);
                        link.setImageURL(imageList.get(0));
                    }

                    linkPreviewViewHolder.previewLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(selectedItems.size() == 0) {
                                Uri webpage = Uri.parse(sourceContent.getFinalUrl());
                                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                if (intent.resolveActivity(currContext.getPackageManager()) != null) {
                                    currContext.startActivity(intent);
                                }
                            } else {
                                toggleSelectedItem(position);
                            }
                        }
                    });

                    linkPreviewViewHolder.text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(selectedItems.size() != 0)
                                toggleSelectedItem(position);
                        }
                    });

                    linkPreviewViewHolder.previewLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (actionMode == null) {
                                actionMode = ((AppCompatActivity) currContext).startSupportActionMode(actionModeCallback);
                            }

                            toggleSelectedItem(position);

                            return true;
                        }
                    });

                    linkPreviewViewHolder.text.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (actionMode == null) {
                                actionMode = ((AppCompatActivity) currContext).startSupportActionMode(actionModeCallback);
                            }

                            toggleSelectedItem(position);

                            return true;
                        }
                    });

                    model.setWebLinkData(link);
                    realm.copyToRealmOrUpdate(model);
                    realm.commitTransaction();
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
        } else {
            linkPreviewViewHolder.titleTextView.setText(model.getWebLinkData().getHeadline());
            linkPreviewViewHolder.descriptionTextView.setText(model.getWebLinkData().getBody());
            Log.i(TAG, model.getWebLinkData().getImageURL());
            if (!model.getWebLinkData().getImageURL().equals("")) {
                glide.load(model.getWebLinkData().getImageURL())
                        .centerCrop()
                        .into(linkPreviewViewHolder.previewImageView);
            } else {
                linkPreviewViewHolder.previewImageView.setVisibility(View.GONE);
            }

            linkPreviewViewHolder.previewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedItems.size() == 0) {
                        Uri webpage = Uri.parse(model.getWebLinkData().getUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(currContext.getPackageManager()) != null) {
                            currContext.startActivity(intent);
                        }
                    } else {
                        toggleSelectedItem(position);
                    }
                }
            });

            linkPreviewViewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedItems.size() != 0)
                        toggleSelectedItem(position);
                }
            });

            linkPreviewViewHolder.previewLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (actionMode == null) {
                        actionMode = ((AppCompatActivity) currContext).startSupportActionMode(actionModeCallback);
                    }

                    toggleSelectedItem(position);

                    return true;
                }
            });

            linkPreviewViewHolder.text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (actionMode == null) {
                        actionMode = ((AppCompatActivity) currContext).startSupportActionMode(actionModeCallback);
                    }

                    toggleSelectedItem(position);

                    return true;
                }
            });
        }

        if (highlightMessagePosition == position) {
            String text = linkPreviewViewHolder.text.getText().toString();
            SpannableString modify = new SpannableString(text);
            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(modify);
            while (matcher.find()) {
                int startIndex = matcher.start();
                int endIndex = matcher.end();
                modify.setSpan(new BackgroundColorSpan(Color.parseColor("#ffff00")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            linkPreviewViewHolder.text.setText(modify);
        }
        if (getItemViewType(position) == USER_WITHLINK) {
            if (model.getIsDelivered())
                linkPreviewViewHolder.receivedTick.setImageResource(R.drawable.check);
            else
                linkPreviewViewHolder.receivedTick.setImageResource(R.drawable.clock);
        }
    }

    private void handleItemEvents(final PieChartViewHolder pieChartViewHolder, final int position) {
        final ChatMessage model = getData().get(position);
        if (model != null) {
            pieChartViewHolder.setView(model);
            pieChartViewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
        }
    }

    private void setClipboard(String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) currContext.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

    private void deleteMessage(final int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                getData().deleteFromRealm(position);
                Number temp = realm.where(ChatMessage.class).max("id");
                if (temp == null) {
                    PrefManager.putLong(Constant.MESSAGE_COUNT, 0);
                } else {
                    PrefManager.putLong(Constant.MESSAGE_COUNT, (long) temp + 1);
                }
            }
        });
    }

    private void markImportant(final int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatMessage chatMessage = getItem(position);
                chatMessage.setIsImportant(true);
                realm.copyToRealmOrUpdate(chatMessage);
            }
        });
    }

    private void markUnimportant(final int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatMessage chatMessage = getItem(position);
                chatMessage.setIsImportant(false);
                realm.copyToRealmOrUpdate(chatMessage);
            }
        });
    }

    private void removeDates(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatMessage> AllDates = realm.where(ChatMessage.class).equalTo("isDate",true).findAll().sort("id");

                int dateIndexFirst = getData().indexOf(AllDates.get(0));

                for(int i = 1 ; i < AllDates.size() ; i++ ){
                    int dateIndexSecond = getData().indexOf(AllDates.get(i));
                    if(dateIndexSecond == dateIndexFirst + 1) {
                        getData().deleteFromRealm(dateIndexFirst);
                        dateIndexSecond--;
                    }
                    dateIndexFirst = dateIndexSecond;
                }

                if(dateIndexFirst == getData().size() - 1 && getData().size()>0 ){
                    getData().deleteFromRealm(dateIndexFirst);
                }
                Number temp = realm.where(ChatMessage.class).max("id");
                if (temp == null) {
                    PrefManager.putLong(Constant.MESSAGE_COUNT, 0);
                } else {
                    PrefManager.putLong(Constant.MESSAGE_COUNT, (long) temp + 1);
                }
            }
        });
    }

    private void scrollToBottom() {
        if (getData() != null && !getData().isEmpty() && recyclerView != null) {
            recyclerView.smoothScrollToPosition(getItemCount() - 1);
        }
    }

    private void  toggleSelectedItem(int position) {
        toggleSelection(position);
        int count = getSelectedItemCount();

        Log.d(TAG, position + " " + isSelected(position));
        if(isSelected(position))
            selectedItems.put(position, isSelected(position));
        else
            selectedItems.delete(position);

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelectedItem(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) currContext).startSupportActionMode(actionModeCallback);
        }
        toggleSelectedItem(position);
        return true;
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ChatFeedRecyclerAdapter.ActionModeCallback.class.getSimpleName();
        private int statusBarColor;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_selection_mode, menu);

            MenuItem important = menu.findItem(R.id.menu_item_important);
            MenuItem unimportant = menu.findItem(R.id.menu_item_unimportant);

            if(currContext instanceof ImportantMessages){
                important.setVisible(false);
                unimportant.setVisible(true);
            } else {
                important.setVisible(true);
                unimportant.setVisible(false);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = currActivity.getWindow().getStatusBarColor();
                currActivity.getWindow().setStatusBarColor(ContextCompat.getColor(currContext, R.color.md_teal_500));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int nSelected;
            RealmResults<ChatMessage> important;

            switch (item.getItemId()) {
                case R.id.menu_item_delete:
                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    if (getSelectedItems().size() == 1){
                        d.setMessage("Delete message?").
                                setCancelable(false).
                                setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        for (int i = getSelectedItems().size() - 1; i >= 0; i--) {
                                            deleteMessage(getSelectedItems().get(i));
                                        }
                                        removeDates();
                                        toast = Toast.makeText(recyclerView.getContext() , R.string.message_deleted , Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        actionMode.finish();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                    } else {
                        d.setMessage("Delete " + getSelectedItems().size() + " messages?").
                                setCancelable(false).
                                setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        for (int i = getSelectedItems().size() - 1; i >= 0; i--) {
                                            deleteMessage(getSelectedItems().get(i));
                                        }
                                        removeDates();
                                        toast = Toast.makeText(recyclerView.getContext() ,getSelectedItems().size() + " Messages deleted", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        actionMode.finish();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                    }

                    AlertDialog alert = d.create();
                    alert.show();
                    Button cancel = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    cancel.setTextColor(Color.BLUE);
                    Button delete = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    delete.setTextColor(Color.RED);
                    return true;

                case R.id.menu_item_copy:
                    nSelected = getSelectedItems().size();
                    if (nSelected == 1) {
                        String copyText;
                        int selected = getSelectedItems().get(0);
                        copyText = getItem(selected).getContent();
                        if (getItem(selected).getActionType() == null || getItem(selected).getActionType().equals(Constant.ANSWER)) {
                            setClipboard(copyText);
                        }
                        setClipboard(copyText);
                    } else {
                        String copyText = "";
                        for (int i : getSelectedItems()) {
                            ChatMessage message = getData().get(i);
                            if (message.getActionType()==null || message.getActionType().equals(Constant.ANSWER)) {
                                Log.d(TAG, message.toString());
                                copyText += "[" + message.getTimeStamp() + "]";
                                copyText += " ";
                                copyText += message.isMine() ? "Me: " : "Susi: ";
                                copyText += message.getContent();
                                copyText += "\n";
                            }
                        }
                        copyText = copyText.substring(0, copyText.length() - 1);
                        setClipboard(copyText);
                    }
                    if (nSelected == 1){
                        Toast toast = Toast.makeText(recyclerView.getContext() , R.string.message_copied , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    else {
                        Toast toast = Toast.makeText(recyclerView.getContext(), nSelected + " " + "Messages copied" , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    actionMode.finish();
                    return true;

                case R.id.menu_item_share:
                    nSelected = getSelectedItems().size();
                    if (nSelected == 1) {
                        int selected = getSelectedItems().get(0);
                        if (getItem(selected).getActionType() == null || getItem(selected).getActionType().equals(Constant.ANSWER)) {
                            shareMessage(getItem(selected).getContent());
                        }
                    } else {
                        String shareText = "";
                        for (int i : getSelectedItems()) {
                            ChatMessage message = getData().get(i);
                            if (message.getActionType()==null || message.getActionType().equals(Constant.ANSWER)) {
                                Log.d(TAG, message.toString());
                                shareText += "[" + message.getTimeStamp() + "]";
                                shareText += " ";
                                shareText += message.isMine() ? "Me: " : "Susi: ";
                                shareText += message.getContent();
                                shareText += "\n";
                            }
                        }
                        shareText = shareText.substring(0, shareText.length() - 1);
                        shareMessage(shareText);
                    }

                    actionMode.finish();
                    return true;

                case R.id.menu_item_important:
                    nSelected = getSelectedItems().size();
                    if (nSelected >0)
                    {
                        for (int i = nSelected - 1; i >= 0; i--) {
                            markImportant(getSelectedItems().get(i));
                        }
                        if(nSelected == 1) {
                            Toast.makeText(context,nSelected+" message marked important",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, nSelected + " messages marked important", Toast.LENGTH_SHORT).show();
                        }
                        important = realm.where(ChatMessage.class).equalTo("isImportant", true).findAll().sort("id");
                        for(int i=0;i<important.size();++i)
                            Log.i("message ",""+important.get(i).getContent());
                        Log.i("total ",""+important.size());
                        actionMode.finish();
                    }
                    return true;

                case R.id.menu_item_unimportant:
                    nSelected = getSelectedItems().size();
                    if (nSelected >0) {
                        for (int i = nSelected - 1; i >= 0; i--) {
                            markUnimportant(getSelectedItems().get(i));
                        }
                        if(nSelected == 1){
                            Toast.makeText(context,nSelected+" message marked unimportant",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, nSelected + " messages marked unimportant", Toast.LENGTH_SHORT).show();
                        }
                        important = realm.where(ChatMessage.class).equalTo("isImportant", true).findAll().sort("id");
                        if(important.size() == 0) {
                            TextView emptyList = (TextView) currActivity.findViewById(R.id.tv_empty_list);
                            emptyList.setVisibility(View.VISIBLE);
                        }
                        actionMode.finish();
                    }
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelection();
            selectedItems.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                currActivity.getWindow().setStatusBarColor(statusBarColor);
            }
            actionMode = null;
        }

        private void shareMessage(String message) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");
            currContext.startActivity(sendIntent);
        }
    }
}