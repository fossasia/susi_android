package org.fossasia.susi.ai.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.DateTimeHelper;
import org.fossasia.susi.ai.helper.MediaUtil;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.model.MapData;
import org.fossasia.susi.ai.rest.clients.BaseUrl;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.clients.LocationClient;
import org.fossasia.susi.ai.rest.services.LocationService;
import org.fossasia.susi.ai.rest.responses.susi.Datum;
import org.fossasia.susi.ai.helper.LocationHelper;
import org.fossasia.susi.ai.rest.responses.others.LocationResponse;
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse;
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import butterknife.OnClick;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import pl.tajchert.sample.DotsTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

public class MainActivity extends AppCompatActivity {
    public static String TAG = MainActivity.class.getName();
    private final int SELECT_PICTURE = 200;
    private final int CROP_PICTURE = 400;
    private boolean isEnabled = true;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.rv_chat_feed)
    RecyclerView rvChatFeed;
    @BindView(R.id.et_message)
    EditText ChatMessage;
    @BindView(R.id.send_message_layout)
    LinearLayout sendMessageLayout;
    @BindView(R.id.btnSpeak)
    protected ImageView btnSpeak;
    @BindView(R.id.voice_input_text)
    protected TextView voiceInputText;
    @BindView(R.id.dots)
    protected DotsTextView voiceDots;
    @BindView(R.id.cancel)
    protected ImageView cancelInput;

    private boolean atHome = true;
    private boolean backPressedOnce = false;
    private FloatingActionButton fab_scrollToEnd;
    //	 Global Variables used for the setMessage Method
    private String answer;
    private boolean isHavingLink = false;
    private String actionType;
    private MapData mapData = null;
    private List<Datum> datumList = null;
    private boolean micCheck;
    private SearchView searchView;
    private boolean check;
    private Menu menu;
    private int pointer;
    private RealmResults<ChatMessage> results;
    private int offset = 1;
    private ChatFeedRecyclerAdapter recyclerAdapter;
    private Realm realm;
    public static String webSearch;
    private TextToSpeech textToSpeech;
    private BroadcastReceiver networkStateReceiver;
    private ClientBuilder clientBuilder;
    private Deque<Pair<String, Long>> nonDeliveredMessages = new LinkedList<>();
    private LocationHelper locationHelper;
    private SpeechRecognizer recognizer;
    private ProgressDialog progressDialog;
    public long newMessageIndex = 0;
    private double latitude = 0;
    private double longitude = 0;
    private String source = "ip";

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
                                String chat = ChatMessage.getText().toString();
                                String chat_message = chat.trim();
                                String splits[] = chat_message.split("\n");
                                String message = "";
                                for (String split : splits)
                                    message = message.concat(split).concat(" ");
                                if (!TextUtils.isEmpty(chat_message)) {
                                    sendMessage(message, chat);
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
                        displayVoiceInput();
                        promptSpeechInput();
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void hideVoiceInput() {
        voiceInputText.setText("");
        voiceInputText.setVisibility(View.GONE);
        cancelInput.setVisibility(View.GONE);
        voiceDots.hideAndStop();
        voiceDots.setVisibility(View.GONE);
        ChatMessage.setVisibility(View.VISIBLE);
        btnSpeak.setVisibility(View.VISIBLE);
    }

    private void displayVoiceInput() {
        voiceDots.setVisibility(View.VISIBLE);
        voiceInputText.setVisibility(View.VISIBLE);
        cancelInput.setVisibility(View.VISIBLE);
        ChatMessage.setVisibility(View.GONE);
        btnSpeak.setVisibility(View.GONE);
    }

    @OnClick(R.id.cancel)
    public void cancelSpeechInput() {
        if(recognizer != null) {
            recognizer.cancel();
            recognizer.destroy();
            recognizer=null;
        }
        hideVoiceInput();
    }

    public static List<String> extractUrls(String text) {
        List<String> links = new ArrayList<>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            links.add(url);
        }
        return links;
    }

    public static boolean checkSpeechOutputPref() {
        return PrefManager.getBoolean(Constant.SPEECH_OUTPUT, true);
    }

    public static boolean checkSpeechAlwaysPref() {
        return PrefManager.getBoolean(Constant.SPEECH_ALWAYS, false);
    }

    public boolean checkMicInput() {
        return micCheck = MediaUtil.isAvailableForVoiceInput(MainActivity.this);
    }

    private void parseSusiResponse(SusiResponse susiResponse, int i) {

        actionType = susiResponse.getAnswers().get(0).getActions().get(i).getType();
        datumList = null;
        mapData = null;
        webSearch = "";
        isHavingLink = false;
        answer = null;

        switch(actionType) {
            case Constant.ANCHOR :
                try {
                    answer = "<a href=\""  +susiResponse.getAnswers().get(0).getActions().get(i).getAnchorLink() + "\">" + susiResponse.getAnswers().get(0).getActions().get(1).getAnchorText() + "</a>";
                } catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage());
                    answer = getString(R.string.error_occurred_try_again);
                }
                break;

            case Constant.ANSWER :
                try {
                    answer = susiResponse.getAnswers().get(0).getActions()
                            .get(i).getExpression();
                    List<String> urlList = extractUrls(answer);
                    Log.d(TAG, urlList.toString());
                    isHavingLink = urlList != null;
                    if (urlList.size() == 0) isHavingLink = false;
                } catch (Exception e ) {
                    answer = getString(R.string.error_occurred_try_again);
                    isHavingLink = false;
                }
                break;

            case Constant.MAP :
                try {
                    final double latitude = susiResponse.getAnswers().get(0).getActions().get(i).getLatitude();
                    final double longitude = susiResponse.getAnswers().get(0).getActions().get(i).getLongitude();
                    final double zoom = susiResponse.getAnswers().get(0).getActions().get(i).getZoom();
                    mapData = new MapData(latitude,longitude,zoom);
                } catch (Exception e) {
                    mapData = null;
                }
                break;

            case Constant.PIECHART :
                try {
                    datumList = susiResponse.getAnswers().get(0).getData();
                } catch (Exception e) {
                    datumList = null;
                }
                break;

            case Constant.RSS :
                try {
                    datumList = susiResponse.getAnswers().get(0).getData();
                } catch (Exception e) {
                    datumList = null;
                }
                break;

            case Constant.WEBSEARCH :
                try {
                    webSearch = susiResponse.getAnswers().get(0).getActions().get(1).getQuery();
                } catch (Exception e) {
                    webSearch = "";
                }
                break;

            default:
                answer = getString(R.string.error_occurred_try_again);
        }
    }

    private void getOldMessages() {
        if (isNetworkConnected()) {
            Call<MemoryResponse> call = clientBuilder.getSusiApi().getChatHistory();
            call.enqueue(new Callback<MemoryResponse>() {
                @Override
                public void onResponse(Call<MemoryResponse> call, Response<MemoryResponse> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        List<SusiResponse> allMessages = response.body().getCognitionsList();
                        if(allMessages.size() == 0) {
                            showToast("No messages found");
                        } else {
                            long c;
                            for (int i = allMessages.size() - 1; i >= 0; i--) {
                                String query = allMessages.get(i).getQuery();
                                String queryDate = allMessages.get(i).getQueryDate();
                                String answerDdate = allMessages.get(i).getAnswerDate();

                                List<String> urlList = extractUrls(query);
                                Log.d(TAG, urlList.toString());
                                isHavingLink = urlList != null;
                                if (urlList.size() == 0) isHavingLink = false;

                                newMessageIndex = PrefManager.getLong(Constant.MESSAGE_COUNT, 0);

                                if (newMessageIndex == 0) {
                                    updateDatabase(newMessageIndex, "", true, DateTimeHelper.getDate(queryDate), DateTimeHelper.getTime(queryDate), false, null, null, false, null);
                                } else {
                                    String prevDate = DateTimeHelper.getDate(allMessages.get(i+1).getQueryDate());

                                    if (!DateTimeHelper.getDate(queryDate).equals(prevDate)) {
                                        updateDatabase(newMessageIndex, "", true, DateTimeHelper.getDate(queryDate), DateTimeHelper.getTime(queryDate), false, null, null, false, null);
                                    }
                                }

                                c = newMessageIndex;
                                updateDatabase(newMessageIndex, query, false, DateTimeHelper.getDate(queryDate), DateTimeHelper.getTime(queryDate), true, null, null, isHavingLink, null);

                                int actionSize = allMessages.get(i).getAnswers().get(0).getActions().size();

                                for(int j=0 ; j<actionSize ; j++) {
                                    parseSusiResponse(allMessages.get(i),j);
                                    updateDatabase(c, answer, false, DateTimeHelper.getDate(answerDdate), DateTimeHelper.getTime(answerDdate), false, actionType, mapData, isHavingLink, datumList);
                                }
                            }
                        }
                        progressDialog.dismiss();
                    } else {
                        if (!isNetworkConnected()) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                    getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<MemoryResponse> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    progressDialog.dismiss();
                }
            });
        } else {
            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                    getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            snackbar.show();
            progressDialog.dismiss();
        }
    }

    private void retrieveOldMessages() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.dialog_retrieve_messages_title));
        progressDialog.show();
        Thread thread = new Thread() {
            @Override
            public void run() {
                getOldMessages();
            }
        };
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clientBuilder = new ClientBuilder();

        realm = Realm.getDefaultInstance();
        Number temp = realm.where(ChatMessage.class).max(getString(R.string.id));
        if (temp == null) {
            newMessageIndex = 0;
        } else {
            newMessageIndex = (long) temp + 1;
        }

        PrefManager.putLong(Constant.MESSAGE_COUNT, newMessageIndex);

        boolean firstRun = getIntent().getBooleanExtra("FIRST_TIME",false);
        if(firstRun && isNetworkConnected()) {
            retrieveOldMessages();
        }
        if (PrefManager.getString(Constant.ACCESS_TOKEN, null) == null) {
            throw new IllegalStateException("Not signed in, Cannot access resource!");
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO}, 1);
        } else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 3);
        }

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            PrefManager.putBoolean(Constant.MIC_INPUT, checkMicInput());
        }

        getLocationFromIP();
        init();
        compensateTTSDelay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationHelper = new LocationHelper(this.getApplicationContext());
        locationHelper.getLocation();
        getLocationFromLocationService();
    }

    private void compensateTTSDelay() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            Locale locale = textToSpeech.getLanguage();
                            textToSpeech.setLanguage(locale);
                        }
                    }
                });
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.domain.app");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);

        recognizer = SpeechRecognizer
                .createSpeechRecognizer(this.getApplicationContext());
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults == null) {
                    Log.e(TAG, "No voice results");
                } else {
                    Log.d(TAG, "Printing matches: ");
                    for (String match : voiceResults) {
                        Log.d(TAG, match);
                    }
                }
                sendMessage(voiceResults.get(0),voiceResults.get(0));
                recognizer.destroy();
                hideVoiceInput();
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "Ready for speech");
                voiceDots.show();
            }

            @Override
            public void onError(int error) {
                Log.d(TAG,
                        "Error listening for speech: " + error);
                Toast.makeText(getApplicationContext(),"Could not recognize speech, try again.",Toast.LENGTH_SHORT).show();
                recognizer.destroy();
                hideVoiceInput();
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "Speech starting");
                voiceDots.start();
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // This method is intentionally empty
            }

            @Override
            public void onEndOfSpeech() {
                // This method is intentionally empty
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // This method is intentionally empty
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> partial = partialResults
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                voiceInputText.setText(partial.get(0));
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // This method is intentionally empty
            }
        };
        recognizer.setRecognitionListener(listener);
        recognizer.startListening(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Handler mHandler = new Handler(Looper.getMainLooper());
        switch (requestCode) {
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
                                cropCapturedImage(getImageUrl(getApplicationContext(), selectedImageUri));
                            } catch (ActivityNotFoundException aNFE) {
                                //display an error message if user device doesn't support
                                showToast(getString(R.string.error_crop_not_supported));
                                try {
                                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                                    Cursor cursor = getContentResolver().query(getImageUrl(getApplicationContext(), selectedImageUri), filePathColumn, null, null, null);
                                    cursor.moveToFirst();
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

    public static Uri writeToTempImage(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Uri getImageUrl(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImage(context, bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void init() {
        ButterKnife.bind(this);
        fab_scrollToEnd = (FloatingActionButton) findViewById(R.id.btnScrollToEnd);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new computeThread().start();
            }
        };

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
        hideVoiceInput();
        rvChatFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvChatFeed.getLayoutManager();
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < rvChatFeed.getAdapter().getItemCount() - 5) {
                    fab_scrollToEnd.setEnabled(true);
                    fab_scrollToEnd.setVisibility(View.VISIBLE);
                } else {
                    fab_scrollToEnd.setEnabled(false);
                    fab_scrollToEnd.setVisibility(View.GONE);
                }
            }
        });

        ChatMessage.setMaxLines(4);
        ChatMessage.setHorizontallyScrolling(false);
        ChatMessage.addTextChangedListener(watch);
        ChatMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String chat = ChatMessage.getText().toString();
                    String message = chat.trim();
                    if (!TextUtils.isEmpty(message)) {
                        sendMessage(message, chat);
                        ChatMessage.setText("");
                    }
                    handled = true;
                }
                return handled;
            }
        });
        setChatBackground();
    }

    public void getLocationFromIP() {
        final LocationService locationService =
                LocationClient.getClient().create(LocationService.class);
        Call<LocationResponse> call = locationService.getLocationUsingIP();
        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    try {
                        String loc = response.body().getLoc();
                        String s[] = loc.split(",");
                        latitude = Double.parseDouble(s[0]);
                        longitude = Double.parseDouble(s[1]);
                        source = "ip";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    public void getLocationFromLocationService() {
        if (locationHelper.canGetLocation()) {
            latitude = locationHelper.getLatitude();
            longitude = locationHelper.getLongitude();
            source = locationHelper.getSource();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationFromLocationService();
                    locationHelper.getLocation();
                }
                if (grantResults.length == 0 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    micCheck = false;
                    PrefManager.putBoolean(Constant.MIC_INPUT, false);
                } else {
                    PrefManager.putBoolean(Constant.MIC_INPUT, checkMicInput());
                }
                break;
            }

            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationFromLocationService();
                    locationHelper.getLocation();
                }
                break;
            }

            case 3: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    micCheck = false;
                    PrefManager.putBoolean(Constant.MIC_INPUT, false);
                } else {
                    PrefManager.putBoolean(Constant.MIC_INPUT, checkMicInput());
                }
            }
        }
    }

    private void voiceReply(final String reply, final boolean isHavingLink) {
        if ((checkSpeechOutputPref() && check) || checkSpeechAlwaysPref()) {
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
                                    if (isHavingLink) {
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
                    displayVoiceInput();
                    promptSpeechInput();
                }
            });
        } else {
            check = false;
            btnSpeak.setImageResource(R.drawable.ic_send_fab);
            btnSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btnSpeak:
                            String chat = ChatMessage.getText().toString();
                            String message = chat.trim();
                            if (!TextUtils.isEmpty(message)) {
                                sendMessage(message, chat);
                                ChatMessage.setText("");
                            }
                            break;
                    }
                }
            });
        }

        boolean isChecked = PrefManager.getBoolean(Constant.ENTER_SEND, false);
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
        RealmResults<ChatMessage> chatMessageDatabaseList;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvChatFeed.setLayoutManager(linearLayoutManager);
        rvChatFeed.setHasFixedSize(false);
        chatMessageDatabaseList = realm.where(ChatMessage.class).findAllSorted("id");
        recyclerAdapter = new ChatFeedRecyclerAdapter(this, chatMessageDatabaseList, true);
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

    private void sendMessage(String query, String actual) {

        boolean isHavingLink;
        List<String> urlList = extractUrls(query);
        Log.d(TAG, urlList.toString());
        isHavingLink = urlList != null;
        if (urlList.size() == 0) isHavingLink = false;

        newMessageIndex = PrefManager.getLong(Constant.MESSAGE_COUNT, 0);

        if (newMessageIndex == 0) {
            updateDatabase(newMessageIndex, "", true, DateTimeHelper.getDate(), DateTimeHelper.getCurrentTime(), false, null, null, false, null);
        } else {
            String s = realm.where(ChatMessage.class).equalTo("id", newMessageIndex - 1).findFirst().getDate();
            if (!DateTimeHelper.getDate().equals(s)) {
                updateDatabase(newMessageIndex, "", true, DateTimeHelper.getDate(), DateTimeHelper.getCurrentTime(), false, null, null, false, null);
            }
        }
        nonDeliveredMessages.add(new Pair(query, newMessageIndex));
        updateDatabase(newMessageIndex, actual, false, DateTimeHelper.getDate(), DateTimeHelper.getCurrentTime(), true, null, null, isHavingLink, null);
        getLocationFromLocationService();
        new computeThread().start();
    }

    private synchronized void computeOtherMessage() {
        final String query;
        final long id;

        if (null != nonDeliveredMessages && !nonDeliveredMessages.isEmpty()) {
            if (isNetworkConnected()) {
                recyclerAdapter.showDots();
                TimeZone tz = TimeZone.getDefault();
                Date now = new Date();
                int timezoneOffset = -1 * (tz.getOffset(now.getTime()) / 60000);
                query = nonDeliveredMessages.getFirst().first;
                id = nonDeliveredMessages.getFirst().second;
                nonDeliveredMessages.pop();
                Log.d(TAG, clientBuilder.getSusiApi().getSusiResponse(timezoneOffset, longitude, latitude, source, Locale.getDefault().getLanguage(), query).request().url().toString());

                clientBuilder.getSusiApi().getSusiResponse(timezoneOffset, longitude, latitude, source, Locale.getDefault().getLanguage(), query).enqueue(
                        new Callback<SusiResponse>() {
                            @Override
                            public void onResponse(Call<SusiResponse> call,
                                                   Response<SusiResponse> response) {

                                if (response != null && response.isSuccessful() && response.body() != null) {
                                    final SusiResponse susiResponse = response.body();

                                    int actionSize = response.body().getAnswers().get(0).getActions().size();
                                    final String date = response.body().getAnswerDate();

                                    for(int i=0 ; i<actionSize ; i++) {
                                        long delay = response.body().getAnswers().get(0).getActions().get(i).getDelay();
                                        final int actionNo = i;
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                parseSusiResponse(susiResponse,actionNo);
                                                final String setMessage = answer;
                                                if(actionType.equals(Constant.ANSWER))
                                                    voiceReply(setMessage, isHavingLink);
                                                updateDatabase(id, setMessage, false, DateTimeHelper.getDate(date), DateTimeHelper.getTime(date), false, actionType, mapData, isHavingLink, datumList);
                                            }
                                        }, delay);
                                    }
                                    recyclerAdapter.hideDots();
                                } else {
                                    if (!isNetworkConnected()) {
                                        recyclerAdapter.hideDots();
                                        nonDeliveredMessages.addFirst(new Pair(query, id));
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    } else {
                                        updateDatabase(id, getString(R.string.error_internet_connectivity), false, DateTimeHelper.getDate(), DateTimeHelper.getCurrentTime(), false, Constant.ANSWER, mapData, false, datumList);
                                    }
                                    recyclerAdapter.hideDots();
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
                                    nonDeliveredMessages.addFirst(new Pair(query, id));
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                            getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                } else {
                                    updateDatabase(id, getString(R.string.error_internet_connectivity), false, DateTimeHelper.getDate(), DateTimeHelper.getCurrentTime(), false, Constant.ANSWER, mapData, false, datumList);
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

    private void updateDatabase(final long prevId, final String message, final boolean isDate, final String date, final String timeStamp , final boolean mine,
                                final String actionType, final MapData mapData, final boolean isHavingLink,
                                final List<Datum> datumList) {
        final long id = newMessageIndex;
        newMessageIndex++;
        PrefManager.putLong(Constant.MESSAGE_COUNT, newMessageIndex);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                ChatMessage chatMessage = bgRealm.createObject(ChatMessage.class, id);
                chatMessage.setContent(message);
                chatMessage.setDate(date);
                chatMessage.setIsDate(isDate);
                chatMessage.setIsMine(mine);
                chatMessage.setTimeStamp(timeStamp);
                chatMessage.setHavingLink(isHavingLink);
                if (mine)
                    chatMessage.setIsDelivered(false);
                else {
                    chatMessage.setActionType(actionType);
                    chatMessage.setWebquery(webSearch);
                    chatMessage.setIsDelivered(true);
                    if(mapData != null) {
                        chatMessage.setLatitude(mapData.getLatitude());
                        chatMessage.setLongitude(mapData.getLongitude());
                        chatMessage.setZoom(mapData.getZoom());
                    }
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
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v(TAG, getString(R.string.updated_successfully));
                if(!mine) {
                    final long prId = prevId;
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            try {
                                ChatMessage previouschatMessage = bgRealm.where(ChatMessage.class).equalTo("id", prId).findFirst();
                                if(previouschatMessage!= null && previouschatMessage.isMine()) {
                                    previouschatMessage.setIsDelivered(true);
                                    previouschatMessage.setDate(date);
                                    previouschatMessage.setTimeStamp(timeStamp);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                ChatMessage previouschatMessage = bgRealm.where(ChatMessage.class).equalTo("id", prId-1).findFirst();
                                if(previouschatMessage!= null && previouschatMessage.isDate()) {
                                    previouschatMessage.setDate(date);
                                    previouschatMessage.setTimeStamp(timeStamp);
                                    ChatMessage previouschatMessage2 = bgRealm.where(ChatMessage.class).equalTo("id", prId-2).findFirst();
                                    if(previouschatMessage2!= null && previouschatMessage2.getDate().equals(previouschatMessage.getDate())) {
                                        previouschatMessage.deleteFromRealm();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    rvChatFeed.smoothScrollToPosition(recyclerAdapter.getItemCount());
                    recyclerAdapter.notifyDataSetChanged();
                }
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
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage.setVisibility(View.GONE);
                btnSpeak.setVisibility(View.GONE);
                sendMessageLayout.setVisibility(View.GONE);
                isEnabled = false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                modifyMenu(false);
                recyclerAdapter.highlightMessagePosition = -1;
                recyclerAdapter.notifyDataSetChanged();
                searchView.onActionViewCollapsed();
                offset = 1;
                ChatMessage.setVisibility(View.VISIBLE);
                btnSpeak.setVisibility(View.VISIBLE);
                sendMessageLayout.setVisibility(View.VISIBLE);
                return false;
            }

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //// Handle Search Query
                ChatMessage.setVisibility(View.GONE);
                btnSpeak.setVisibility(View.GONE);
                sendMessageLayout.setVisibility(View.GONE);
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
                    recyclerAdapter.highlightMessagePosition = -1;
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    ChatMessage.setVisibility(View.GONE);
                    btnSpeak.setVisibility(View.GONE);
                    sendMessageLayout.setVisibility(View.GONE);
                    results = realm.where(ChatMessage.class).contains(getString(R.string.content),
                            newText, Case.INSENSITIVE).findAll();
                    if(results.size() == 0){
                        return false;
                    }
                    recyclerAdapter.query = newText;
                    offset = 1;
                    Log.d(TAG, String.valueOf(results.size()));
                    modifyMenu(true);
                    pointer = (int) results.get(results.size() - offset).getId();
                    Log.d(TAG,
                            results.get(results.size() - offset).getContent() + "  " +
                                    results.get(results.size() - offset).getId());
                    searchMovement(pointer);
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

            if (!isEnabled) {
                ChatMessage.setVisibility(View.VISIBLE);
                btnSpeak.setVisibility(View.VISIBLE);
                sendMessageLayout.setVisibility(View.VISIBLE);
            }
            modifyMenu(false);
            recyclerAdapter.highlightMessagePosition = -1;
            recyclerAdapter.notifyDataSetChanged();
            searchView.onActionViewCollapsed();
            recyclerAdapter.clearSelection();
            offset = 1;
            return;
        }
        if (atHome) {
            if (backPressedOnce) {
                finish();
                return;
            }
            backPressedOnce = true;
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedOnce = false;
                }
            }, 2000);
        } else if (!atHome) {
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
                int permissionCheck = ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                return true;
            case R.id.action_share:
                try {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            String.format(getString(R.string.promo_msg_template),
                                    String.format(getString(R.string.app_share_url), getPackageName())));
                    startActivity(shareIntent);
                } catch (Exception e) {
                    showToast(getString(R.string.error_msg_retry));
                }
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
        if(recognizer != null) {
            recognizer.cancel();
            recognizer.destroy();
            recognizer=null;
        }
        hideVoiceInput();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(locationHelper != null) {
            locationHelper.removeListener();
            locationHelper = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void scrollToEnd(View view) {
        rvChatFeed.smoothScrollToPosition(rvChatFeed.getAdapter().getItemCount() - 1);
    }

    private class computeThread extends Thread {
        public void run() {
            computeOtherMessage();
        }
    }
}
