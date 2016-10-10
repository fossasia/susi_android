package org.fossasia.susi.ai.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.helper.DateTimeHelper;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.SusiResponse;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static String TAG = MainActivity.class.getName();
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int SELECT_PICTURE = 200;
    private final int CROP_PICTURE = 400;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.rv_chat_feed)
    RecyclerView rvChatFeed;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.send_message_layout)
    LinearLayout sendMessageLayout;
    RealmResults<ChatMessage> chatMessageDatabaseList;
    /**
     * Preference for using Enter Key as send
     */
    SharedPreferences Enter_pref;
    private SearchView searchView;
    private Menu menu;
    private int pointer;
    private RealmResults<ChatMessage> results;
    private int offset = 1;
    private ChatFeedRecyclerAdapter recyclerAdapter;
    private Realm realm;
    private TextView edittext;
    private ImageButton btn;
    private SharedPreferences shre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btn = (ImageButton) findViewById(R.id.btnSpeak);
        edittext = (EditText) findViewById(R.id.et_message);
        edittext.addTextChangedListener(watch);
    }

    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(i2>0)
            {
                btn.setImageResource(R.drawable.ic_send_fab);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.btnSpeak:
                                String message = etMessage.getText().toString();
                                message = message.trim();
                                if (!TextUtils.isEmpty(message)) {
                                    sendMessage(message);
                                    etMessage.setText("");
                                }
                                break;
                        }
                    }
                });
            }
            else
            {
                btn.setImageResource(R.drawable.ic_mic_white_24dp);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        promptSpeechInput();
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    sendMessage(result.get(0));
                }
                break;
            }
            case CROP_PICTURE: {
                if (resultCode == RESULT_OK && null != data) {
                    try {
                        Bundle extras = data.getExtras();
                        Bitmap thePic = extras.getParcelable("data");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        thePic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] b = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        //SharePreference to store image
                        shre = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor edit = shre.edit();
                        edit.putString("image_data", encodedImage);
                        edit.apply();
                        //set gallery image
                        setChatBackground();
                    }catch(NullPointerException e)
                    {
                        System.out.print("NullPointerException caught");
                    }
                }
                break;
            }
            case SELECT_PICTURE: {
                if (resultCode == RESULT_OK && null != data) {
                    Uri selectedImageUri = data.getData();
                    try {
                        cropCapturedImage(selectedImageUri);
                    } catch (ActivityNotFoundException aNFE) {
                        //display an error message if user device doesn't support
                        String errorMessage = "Sorry - your device doesn't support the crop action!";
                        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                }
            }

        }
    }

    private void init() {
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.susi);

        setupAdapter();

        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String message = etMessage.getText().toString();
                    message = message.trim();
                    if (!TextUtils.isEmpty(message)) {
                        sendMessage(message);
                        etMessage.setText("");
                    }
                    handled = true;
                }
                return handled;
            }
        });
        setChatBackground();
    }
    protected void chatBackgroundActivity(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Complete Action Using");
        builder.setItems(new CharSequence[]
                        {"1. Gallery", "2. No Wallpaper", "3. Default"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        shre = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        switch (which) {
                            case 0:
                                Intent bgintent = new Intent();
                                bgintent.setType("image/*");
                                bgintent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(bgintent.createChooser(bgintent,"Select background"), SELECT_PICTURE);
                                break;
                            case 1:
                                SharedPreferences.Editor edit=shre.edit();
                                edit.putString("image_data","no_wall");
                                edit.apply();
                                setChatBackground();
                                break;
                            case 2:
                                SharedPreferences.Editor editor=shre.edit();
                                editor.putString("image_data","default");
                                editor.apply();
                                setChatBackground();
                                break;
                        }
                    }
                });
        builder.create().show();
    }
    public void cropCapturedImage(Uri picUri){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(picUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, CROP_PICTURE);
    }
    public void setChatBackground(){
        shre = PreferenceManager.getDefaultSharedPreferences(this);
        String previouslyChatImage = shre.getString("image_data", "");
        Drawable bg;
        if(previouslyChatImage.equalsIgnoreCase("default")){
            //set default layout
            getWindow().setBackgroundDrawableResource(R.drawable.swirl_pattern);
        }else if(previouslyChatImage.equalsIgnoreCase("no_wall")){
            //set no wall
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#EFF2F6"));
        }
        else if( !previouslyChatImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyChatImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            bg =new BitmapDrawable(getResources(),bitmap);
            //set Drable bitmap which taking from gallery
            getWindow().setBackgroundDrawable(bg);
        }
        else {
            //set defult layout when app launch first time
            getWindow().setBackgroundDrawableResource(R.drawable.swirl_pattern);
        }
    }

    private void checkEnterKeyPref() {
        Enter_pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean check = Enter_pref.getBoolean("Enter_send", false);
        if (check) {
            etMessage.setImeOptions(EditorInfo.IME_ACTION_SEND);
            etMessage.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            etMessage.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            etMessage.setSingleLine(false);
            etMessage.setMaxLines(4);
            etMessage.setVerticalScrollBarEnabled(true);
        }
    }

    private void setupAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvChatFeed.setLayoutManager(linearLayoutManager);
        rvChatFeed.setHasFixedSize(true);

        chatMessageDatabaseList = realm.where(ChatMessage.class).findAll();
        recyclerAdapter = new ChatFeedRecyclerAdapter(this, this, chatMessageDatabaseList);

        rvChatFeed.setAdapter(recyclerAdapter);
        rvChatFeed.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
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
        Number temp = realm.where(ChatMessage.class).max("id");
        long id;
        if (temp == null) {
            id = 0;
        } else {
            id = (long) temp + 1;
        }
        updateDatabase(id, query, true, false, false, DateTimeHelper.getCurrentTime());
        computeOtherMessage(query);
    }

    private void computeOtherMessage(final String query) {
        if (isNetworkConnected()) {
            ClientBuilder.getSusiService().getSusiResponse(query).enqueue(new Callback<SusiResponse>() {
                @Override
                public void onResponse(Call<SusiResponse> call, Response<SusiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String answer;
                        boolean ismap;
                        try {
                            SusiResponse susiResponse = response.body();
                            answer = susiResponse.getAnswers().get(0).getActions().get(0).getExpression();
                            String place = susiResponse.getAnswers().get(0).getData().get(0).getPlace();
                            ismap = place != null && !place.isEmpty();
                        } catch (IndexOutOfBoundsException | NullPointerException e) {
                            e.printStackTrace();
                            answer = "An error occurred. please try again!";
                            ismap = false;
                        }
                        addNewMessage(answer, ismap);
                    } else {
                        addNewMessage("An error occurred. please try again!", false);
                    }

                }

                @Override
                public void onFailure(Call<SusiResponse> call, Throwable t) {
                    t.printStackTrace();
                    addNewMessage("An error occurred. please try again!", false);
                }
            });
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Internet Connection Not Available!!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void addNewMessage(String answer, boolean isMap) {
        Number temp = realm.where(ChatMessage.class).max("id");
        long id;
        if (temp == null) {
            id = 0;
        } else {
            id = (long) temp + 1;
        }
        updateDatabase(id, answer, false, false, isMap, DateTimeHelper.getCurrentTime());
    }

    private void updateDatabase(final long id, final String message, final boolean mine, final boolean image, final boolean isMap, final String timeStamp) {
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
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "update successful!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, error.getMessage());
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
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //// Handle Search Query
                results = realm.where(ChatMessage.class).contains("content", query, Case.INSENSITIVE).findAll();
                recyclerAdapter.query=query;
                offset = 1;
                Log.d(TAG, String.valueOf(results.size()));
                if (results.size() > 0) {
                    modifyMenu(true);
                    pointer = (int) results.get(results.size() - offset).getId();
                    Log.d(TAG,results.get(results.size()-offset).getContent()+"  "+results.get(results.size()-offset).getId());
                    searchMovement(pointer);
                } else {
                    Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText))
                {
                    modifyMenu(false);
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
        super.onBackPressed();
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
                    Log.d(TAG,results.get(results.size()-offset).getContent()+"  "+results.get(results.size()-offset).getId());
                    searchMovement(pointer);
                } else {
                    Toast.makeText(this, "Nothing Up that matches your Query", Toast.LENGTH_SHORT).show();
                    offset--;
                }
                break;
            case R.id.down_angle:
                offset--;
                if (results.size() - offset < results.size()) {
                    pointer = (int) results.get(results.size() - offset).getId();
                    Log.d(TAG,results.get(results.size()-offset).getContent()+"  "+results.get(results.size()-offset).getId());
                    searchMovement(pointer);
                } else {
                    Toast.makeText(this, "Nothing Down that matches your Query", Toast.LENGTH_SHORT).show();
                    offset++;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        checkEnterKeyPref();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
