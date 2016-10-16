package org.fossasia.susi.ai.adapters.recyclerAdapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.activities.MainActivity;
import org.fossasia.susi.ai.adapters.viewHolders.ChatViewHolder;
import org.fossasia.susi.ai.adapters.viewHolders.LinkPreviewViewHolder;
import org.fossasia.susi.ai.adapters.viewHolders.MapViewHolder;
import org.fossasia.susi.ai.adapters.viewHolders.PieChartViewHolder;
import org.fossasia.susi.ai.helper.AndroidHelper;
import org.fossasia.susi.ai.helper.MapHelper;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.rest.model.Datum;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

/**
 * Created by
 * --Vatsal Bajpai on
 * --25/09/16 at
 * --9:49 PM
 */

public class ChatFeedRecyclerAdapter extends RealmRecyclerViewAdapter<ChatMessage, RecyclerView.ViewHolder> {

    public static final int USER_MESSAGE = 0;
    public static final int SUSI_MESSAGE = 1;
    public static final int USER_IMAGE = 2;
    public static final int SUSI_IMAGE = 3;
    public static final int MAP = 4;
    public static final int PIECHART = 7;
    private static final int USER_WITHLINK = 5;
    private static final int SUSI_WITHLINK = 6;
    public int highlightMessagePosition = -1;
    public String query = "";
    private Context currContext;
    private Realm realm;
    private int lastMsgCount;
    private RealmResults<ChatMessage> itemList;
//    private Activity activity;
    private String TAG = ChatFeedRecyclerAdapter.class.getSimpleName();
    private RecyclerView recyclerView;
    private TextToSpeech textToSpeech;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    public ChatFeedRecyclerAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<ChatMessage> data, boolean autoUpdate) {
        super(context, data, autoUpdate);
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

    public void scrollToBottom() {
        if (getData() != null && !getData().isEmpty() && recyclerView != null) {
            recyclerView.smoothScrollToPosition(getItemCount() - 1);
        }
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
            case PIECHART:
                view = inflater.inflate(R.layout.item_susi_piechart, viewGroup, false);
                return new PieChartViewHolder(view);
            default:
                view = inflater.inflate(R.layout.item_user_message, viewGroup, false);
                return new ChatViewHolder(view, USER_MESSAGE);
        }

    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getData().get(position);

        if (item.isMap()) return MAP;
        else if(item.isPieChart()) return PIECHART;
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
        else if (holder instanceof MapViewHolder) {
            MapViewHolder mapViewHolder = (MapViewHolder) holder;
            handleItemEvents(mapViewHolder, position);
        }
        else if(holder instanceof PieChartViewHolder) {
            PieChartViewHolder pieChartViewHolder = (PieChartViewHolder) holder;
            handleItemEvents(pieChartViewHolder,position);
        }
        else if (holder instanceof LinkPreviewViewHolder) {
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
        final ChatMessage model = getData().get(position);
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
                                builder.setItems(new CharSequence[]
                                                {" Copy Text", " Delete"},
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        String str = chatViewHolder.chatTextView.getText().toString();
                                                        setClipboard(currContext,str);

                                                        Snackbar.make(view, "Copied", Snackbar.LENGTH_LONG).show();

                                                        break;
                                                    case 1:
                                                        deleteMessage(position);
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
                        final String toSpeak = model.getContent();
                        chatViewHolder.chatTextView.setText(model.getContent());
                        chatViewHolder.timeStamp.setText(model.getTimeStamp());
                        chatViewHolder.chatTextView.setTag(chatViewHolder);
                        if(MainActivity.checkSpeechOutputPref()){
                            if(MainActivity.checkSpeechOutputPref()){
                                final AudioManager audiofocus = (AudioManager) currContext.getSystemService(Context.AUDIO_SERVICE);
                                int result = audiofocus.requestAudioFocus(afChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
                                if(result== AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                                {
                                    textToSpeech=new TextToSpeech(currContext, new TextToSpeech.OnInitListener() {
                                        @Override
                                        public void onInit(int status) {
                                            if(status != TextToSpeech.ERROR) {
                                                textToSpeech.setLanguage(Locale.UK);
                                                if(position==getItemCount()-1)
                                                    textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                                audiofocus.abandonAudioFocus(afChangeListener);

                                            }
                                        }
                                    });}

                                AudioManager.OnAudioFocusChangeListener afChangeListener =
                                        new AudioManager.OnAudioFocusChangeListener() {
                                            public void onAudioFocusChange(int focusChange) {
                                                if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                                                    textToSpeech.stop();
                                                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                                                    // Resume playback
                                                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                                                    textToSpeech.stop();
                                                }
                                            }
                                        };}}


                        chatViewHolder.chatMessage.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(final View view) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
                                builder.setItems(new CharSequence[]
                                                {" Copy Text", " Delete"},
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        String str = chatViewHolder.chatTextView.getText().toString();
                                                        setClipboard(currContext,str);

                                                        Snackbar.make(view, "Copied", Snackbar.LENGTH_LONG).show();
                                                        break;
                                                    case 1:
                                                        deleteMessage(position);
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

        final ChatMessage model = getData().get(position);
        final String toSpeak = model.getContent();
        String[] parts = toSpeak.split(":");
        final String string1 = parts[0];
        if(MainActivity.checkSpeechOutputPref()){
            final AudioManager audiofocus = (AudioManager) currContext.getSystemService(Context.AUDIO_SERVICE);
            int result = audiofocus.requestAudioFocus(afChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
            if(result== AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            {
                textToSpeech=new TextToSpeech(currContext, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            textToSpeech.setLanguage(Locale.UK);
                            if(position==getItemCount()-1)
                                textToSpeech.speak(string1, TextToSpeech.QUEUE_FLUSH, null);
                            audiofocus.abandonAudioFocus(afChangeListener);

                        }
                    }
                });}

            AudioManager.OnAudioFocusChangeListener afChangeListener =
                    new AudioManager.OnAudioFocusChangeListener() {
                        public void onAudioFocusChange(int focusChange) {
                            if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                                textToSpeech.stop();
                            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                                
                            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                                textToSpeech.stop();
                            }
                        }
                    };
        mapViewHolder.chatMessages.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
                builder.setItems(new CharSequence[]
                                {" Copy Text", " Delete"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        String str = mapViewHolder.text.getText().toString();
                                        setClipboard(currContext,str);
                                        Snackbar.make(view, "Copied", Snackbar.LENGTH_LONG).show();
                                        break;
                                    case 1:
                                        deleteMessage(position);
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

                if(highlightMessagePosition==position)
                {
                    String text = mapViewHolder.text.getText().toString();
                    SpannableString modify = new SpannableString(text);
                    Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(modify);
                    while (matcher.find()) {
                        int startIndex = matcher.start();
                        int endIndex = matcher.end();
                        modify.setSpan(new BackgroundColorSpan(Color.parseColor("#2b3c4e")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    mapViewHolder.text.setText(modify);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }}

    private void deleteMessage(final int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                getData().deleteFromRealm(position);
            }
        });
    }
    private void handleItemEvents(final LinkPreviewViewHolder linkPreviewViewHolder, final int position) {

        final ChatMessage model = getData().get(position);
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
                    Glide.with(currContext)
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

        if(highlightMessagePosition==position)
        {
            String text = linkPreviewViewHolder.text.getText().toString();
            SpannableString modify = new SpannableString(text);
            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(modify);
            while (matcher.find()) {
                int startIndex = matcher.start();
                int endIndex = matcher.end();
                modify.setSpan(new BackgroundColorSpan(Color.parseColor("#2b3c4e")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                for(int i = 0; i < datumList.size(); i++) {
                    yVals.add(new Entry(datumList.get(i).getPercent(),i));
                    xVals.add(datumList.get(i).getPresident());
                }
                pieChartViewHolder.pieChart.setClickable(false);
                pieChartViewHolder.pieChart.setHighlightPerTapEnabled(false);
                PieDataSet dataSet = new PieDataSet(yVals,"");
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




    private void setClipboard(Context context,String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)currContext.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

    }
}