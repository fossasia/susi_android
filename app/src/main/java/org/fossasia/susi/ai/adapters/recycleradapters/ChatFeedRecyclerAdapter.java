package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.squareup.picasso.Picasso;

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
import org.fossasia.susi.ai.helper.AndroidHelper;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.ConstraintsHelper;
import org.fossasia.susi.ai.helper.MapHelper;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.model.MapData;
import org.fossasia.susi.ai.model.WebLink;
import org.fossasia.susi.ai.model.WebSearchModel;
import org.fossasia.susi.ai.rest.clients.WebSearchClient;
import org.fossasia.susi.ai.rest.services.WebSearchService;
import org.fossasia.susi.ai.rest.responses.susi.Datum;
import org.fossasia.susi.ai.rest.responses.others.WebSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import pl.tajchert.sample.DotsTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <h1>Main adapter to display chat feed as a recycler view.</h1>
 *
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
    public int highlightMessagePosition = -1;
    public String query = "";
    private String webquery ;
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

    /**
     * Extract links from text
     *
     * @param text String text
     * @return List of urls
     */
    private static List<String> extractLinks(String text) {
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
                SearchResultsListHolder searchResultsListHolder = new SearchResultsListHolder(view, clickListener);
                searchResultsListHolder.recyclerView.addItemDecoration(new ConstraintsHelper(6, currContext));
                return new SearchResultsListHolder(view, clickListener);
            case WEB_SEARCH:
                view = inflater.inflate(R.layout.search_list, viewGroup, false);
                SearchResultsListHolder webResultsListHolder = new SearchResultsListHolder(view, clickListener);
                webResultsListHolder.recyclerView.addItemDecoration(new ConstraintsHelper(6, currContext));
                return new SearchResultsListHolder(view, clickListener);
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

    /**
     * Method to handle date views
     *
     * @param dateViewHolder DateViewHolder
     * @param position position of view
     */
    private void handleItemEvents(DateViewHolder dateViewHolder, int position){
        dateViewHolder.textDate.setText(getData().get(position).getDate());
    }

    /**
     * Method to handle Search Results holder for websearch and rss
     *
     * @param searchResultsListHolder Search result list holder
     * @param position position of view
     * @param isClientSearch boolean to check if action type is websearch or rss
     */
    private void handleItemEvents(final SearchResultsListHolder searchResultsListHolder,final int position, boolean isClientSearch) {
        searchResultsListHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
        final ChatMessage model = getData().get(position);
        if(isClientSearch) {
            if(model!=null) {
                webquery = model.getWebquery();

                if(model.getWebSearchList()==null || model.getWebSearchList().size()==0) {
                    final WebSearchService apiService = WebSearchClient.getClient().create(WebSearchService.class);

                    Call<WebSearch> call = apiService.getresult(webquery);

                    call.enqueue(new Callback<WebSearch>() {
                        @Override
                        public void onResponse(Call<WebSearch> call, Response<WebSearch> response) {
                            Log.e(TAG, response.toString());
                            if (response.body() != null ) {
                                realm.beginTransaction();
                                RealmList<WebSearchModel> searchResults = new RealmList<>();

                                for(int i=0 ; i<response.body().getRelatedTopics().size() ; i++) {
                                    try {
                                        String url = response.body().getRelatedTopics().get(i).getUrl();
                                        String text = response.body().getRelatedTopics().get(i).getText();
                                        String iconUrl = response.body().getRelatedTopics().get(i).getIcon().getUrl();
                                        String htmlText = response.body().getRelatedTopics().get(i).getResult();
                                        Realm realm = Realm.getDefaultInstance();
                                        final WebSearchModel webSearch = realm.createObject(WebSearchModel.class);
                                        try {
                                            String[] tempStr = htmlText.split("\">");
                                            String[] tempStr2 = tempStr[tempStr.length-1].split("</a>");
                                            webSearch.setHeadline(tempStr2[0]);
                                            webSearch.setBody(tempStr2[tempStr2.length-1]);
                                        } catch (Exception e ) {
                                            webSearch.setBody(text);
                                            webSearch.setHeadline(webquery);
                                        }
                                        webSearch.setImageURL(iconUrl);
                                        webSearch.setUrl(url);
                                        searchResults.add(webSearch);
                                    } catch (Exception e) {
                                        Log.v(TAG,e.getLocalizedMessage());
                                    }
                                }
                                if(searchResults.size()==0) {
                                    Realm realm = Realm.getDefaultInstance();
                                    final WebSearchModel webSearch = realm.createObject(WebSearchModel.class);
                                    webSearch.setBody(null);
                                    webSearch.setHeadline("No Results Found");
                                    webSearch.setImageURL(null);
                                    webSearch.setUrl(null);
                                    searchResults.add(webSearch);
                                }
                                LinearLayoutManager layoutManager = new LinearLayoutManager(currContext,
                                        LinearLayoutManager.HORIZONTAL, false);

                                searchResultsListHolder.recyclerView.setLayoutManager(layoutManager);
                                WebSearchAdapter resultsAdapter = new WebSearchAdapter(currContext, searchResults);
                                searchResultsListHolder.recyclerView.setAdapter(resultsAdapter);
                                model.setWebSearchList(searchResults);
                                realm.copyToRealmOrUpdate(model);
                                realm.commitTransaction();
                            } else {
                                searchResultsListHolder.recyclerView.setAdapter(null);
                                searchResultsListHolder.recyclerView.setLayoutManager(null);
                            }
                        }

                        @Override
                        public void onFailure(Call<WebSearch> call, Throwable t) {
                            Log.e(TAG, "error" + t.toString());
                        }
                    });
                } else {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(currContext,
                            LinearLayoutManager.HORIZONTAL, false);
                    searchResultsListHolder.recyclerView.setLayoutManager(layoutManager);
                    WebSearchAdapter resultsAdapter = new WebSearchAdapter(currContext, model.getWebSearchList());
                    searchResultsListHolder.recyclerView.setAdapter(resultsAdapter);
                }

            } else{
                searchResultsListHolder.recyclerView.setAdapter(null);
                searchResultsListHolder.recyclerView.setLayoutManager(null);
            }
        } else {
            if (model != null) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(currContext,
                        LinearLayoutManager.HORIZONTAL, false);
                searchResultsListHolder.recyclerView.setLayoutManager(layoutManager);
                SearchResultsAdapter resultsAdapter;
                List<Datum> data = new ArrayList<>();
                int count = model.getCount();
                if(count == -1) {
                    resultsAdapter = new SearchResultsAdapter(currContext, model.getDatumRealmList());

                } else {
                    for (int i=0;i<count;i++) {
                        data.add(model.getDatumRealmList().get(i));
                    }
                    resultsAdapter = new SearchResultsAdapter(currContext, data);
                }
                searchResultsListHolder.recyclerView.setAdapter(resultsAdapter);
            } else {
                searchResultsListHolder.recyclerView.setAdapter(null);
                searchResultsListHolder.recyclerView.setLayoutManager(null);
            }
        }

        searchResultsListHolder.backgroundLayout.setOnLongClickListener(new View.OnLongClickListener() {
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

    /**
     * Method to handle text messages both of user's and susi's
     *
     * @param chatViewHolder Chat view holder
     * @param position position of view
     */
    private void handleItemEvents(final ChatViewHolder chatViewHolder, final int position) {
        final ChatMessage model = getData().get(position);

        chatViewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
        if (model != null) {
            try {
                switch (getItemViewType(position)) {
                    case USER_MESSAGE:
                        chatViewHolder.chatTextView.setText(model.getContent());
                        chatViewHolder.timeStamp.setText(model.getTimeStamp());
                        if(model.getIsDelivered())
                            chatViewHolder.receivedTick.setImageResource(R.drawable.check);
                        else
                            chatViewHolder.receivedTick.setImageResource(R.drawable.clock);

                        chatViewHolder.chatTextView.setTag(chatViewHolder);
                        if (highlightMessagePosition == position) {
                            String text = chatViewHolder.chatTextView.getText().toString();
                            SpannableString modify = new SpannableString(text);
                            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(modify);
                            while (matcher.find()) {
                                int startIndex = matcher.start();
                                int endIndex = matcher.end();
                                modify.setSpan(new BackgroundColorSpan(Color.parseColor("#ffff00")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            chatViewHolder.chatTextView.setText(modify);

                        }
                        chatViewHolder.timeStamp.setTag(chatViewHolder);
                        chatViewHolder.receivedTick.setTag(chatViewHolder);
                        break;
                    case SUSI_MESSAGE:
                        Spanned answerText;
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            answerText = Html.fromHtml(model.getContent(),Html.FROM_HTML_MODE_COMPACT);
                        } else{
                            answerText = Html.fromHtml(model.getContent());
                        }

                        chatViewHolder.chatTextView.setText(answerText);
                        chatViewHolder.chatTextView.setMovementMethod(LinkMovementMethod.getInstance());
                        chatViewHolder.timeStamp.setText(model.getTimeStamp());
                        chatViewHolder.chatTextView.setTag(chatViewHolder);
                        if (highlightMessagePosition == position) {
                            String text = chatViewHolder.chatTextView.getText().toString();
                            SpannableString modify = new SpannableString(text);
                            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(modify);
                            while (matcher.find()) {
                                int startIndex = matcher.start();
                                int endIndex = matcher.end();
                                modify.setSpan(new BackgroundColorSpan(Color.parseColor("#ffff00")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    /**
     * Method to handle Map view holder for map action type
     *
     * @param mapViewHolder Map view holder
     * @param position position of view
     */
    private void handleItemEvents(final MapViewHolder mapViewHolder, final int position) {
        final ChatMessage model = getData().get(position);
        mapViewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));

        if (model != null) {
            try {
                final MapHelper mapHelper = new MapHelper(new MapData(model.getLatitude(),model.getLongitude(),model.getZoom()));
                mapViewHolder.pointer.setVisibility(View.GONE);
                Log.v(TAG, mapHelper.getMapURL());

                Picasso.with(currContext).load(mapHelper.getMapURL())
                        .into(mapViewHolder.mapImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                mapViewHolder.pointer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                Log.d("Error","map image can't loaded");
                            }
                        });

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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to handle Link preview holder and fetching data from link.
     *
     * @param linkPreviewViewHolder Link preview view holder
     * @param position position of view
     */
    private void handleItemEvents(final LinkPreviewViewHolder linkPreviewViewHolder, final int position) {
        final ChatMessage model = getData().get(position);
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

                    if(sourceContent != null) {

                        if (!sourceContent.getDescription().isEmpty()) {
                            Log.d(TAG, "onPos: " + sourceContent.getDescription());
                            linkPreviewViewHolder.previewLayout.setVisibility(View.VISIBLE);
                            linkPreviewViewHolder.descriptionTextView.setVisibility(View.VISIBLE);
                            linkPreviewViewHolder.descriptionTextView.setText(sourceContent.getDescription());
                        }

                        if (!sourceContent.getTitle().isEmpty()) {
                            Log.d(TAG, "onPos: " + sourceContent.getTitle());
                            linkPreviewViewHolder.previewLayout.setVisibility(View.VISIBLE);
                            linkPreviewViewHolder.titleTextView.setVisibility(View.VISIBLE);
                            linkPreviewViewHolder.titleTextView.setText(sourceContent.getTitle());
                        }

                        link.setBody(sourceContent.getDescription());
                        link.setHeadline(sourceContent.getTitle());
                        link.setUrl(sourceContent.getUrl());

                        final List<String> imageList = sourceContent.getImages();

                        if (imageList == null || imageList.size() == 0) {
                            linkPreviewViewHolder.previewImageView.setVisibility(View.GONE);
                            link.setImageURL("");
                        } else {
                            linkPreviewViewHolder.previewImageView.setVisibility(View.VISIBLE);
                            Picasso.with(currContext).load(imageList.get(0))
                                    .fit().centerCrop()
                                    .into(linkPreviewViewHolder.previewImageView);
                            link.setImageURL(imageList.get(0));
                        }

                        linkPreviewViewHolder.previewLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (selectedItems.size() == 0) {
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
                                if (selectedItems.size() != 0)
                                    toggleSelectedItem(position);
                            }
                        });
                    }

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
                linkPreviewViewHolder.titleTextView.setText(model.getWebLinkData().getHeadline());
            } else {
                linkPreviewViewHolder.titleTextView.setVisibility(View.GONE);
                Log.d(TAG, "handleItemEvents: " + "isEmpty");
            }

            if(!model.getWebLinkData().getBody().isEmpty()) {
                Log.d(TAG, "onPos: " + model.getWebLinkData().getHeadline());
                linkPreviewViewHolder.descriptionTextView.setText(model.getWebLinkData().getBody());
            } else {
                linkPreviewViewHolder.descriptionTextView.setVisibility(View.GONE);
                Log.d(TAG, "handleItemEvents: " + "isEmpty");
            }

            if(model.getWebLinkData().getHeadline().isEmpty() && model.getWebLinkData().getBody().isEmpty()) {
                linkPreviewViewHolder.previewLayout.setVisibility(View.GONE);
            }

            Log.i(TAG, model.getWebLinkData().getImageURL());
            if (!model.getWebLinkData().getImageURL().equals("")) {
                Picasso.with(currContext).load(model.getWebLinkData().getImageURL())
                        .fit().centerCrop()
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

    /**
     * Method to handle Pie chart view holder for action type piechart
     *
     * @param pieChartViewHolder Pie chart view holder
     * @param position position of view
     */
    private void handleItemEvents(final PieChartViewHolder pieChartViewHolder, final int position) {
        final ChatMessage model = getData().get(position);
        if (model != null) {
            try {
                pieChartViewHolder.chatTextView.setText(model.getContent());
                pieChartViewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
                pieChartViewHolder.timeStamp.setText(model.getTimeStamp());
                pieChartViewHolder.pieChart.setUsePercentValues(true);
                pieChartViewHolder.pieChart.setDrawHoleEnabled(true);
                pieChartViewHolder.pieChart.setHoleRadius(7);
                pieChartViewHolder.pieChart.setTransparentCircleRadius(10);
                pieChartViewHolder.pieChart.setRotationEnabled(true);
                pieChartViewHolder.pieChart.setRotationAngle(0);
                pieChartViewHolder.pieChart.setDragDecelerationFrictionCoef(0.001f);
                pieChartViewHolder.pieChart.getLegend().setEnabled(false);
                pieChartViewHolder.pieChart.setDescription("");
                RealmList<Datum> datumList = model.getDatumRealmList();
                final ArrayList<Entry> yVals = new ArrayList<>();
                final ArrayList<String> xVals = new ArrayList<>();
                for (int i = 0; i < datumList.size(); i++) {
                    yVals.add(new Entry(datumList.get(i).getPercent(), i));
                    xVals.add(datumList.get(i).getPresident());
                }
                pieChartViewHolder.pieChart.setClickable(false);
                pieChartViewHolder.pieChart.setHighlightPerTapEnabled(false);
                PieDataSet dataSet = new PieDataSet(yVals, "");
                dataSet.setSliceSpace(3);
                dataSet.setSelectionShift(5);
                ArrayList<Integer> colors = new ArrayList<>();
                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);
                for (int c : ColorTemplate.JOYFUL_COLORS)
                    colors.add(c);
                for (int c : ColorTemplate.COLORFUL_COLORS)
                    colors.add(c);
                for (int c : ColorTemplate.LIBERTY_COLORS)
                    colors.add(c);
                for (int c : ColorTemplate.PASTEL_COLORS)
                    colors.add(c);
                dataSet.setColors(colors);
                PieData data = new PieData(xVals, dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.GRAY);
                pieChartViewHolder.pieChart.setData(data);
                pieChartViewHolder.pieChart.highlightValues(null);
                pieChartViewHolder.pieChart.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /**
     * Toggle selection of view
     *
     * @param position position of message
     */
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

    /**
     * Action mode callback for action mode. Used for deleting, copying and sharing messages.
     */
    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ChatFeedRecyclerAdapter.ActionModeCallback.class.getSimpleName();
        private int statusBarColor;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_selection_mode, menu);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = currActivity.getWindow().getStatusBarColor();
                currActivity.getWindow().setStatusBarColor(ContextCompat.getColor(currContext, R.color.md_teal_500));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            if(getSelectedItems().size()>0) {
                Log.d(TAG, "onPrepareActionMode: size " + getSelectedItems().size());
                for(int i = 0; i<getSelectedItems().size() ;i++ ){
                    if(getItemViewType(getSelectedItems().get(i)) == USER_MESSAGE ||
                            getItemViewType(getSelectedItems().get(i)) == SUSI_MESSAGE ||
                            getItemViewType(getSelectedItems().get(i)) == USER_WITHLINK ||
                            getItemViewType(getSelectedItems().get(i)) == SUSI_WITHLINK){
                        menu.clear();
                        mode.getMenuInflater().inflate(R.menu.menu_selection_mode, menu);
                    }
                    else {
                        Log.d(TAG, "onPrepareActionMode: + Other response");
                        menu.removeItem(R.id.menu_item_copy);
                        menu.removeItem(R.id.menu_item_share);
                        break;
                    }
                }
                Log.d(TAG, "onPrepareActionMode: " + getItemViewType(getSelectedItems().get(0)));
            }

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int nSelected;

            switch (item.getItemId()) {

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
                        StringBuilder copyText = new StringBuilder();
                        for (int i : getSelectedItems()) {
                            ChatMessage message = getData().get(i);
                            if (message.getActionType()==null || message.getActionType().equals(Constant.ANSWER)) {
                                Log.d(TAG, message.toString());
                                copyText.append("[").append(message.getTimeStamp()).append("]").append(" ");
                                copyText.append(message.isMine() ? "Me: " : "Susi: ").append(message.getContent()).append("\n");
                            }
                        }
                        setClipboard(copyText.toString());
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
                        StringBuilder shareText = new StringBuilder();
                        for (int i : getSelectedItems()) {
                            ChatMessage message = getData().get(i);
                            if (message.getActionType()==null || message.getActionType().equals(Constant.ANSWER)) {
                                Log.d(TAG, message.toString());
                                shareText.append("[").append(message.getTimeStamp()).append("]").append(" ");
                                shareText.append(message.isMine() ? "Me: " : "Susi: ").append(message.getContent()).append("\n");
                            }
                        }
                        shareMessage(shareText.toString());
                    }

                    actionMode.finish();
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