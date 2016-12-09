package org.fossasia.susi.ai.activities;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.DateTimeHelper;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.rest.BaseUrl;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.Datum;
import org.fossasia.susi.ai.rest.model.SusiResponse;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

public class MainActivity extends AppCompatActivity {

    public static String TAG = MainActivity.class.getName();

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int SELECT_PICTURE = 200;
    private final int CROP_PICTURE = 400;
    private  boolean atHome = true;
    private boolean backPressedOnce = false;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.rv_chat_feed)
    RecyclerView rvChatFeed;

    @BindView(R.id.et_message)
    EditText ChatMessage;

    @BindView(R.id.send_message_layout)
    LinearLayout sendMessageLayout;

    @BindView(R.id.btnSpeak)
    ImageButton btnSpeak;

    @BindView(R.id.date)
    TextView dates;

	private FloatingActionButton fab_scrollToEnd;
//	 Global Variables used for the setMessage Method
    private String answer;
    private boolean ismap, isPieChart = false;
    private boolean isHavingLink;
    private boolean isSearchResult;
    private long delay = 0;
    private List<Datum> datumList = null;
//
    private RealmResults<ChatMessage> chatMessageDatabaseList;
    private Boolean micCheck;
    private SearchView searchView;
    private  Boolean check;
    private Menu menu;
    private int pointer;
    private RealmResults<ChatMessage> results;
    private int offset = 1;
    private ChatFeedRecyclerAdapter recyclerAdapter;
    private Realm realm;
    private TextToSpeech textToSpeech;
    private AudioManager.OnAudioFocusChangeListener afChangeListener =
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
            };

    private ClientBuilder clientBuilder;
    private Deque<Pair<String, Long>> nonDeliveredMessages = new LinkedList<>();
    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().trim().length() > 0 || !micCheck) {
                btnSpeak.setImageResource(R.drawable.ic_send_fab);
                btnSpeak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        check = false;
                        switch (view.getId()) {
                            case R.id.btnSpeak:
                                String chat_message = ChatMessage.getText().toString();
                                chat_message = chat_message.trim();
                                String splits[]=chat_message.split("\n");
                                String message="";
                                for (String split : splits) message = message.concat(split).concat(" ");
                                if (!TextUtils.isEmpty(chat_message)) {
                                    sendMessage(message);
                                    ChatMessage.setText("");
                                }
                                break;
                        }
                    }
                });
            } else {
                btnSpeak.setImageResource(R.drawable.ic_mic_white_24dp);
                btnSpeak.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        check = true;
                        promptSpeechInput();
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private BroadcastReceiver networkStateReceiver;

    public static List<String> extractUrls(String text) {
        List<String> links = new ArrayList<String>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            links.add(url);
        }

        return links;
    }

    public static Boolean checkSpeechOutputPref() {
        Boolean checks = PrefManager.getBoolean(Constant.SPEECH_OUTPUT, false);
        return checks;
    }

    public static Boolean checkSpeechAlwaysPref() {
        Boolean checked = PrefManager.getBoolean(Constant.SPEECH_ALWAYS, false);
        return checked;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (PrefManager.getString(Constant.ACCESS_TOKEN, null) == null) {
            throw new IllegalStateException("Not signed in, Cannot access resource!");
        }
        clientBuilder = new ClientBuilder();
        init();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showToast(getString(R.string.speech_not_supported));
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Handler mHandler=new Handler(Looper.getMainLooper());
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> result = data
                                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            sendMessage(result.get(0));
                        }
                    });
                    }
                break;
            }
            case CROP_PICTURE: {
                if (resultCode == RESULT_OK && null != data) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Bundle extras = data.getExtras();
                                Bitmap thePic = extras.getParcelable("data");
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                thePic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] b = baos.toByteArray();
                                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                                //SharePreference to store image
                                PrefManager.putString(Constant.IMAGE_DATA, encodedImage);
                                //set gallery image
                                setChatBackground();
                            } catch (NullPointerException e) {
                                Log.d(TAG, e.getLocalizedMessage());
                            }
                        }
                    });
                }
                break;
            }
            case SELECT_PICTURE: {
                if (resultCode == RESULT_OK && null != data) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            Uri selectedImageUri = data.getData();
                            InputStream imageStream;
                            Bitmap selectedImage;
                            try {
                                cropCapturedImage(selectedImageUri);
                            } catch (ActivityNotFoundException aNFE) {
                                //display an error message if user device doesn't support
                                showToast(getString(R.string.error_crop_not_supported));
                                try {
                                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                                    Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                                    cursor.moveToFirst();
                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                    String picturePath = cursor.getString(columnIndex);
                                    imageStream = getContentResolver().openInputStream(selectedImageUri);
                                    selectedImage = BitmapFactory.decodeStream(imageStream);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] b = baos.toByteArray();
                                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                                    //SharePreference to store image
                                    PrefManager.putString(Constant.IMAGE_DATA, encodedImage);
                                    cursor.close();
                                    //set gallery image
                                    setChatBackground();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
                break;
            }

        }
    }

    private void init() {
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
		fab_scrollToEnd = (FloatingActionButton) findViewById(R.id.btnScrollToEnd);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new computeThread().start();
            }
        };
        Log.d(TAG, "init");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.susi);
        nonDeliveredMessages.clear();
        RealmResults<ChatMessage> nonDelivered = realm.where(ChatMessage.class).equalTo("isDelivered", false).findAll().sort("id");
        for (ChatMessage each : nonDelivered) {
            Log.d(TAG, each.getContent());
            nonDeliveredMessages.add(new Pair(each.getContent(), each.getId()));
        }

		checkEnterKeyPref();
        setupAdapter();

		rvChatFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvChatFeed.getLayoutManager();
                if(linearLayoutManager.findLastCompletelyVisibleItemPosition() < rvChatFeed.getAdapter().getItemCount()-5){
                    fab_scrollToEnd.setEnabled(true);
                    fab_scrollToEnd.setVisibility(View.VISIBLE);
                }else{
                    fab_scrollToEnd.setEnabled(false);
                    fab_scrollToEnd.setVisibility(View.GONE);
                }

            }
        });
		
        ChatMessage.addTextChangedListener(watch);
        dates.setText(DateTimeHelper.getDate());
		
        ChatMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String message = ChatMessage.getText().toString();
                    message = message.trim();
                    if (!TextUtils.isEmpty(message)) {
                        sendMessage(message);
                        ChatMessage.setText("");
                    }
                    handled = true;
                }
                return handled;
            }
        });
        setChatBackground();
    }

    private void voiceReply(final String reply, final boolean isMap) {
        if ((checkSpeechOutputPref()&&check)||checkSpeechAlwaysPref()) {
            final AudioManager audiofocus = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {

                    int result = audiofocus.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    Locale locale = textToSpeech.getLanguage();
                                    textToSpeech.setLanguage(locale);
                                    String spokenReply = reply;
                                    if(isMap) {
                                        spokenReply = reply.substring(0, reply.indexOf("http"));
                                    }
                                    textToSpeech.speak(spokenReply, TextToSpeech.QUEUE_FLUSH, null);
                                    audiofocus.abandonAudioFocus(afChangeListener);

                                }
                            }
                        });
                    }

                }
            });

        }
    }


    protected void chatBackgroundActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.dialog_action_complete);
        builder.setItems(R.array.dialog_complete_action_items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, SELECT_PICTURE);
                                break;
                            case 1:
                                PrefManager.putString(Constant.IMAGE_DATA,
                                        getString(R.string.background_no_wall));
                                setChatBackground();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void cropCapturedImage(Uri picUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(picUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 9);
        cropIntent.putExtra("aspectY", 14);
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, CROP_PICTURE);
    }

    public void setChatBackground() {
        String previouslyChatImage = PrefManager.getString(Constant.IMAGE_DATA, "");
        Drawable bg;
        if (previouslyChatImage.equalsIgnoreCase(getString(R.string.background_no_wall))) {
            //set no wall
            getWindow().getDecorView()
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.default_bg));
        } else if (!previouslyChatImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(previouslyChatImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            bg = new BitmapDrawable(getResources(), bitmap);
            //set Drawable bitmap which taking from gallery
            getWindow().setBackgroundDrawable(bg);
        } else {
            //set default layout when app launch first time
            getWindow().getDecorView()
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.default_bg));
        }
    }

    private void checkEnterKeyPref() {
        micCheck = PrefManager.getBoolean(Constant.MIC_INPUT, true);
        if (micCheck) {
            btnSpeak.setImageResource(R.drawable.ic_mic_white_24dp);
            btnSpeak.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    check = true;
                    promptSpeechInput();
                }
            });
        } else {
            check= false;
            btnSpeak.setImageResource(R.drawable.ic_send_fab);
            btnSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btnSpeak:
                            String message = ChatMessage.getText().toString();
                            message = message.trim();
                            if (!TextUtils.isEmpty(message)) {
                                sendMessage(message);
                                ChatMessage.setText("");
                            }
                            break;
                    }
                }
            });
        }
        Boolean isChecked = PrefManager.getBoolean(Constant.ENTER_SEND, false);
        if (isChecked) {
            ChatMessage.setImeOptions(EditorInfo.IME_ACTION_SEND);
            ChatMessage.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            ChatMessage.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            ChatMessage.setSingleLine(false);
            ChatMessage.setMaxLines(4);
            ChatMessage.setVerticalScrollBarEnabled(true);
        }
    }

    private void setupAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvChatFeed.setLayoutManager(linearLayoutManager);
        rvChatFeed.setHasFixedSize(false);

        chatMessageDatabaseList = realm.where(ChatMessage.class).findAllSorted("id");
        recyclerAdapter = new ChatFeedRecyclerAdapter(Glide.with(this), this, chatMessageDatabaseList, true);

        rvChatFeed.setAdapter(recyclerAdapter);
        rvChatFeed.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rvChatFeed.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int scrollTo = rvChatFeed.getAdapter().getItemCount() - 1;
                            scrollTo = scrollTo >= 0 ? scrollTo : 0;
                            rvChatFeed.scrollToPosition(scrollTo);
                        }
                    }, 10);
                }
            }
        });

    }

    private void sendMessage(String query) {
        Number temp = realm.where(ChatMessage.class).max(getString(R.string.id));
        long id;
        if (temp == null) {
            id = 0;
        } else {
            id = (long) temp + 1;
        }

        boolean isHavingLink;
        List<String> urlList = extractUrls(query);
        Log.d(TAG, urlList.toString());

        isHavingLink = urlList != null;
        if (urlList.size() == 0) isHavingLink = false;

        updateDatabase(id, query, true, false, false, false, isHavingLink, DateTimeHelper.getCurrentTime(), false, null);
        nonDeliveredMessages.add(new Pair(query, id));
        recyclerAdapter.showDots();
        new computeThread().start();
    }



    private synchronized void computeOtherMessage() {
        final String query;
        final long id;
        if (null != nonDeliveredMessages && !nonDeliveredMessages.isEmpty()) {
            if (isNetworkConnected()) {
                TimeZone tz = TimeZone.getDefault();
                Date now = new Date();
                int timezoneOffset = -1 * (tz.getOffset(now.getTime()) / 60000);
                query = nonDeliveredMessages.getFirst().first;
                id = nonDeliveredMessages.getFirst().second;
                nonDeliveredMessages.pop();
                clientBuilder.getSusiApi().getSusiResponse(query, timezoneOffset).enqueue(
                        new Callback<SusiResponse>() {
                            @Override
                            public void onResponse(Call<SusiResponse> call,
                                                   Response<SusiResponse> response) {
                                recyclerAdapter.hideDots();
                                if (response != null && response.isSuccessful() && response.body() != null) {
                                    SusiResponse susiResponse = response.body();
                                    int responseActionSize = response.body().getAnswers().get(0).getActions().size();
                                    for (int counter = 0; counter< responseActionSize ; counter++) {

                                        try {
                                            answer = susiResponse.getAnswers().get(0).getActions()
                                                    .get(counter).getExpression();
                                            delay = susiResponse.getAnswers().get(0).getActions()
                                                    .get(counter).getDelay();

                                            try {
                                                ismap = response.body().getAnswers().get(0).getActions().get(2).getType().equals("map");
                                                datumList = response.body().getAnswers().get(0).getData();
                                            } catch (Exception e) {
                                                ismap = false;
                                            }
                                            List<String> urlList = extractUrls(answer);
                                            Log.d(TAG, urlList.toString());
                                            String setMessage = answer;
                                            voiceReply(setMessage, ismap);
                                            isHavingLink = urlList != null;
                                            if (urlList.size() == 0) isHavingLink = false;
                                        } catch (IndexOutOfBoundsException | NullPointerException e) {
                                            Log.d(TAG, e.getLocalizedMessage());
                                            answer = getString(R.string.error_occurred_try_again);
                                            isHavingLink = false;
                                        }
                                        try {
                                            if (response.body().getAnswers().get(0).getActions().size() > 1) {
                                                String type = response.body().getAnswers().get(0).getActions().get(1).getType();
                                                isPieChart = type != null && type.equals("piechart");
                                                datumList = response.body().getAnswers().get(0).getData();
                                            }
                                        } catch (Exception e) {
                                            Log.d(TAG, e.getLocalizedMessage());
                                            isPieChart = false;
                                        }
                                        try {
                                            isSearchResult = response.body().getAnswers().get(0).getActions().get(1).getType().equals("rss");
                                            datumList = response.body().getAnswers().get(0).getData();
                                        } catch (Exception e) {
                                            isSearchResult = false;
                                        }

                                        realm.executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm bgRealm) {
                                                long prId = id;
                                                try {
                                                    ChatMessage chatMessage = bgRealm.where(ChatMessage.class).equalTo("id", prId).findFirst();
                                                    chatMessage.setIsDelivered(true);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        rvChatFeed.getRecycledViewPool().clear();
                                        recyclerAdapter.notifyItemChanged((int) id);
                                        final String setMessage = answer;

                                        final Handler delayHandler = new Handler();
                                        delayHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                addNewMessage(setMessage, ismap, isHavingLink, isPieChart, isSearchResult, datumList);
                                            }
                                        }, delay);

                                    }

                                    } else {
                                        if (!isNetworkConnected()) {
                                            recyclerAdapter.hideDots();
                                            nonDeliveredMessages.addFirst(new Pair(query, id));
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        } else {
                                            realm.executeTransactionAsync(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm bgRealm) {
                                                    long prId = id;
                                                    try {
                                                        ChatMessage chatMessage = bgRealm.where(ChatMessage.class).equalTo("id", prId).findFirst();
                                                        chatMessage.setIsDelivered(true);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            rvChatFeed.getRecycledViewPool().clear();
                                            recyclerAdapter.notifyItemChanged((int) id);
                                            addNewMessage(getString(R.string.error_invalid_token), false, false, false, false, null);
                                        }
                                    }
                                    if (isNetworkConnected())
                                        computeOtherMessage();
                                    }



                            @Override
                            public void onFailure(Call<SusiResponse> call, Throwable t) {
                                if (t.getLocalizedMessage() != null) {
                                    Log.d(TAG, t.getLocalizedMessage());
                                } else {
                                    Log.d(TAG, "An error occurred", t);
                                }
                                recyclerAdapter.hideDots();

                                if (!isNetworkConnected()) {
                                    recyclerAdapter.hideDots();
                                    nonDeliveredMessages.addFirst(new Pair(query, id));
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                            getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                } else {
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm bgRealm) {
                                            long prId = id;
                                            try {
                                                ChatMessage chatMessage = bgRealm.where(ChatMessage.class).equalTo("id", prId).findFirst();
                                                chatMessage.setIsDelivered(true);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    rvChatFeed.getRecycledViewPool().clear();
                                    recyclerAdapter.notifyItemChanged((int) id);
                                    addNewMessage(getString(R.string.error_internet_connectivity), false, false, false, false, null);
                                }
                                BaseUrl.updateBaseUrl(t);
                                computeOtherMessage();
                            }
                        });
            } else {
                recyclerAdapter.hideDots();
                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    private void addNewMessage(String answer, boolean isMap, boolean isHavingLink, boolean isPieChart, boolean isSearchReult, List<Datum> datumList) {
        Number temp = realm.where(ChatMessage.class).max(getString(R.string.id));
        long id;
        if (temp == null) {
            id = 0;
        } else {
            id = (long) temp + 1;
        }
        updateDatabase(id, answer, false, isSearchReult, false, isMap, isHavingLink, DateTimeHelper.getCurrentTime(), isPieChart, datumList);
    }

    private void updateDatabase(final long id, final String message, final boolean mine, final boolean isSearchResult,
                                final boolean image, final boolean isMap, final boolean isHavingLink, final String timeStamp, final boolean isPieChart, final List<Datum> datumList) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                ChatMessage chatMessage = bgRealm.createObject(ChatMessage.class);
                chatMessage.setId(id);
                chatMessage.setContent(message);
                chatMessage.setIsMine(mine);
                chatMessage.setIsImage(image);
                chatMessage.setTimeStamp(timeStamp);
                chatMessage.setMap(isMap);
                chatMessage.setHavingLink(isHavingLink);
                chatMessage.setIsPieChart(isPieChart);
                chatMessage.setSearchResult(isSearchResult);
                if (mine)
                    chatMessage.setIsDelivered(false);
                else
                    chatMessage.setIsDelivered(true);

                if (datumList != null) {
                    RealmList<Datum> datumRealmList = new RealmList<>();
                    for (Datum datum : datumList) {
                        Datum realmDatum = bgRealm.createObject(Datum.class);
                        realmDatum.setDescription(datum.getDescription());
                        realmDatum.setLink(datum.getLink());
                        realmDatum.setTitle(datum.getTitle());
                        datumRealmList.add(realmDatum);
                    }
                    chatMessage.setDatumRealmList(datumRealmList);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v(TAG, getString(R.string.updated_successfully));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
//      TODO: Create Preference Pane and Enable Options Menu
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                modifyMenu(false);
                recyclerAdapter.highlightMessagePosition = -1;
                recyclerAdapter.notifyDataSetChanged();
                searchView.onActionViewCollapsed();
                offset = 1;
                ChatMessage.setEnabled(true);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //// Handle Search Query
                results = realm.where(ChatMessage.class).contains(getString(R.string.content),
                        query, Case.INSENSITIVE).findAll();
                recyclerAdapter.query = query;
                offset = 1;
                Log.d(TAG, String.valueOf(results.size()));
                if (results.size() > 0) {
                    modifyMenu(true);
                    pointer = (int) results.get(results.size() - offset).getId();
                    Log.d(TAG,
                            results.get(results.size() - offset).getContent() + "  " +
                                    results.get(results.size() - offset).getId());
                    searchMovement(pointer);
                } else {
                    showToast(getString(R.string.not_found));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    modifyMenu(false);
                }else{
                    ChatMessage.setEnabled(false);
                }
                return false;
            }
        });
        return true;
    }

    private void searchMovement(int position) {
        rvChatFeed.scrollToPosition(position);
        recyclerAdapter.highlightMessagePosition = position;
        recyclerAdapter.notifyDataSetChanged();
    }

    private void modifyMenu(boolean show) {
        menu.findItem(R.id.up_angle).setVisible(show);
        menu.findItem(R.id.down_angle).setVisible(show);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            modifyMenu(false);
            recyclerAdapter.highlightMessagePosition = -1;
            recyclerAdapter.notifyDataSetChanged();
            searchView.onActionViewCollapsed();
            offset = 1;
            return;
        }
        if(atHome){
            if(backPressedOnce){
                finish();
            }
            backPressedOnce=true;
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    backPressedOnce= false;
                }
            }, 2000);
        }
        else if(!atHome){
            atHome = true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.wall_settings:
                chatBackgroundActivity();
                return true;
            case R.id.up_angle:
                offset++;
                if (results.size() - offset > -1) {
                    pointer = (int) results.get(results.size() - offset).getId();
                    Log.d(TAG, results.get(results.size() - offset).getContent() + "  " +
                            results.get(results.size() - offset).getId());
                    searchMovement(pointer);
                } else {
                    showToast(getString(R.string.nothing_up_matches_your_query));
                    offset--;
                }
                break;
            case R.id.down_angle:
                offset--;
                if (results.size() - offset < results.size()) {
                    pointer = (int) results.get(results.size() - offset).getId();
                    Log.d(TAG, results.get(results.size() - offset).getContent() + "  " +
                            results.get(results.size() - offset).getId());
                    searchMovement(pointer);
                } else {
                    showToast(getString(R.string.nothing_down_matches_your_query));
                    offset++;
                }
                return true;
            case R.id.action_logout:
                AlertDialog.Builder d = new AlertDialog.Builder(this);
                d.setMessage("Are you sure ?").
                        setCancelable(false).
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //clear token
                                PrefManager.clearToken();
                                //clear all messages.
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.deleteAll();
                                    }
                                });
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }).
                        setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = d.create();
                alert.setTitle("Logout");
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.BLACK);
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.BLACK);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nonDeliveredMessages.clear();
        RealmResults<ChatMessage> nonDelivered = realm.where(ChatMessage.class).equalTo("isDelivered", false).findAll().sort("id");
        for (ChatMessage each : nonDelivered)
            nonDeliveredMessages.add(new Pair(each.getContent(), each.getId()));
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        checkEnterKeyPref();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

	public void scrollToEnd(View view) {
        rvChatFeed.smoothScrollToPosition( rvChatFeed.getAdapter().getItemCount()-1 );
    }
	
    private class computeThread extends Thread {
        public void run() {
            computeOtherMessage();
        }
    }

}
