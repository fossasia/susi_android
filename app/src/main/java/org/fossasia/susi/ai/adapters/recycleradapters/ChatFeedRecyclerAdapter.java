package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewholders.ChatViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.DateViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.LinkPreviewViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.MapViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.MessageViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.PieChartViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.SearchResultsListHolder;
import org.fossasia.susi.ai.adapters.viewholders.TypingDotsHolder;
import org.fossasia.susi.ai.adapters.viewholders.ZeroHeightHolder;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.ConstraintsHelper;
import org.fossasia.susi.ai.data.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import pl.tajchert.sample.DotsTextView;

/**
 * <h1>Main adapter to display chat feed as a recycler view.</h1>
 *
 * Created by
 * --Vatsal Bajpai on
 * --25/09/16 at
 * --9:49 PM
 */
public class ChatFeedRecyclerAdapter extends RealmRecyclerViewAdapter<ChatMessage, RecyclerView.ViewHolder> implements MessageViewHolder.ClickListener {

    public static final int USER_MESSAGE = 0;
    public static final int SUSI_MESSAGE = 1;
    public static final int USER_IMAGE = 2;
    public static final int SUSI_IMAGE = 3;
    private static final int MAP = 4;
    private static final int PIECHART = 7;
    public static final int USER_WITHLINK = 5;
    private static final int SUSI_WITHLINK = 6;
    private static final int DOTS = 8;
    private static final int NULL_HOLDER = 9;
    private static final int SEARCH_RESULT = 10;
    private static final int WEB_SEARCH = 11;
    private static final int DATE_VIEW = 12;
    private Context currContext;
    private Realm realm;
    private int lastMsgCount;
    private RecyclerView recyclerView;
    private MessageViewHolder.ClickListener clickListener;
    public ActionMode actionMode;
    public SparseBooleanArray selectedItems;
    // For typing dots from Susi
    private TypingDotsHolder dotsHolder;
    private ZeroHeightHolder nullHolder;
    private boolean isSusiTyping = false;

    /**
     * Instantiates a new Chat feed recycler adapter.
     *
     * @param context    the context
     * @param data       the data
     * @param autoUpdate the auto update
     */
    public ChatFeedRecyclerAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<ChatMessage> data, boolean autoUpdate) {
        super(context, data, autoUpdate);
        this.clickListener = this;
        currContext = context;
        lastMsgCount = getItemCount();
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

    /**
     * Extract links from text
     *
     * @param text String text
     * @return List of urls
     */
    public static List<String> extractLinks(String text) {
        List<String> links = new ArrayList<>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            links.add(url);
        }
        return links;
    }

    /**
     * Show dots while susi is typing.
     */
    public void showDots() {
        isSusiTyping = true;
    }

    /**
     * Hide dots when susi is not typing.
     */
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
                return new MapViewHolder(view);
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
                SearchResultsListHolder searchResultsListHolder = new SearchResultsListHolder(view);
                searchResultsListHolder.recyclerView.addItemDecoration(new ConstraintsHelper(6, currContext));
                return searchResultsListHolder;
            case WEB_SEARCH:
                view = inflater.inflate(R.layout.search_list, viewGroup, false);
                SearchResultsListHolder webResultsListHolder = new SearchResultsListHolder(view);
                webResultsListHolder.recyclerView.addItemDecoration(new ConstraintsHelper(6, currContext));
                return webResultsListHolder;
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
                    return new ChatMessage(-404, "", "", false, false, false, "", null, "", 0);
                }
                return new ChatMessage(-405, "", "", false, false, false, "", null, "", 0);
            }
            return getData().get(index);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatViewHolder) {
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            chatViewHolder.setView(getData().get(position), getItemViewType(position));
            //chatViewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
        } else if (holder instanceof MapViewHolder) {
            MapViewHolder mapViewHolder = (MapViewHolder) holder;
            mapViewHolder.setView(getData().get(position), currContext);
        } else if (holder instanceof PieChartViewHolder) {
            PieChartViewHolder pieChartViewHolder = (PieChartViewHolder) holder;
            pieChartViewHolder.setView(getData().get(position));
        } else if (holder instanceof LinkPreviewViewHolder) {
            LinkPreviewViewHolder linkPreviewViewHolder = (LinkPreviewViewHolder) holder;
            linkPreviewViewHolder.setView(getData().get(position), getItemViewType(position), currContext, this, position);
            //linkPreviewViewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
        } else if (holder instanceof SearchResultsListHolder && getItemViewType(position) == SEARCH_RESULT) {
            SearchResultsListHolder searchResultsListHolder = (SearchResultsListHolder) holder;
            searchResultsListHolder.setView(getData().get(position), false, currContext);
        } else if (holder instanceof SearchResultsListHolder && getItemViewType(position) == WEB_SEARCH){
            SearchResultsListHolder searchResultsListHolder = (SearchResultsListHolder) holder;
            searchResultsListHolder.setView(getData().get(position), true, currContext);
        } else if (holder instanceof DateViewHolder) {
            DateViewHolder dateViewHolder = (DateViewHolder) holder;
            dateViewHolder.textDate.setText(getData().get(position).getDate());
        }
    }

    /**
     * To set clipboard for copying text messages
     *
     * @param text text to be copied
     */
    private void setClipboard(String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) currContext.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Scroll to bottom
     */
    private void scrollToBottom() {
        if (getData() != null && !getData().isEmpty() && recyclerView != null) {
            recyclerView.smoothScrollToPosition(getItemCount() - 1);
        }
    }

    @Override
    public void onItemClicked(int position) {
        // Add something here when needed
    }

    @Override
    public boolean onItemLongClicked(int position) {
        //TODO : Display a dialog box here
        return true;
    }
}