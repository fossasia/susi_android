package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewholders.ChatViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.LinkPreviewViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.MapViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.MessageViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.PieChartViewHolder;
import org.fossasia.susi.ai.adapters.viewholders.SearchResultsHolder;
import org.fossasia.susi.ai.adapters.viewholders.TypingDotsHolder;
import org.fossasia.susi.ai.adapters.viewholders.ZeroHeightHolder;
import org.fossasia.susi.ai.helper.AndroidHelper;
import org.fossasia.susi.ai.helper.MapHelper;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.rest.model.Datum;

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
    public static final int MAP = 4;
    public static final int PIECHART = 7;
    private static final int USER_WITHLINK = 5;
    private static final int SUSI_WITHLINK = 6;
    private static final int DOTS = 8;
    private static final int NULL_HOLDER = 9;
    private static final int SEARCH_RESULT = 10;
    private final RequestManager glide;
    public int highlightMessagePosition = -1;
    public String query = "";
    private Context currContext;
    private Realm realm;
    private int lastMsgCount;
    private RealmResults<ChatMessage> itemList;
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
        List<String> links = new ArrayList<String>();
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
                return new SearchResultsHolder(view);
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
        else if (item.isMap()) return MAP;
        else if (item.isSearchResult()) return SEARCH_RESULT;
        else if (item.isPieChart()) return PIECHART;
        else if (item.isMine() && item.isHavingLink()) return USER_WITHLINK;
        else if (!item.isMine() && item.isHavingLink()) return SUSI_WITHLINK;
        else if (item.isMine() && !item.isImage()) return USER_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return SUSI_MESSAGE;
        else if (item.isMine() && item.isImage()) return USER_IMAGE;
        else return SUSI_IMAGE;
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
                    return new ChatMessage(-404, "", false, false, false, false, false, false,
                            "", null);
                }
                return new ChatMessage(-405, "", false, false, false, false, false, false,
                        "", null);
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
        } else if (holder instanceof SearchResultsHolder) {
            SearchResultsHolder searchResultsHolder = (SearchResultsHolder) holder;
            handleItemEvents(searchResultsHolder, position);
        }

       /* if (highlightMessagePosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#3e6182"));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }*/
    }

    private void handleItemEvents(SearchResultsHolder searchResultsHolder, int position) {
        final ChatMessage model = getData().get(position);
        if (model != null) {
            searchResultsHolder.message.setText(model.getContent());
            LinearLayoutManager layoutManager = new LinearLayoutManager(currContext,
                    LinearLayoutManager.HORIZONTAL, false);
            searchResultsHolder.recyclerView.setLayoutManager(layoutManager);
            SearchResultsAdapter resultsAdapter = new SearchResultsAdapter(currContext, model.getDatumRealmList());
            searchResultsHolder.recyclerView.setAdapter(resultsAdapter);
            searchResultsHolder.timeStamp.setText(model.getTimeStamp());
        } else {
            searchResultsHolder.recyclerView.setAdapter(null);
            searchResultsHolder.recyclerView.setLayoutManager(null);
            searchResultsHolder.message.setText(null);
            searchResultsHolder.timeStamp.setText(null);
        }
    }

    private void handleItemEvents(final ChatViewHolder chatViewHolder, final int position) {
        final ChatMessage model = getData().get(position);

        chatViewHolder.chatMessageView.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
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
                        chatViewHolder.chatTextView.setText(model.getContent());
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

    private void handleItemEvents(final MapViewHolder mapViewHolder, final int position) {

        final ChatMessage model = getData().get(position);
        mapViewHolder.chatMessageView.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));

        if (model != null) {
            try {
                final MapHelper mapHelper = new MapHelper(model.getContent());
                mapViewHolder.text.setText(mapHelper.getDisplayText());
                mapViewHolder.timestampTextView.setText(model.getTimeStamp());
                Glide.with(currContext).load(mapHelper.getMapURL()).into(mapViewHolder.mapImage);
                mapViewHolder.mapImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapViewHolder.onClick(v);
                        /*
                          Open in Google Maps if installed, otherwise open browser.
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

                if (highlightMessagePosition == position) {
                    String text = mapViewHolder.text.getText().toString();
                    SpannableString modify = new SpannableString(text);
                    Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(modify);
                    while (matcher.find()) {
                        int startIndex = matcher.start();
                        int endIndex = matcher.end();
                        modify.setSpan(new BackgroundColorSpan(Color.parseColor("#ffff00")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    mapViewHolder.text.setText(modify);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleItemEvents(final LinkPreviewViewHolder linkPreviewViewHolder, final int position) {

        final ChatMessage model = getData().get(position);
        linkPreviewViewHolder.text.setText(model.getContent());
        linkPreviewViewHolder.timestampTextView.setText(model.getTimeStamp());
        if(model.getIsDelivered())
            linkPreviewViewHolder.receivedTick.setImageResource(R.drawable.check);
        else
            linkPreviewViewHolder.receivedTick.setImageResource(R.drawable.clock);
        linkPreviewViewHolder.chatMessageView.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));

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
                    glide.load(imageList.get(0))
                            .centerCrop()
                            .into(linkPreviewViewHolder.previewImageView);
                }

                linkPreviewViewHolder.previewLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        linkPreviewViewHolder.onClick(view);
                        Uri webpage = Uri.parse(sourceContent.getFinalUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(currContext.getPackageManager()) != null) {
                            currContext.startActivity(intent);
                        }
                    }
                });

            }
        };

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

    private void handleItemEvents(final PieChartViewHolder pieChartViewHolder, final int position) {

        final ChatMessage model = getData().get(position);
        if (model != null) {
            try {
                pieChartViewHolder.chatTextView.setText(model.getContent());
                pieChartViewHolder.chatMessageView.setBackgroundColor(ContextCompat.getColor(currContext, isSelected(position) ? R.color.translucent_blue : android.R.color.transparent));
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
            }
        });
    }

    private void scrollToBottom() {
        if (getData() != null && !getData().isEmpty() && recyclerView != null) {
            recyclerView.smoothScrollToPosition(getItemCount() - 1);
        }
    }

    private void toggleSelectedItem(int position) {

        toggleSelection(position);
        int count = getSelectedItemCount();

        Log.d(TAG, position + " " + isSelected(position));
        selectedItems.put(position, isSelected(position));

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
            switch (item.getItemId()) {
                case R.id.menu_item_delete:
                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setMessage("Are you sure ?").
                            setCancelable(false).
                            setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    for (int i = getSelectedItems().size() - 1; i >= 0; i--) {
                                        deleteMessage(getSelectedItems().get(i));
                                    }
                                    if (getSelectedItems().size() == 1)
                                        Snackbar.make(recyclerView, " Message Deleted !!", Snackbar.LENGTH_LONG).show();
                                    else
                                        Snackbar.make(recyclerView, getSelectedItems().size() + " Messages Deleted !!", Snackbar.LENGTH_LONG).show();

                                    actionMode.finish();

                                }
                            }).
                            setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = d.create();
                    alert.setTitle("Delete");
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
                        if (getItem(selected).isMap()) {
                            copyText = copyText.substring(0, copyText.indexOf("http"));

                        }
                        setClipboard(copyText);
                    } else {
                        String copyText = "";
                        for (Integer i : getSelectedItems()) {
                            ChatMessage message = getData().get(i);
                            Log.d(TAG, message.toString());
                            copyText += "[" + message.getTimeStamp() + "]";
                            copyText += " ";
                            copyText += message.isMine() ? "Me: " : "Susi: ";
                            if (message.isMap()) {
                                String CopiedText = getData().get(i).getContent();
                                copyText += CopiedText.substring(0, CopiedText.indexOf("http"));
                            } else
                                copyText += message.getContent();
                            copyText += "\n";
                            Log.d("copyText", " " + i + " " + copyText);
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
                        shareMessage(getItem(selected).getContent());
                    } else {
                        String shareText = "";
                        for (Integer i : getSelectedItems()) {
                            ChatMessage message = getData().get(i);
                            Log.d(TAG, message.toString());
                            shareText += "[" + message.getTimeStamp() + "]";
                            shareText += " ";
                            shareText += message.isMine() ? "Me: " : "Susi: ";
                            shareText += message.getContent();
                            shareText += "\n";
                        }
                        shareText = shareText.substring(0, shareText.length() - 1);
                        shareMessage(shareText);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                currActivity.getWindow().setStatusBarColor(statusBarColor);
            }
            actionMode = null;
        }

        public void shareMessage(String message) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");
            currContext.startActivity(sendIntent);
        }
    }
}