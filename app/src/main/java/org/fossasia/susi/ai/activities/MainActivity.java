package org.fossasia.susi.ai.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.adapters.viewHolders.ChatViewHolder;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.SusiResponse;

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
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.rv_chat_feed)
    RecyclerView rvChatFeed;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.btn_send)
    FloatingActionButton btnSend;
    @BindView(R.id.send_message_layout)
    LinearLayout sendMessageLayout;
    RealmResults<ChatMessage> chatMessageDatabaseList;
    private ChatFeedRecyclerAdapter recyclerAdapter;
    private Realm realm;
    private SearchView searchView;
    private Menu menu;
    private int pointer;
    private RealmResults<ChatMessage> results;
    private int offset = 1;

    /**
     * Preference for using Enter Key as send
     */
    SharedPreferences Enter_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
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
        ChatMessage chatMessage = new ChatMessage(id, query, true, false);
        updateDatabase(id, query, true, false);
        recyclerAdapter.addMessage(chatMessage, true);
        computeOtherMessage(query);
    }

    private void computeOtherMessage(final String query) {
        if (isNetworkConnected()) {
            ClientBuilder.getSusiService().getSusiResponse(query).enqueue(new Callback<SusiResponse>() {
                @Override
                public void onResponse(Call<SusiResponse> call, Response<SusiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String answer;
                        try {
                            SusiResponse susiResponse = response.body();
                            answer = susiResponse.getAnswers().get(0).getActions().get(0).getExpression();
                        } catch (IndexOutOfBoundsException | NullPointerException e) {
                            e.printStackTrace();
                            answer = "An error occurred. please try again!";
                        }
                        addNewMessage(answer);
                    }

                }

                @Override
                public void onFailure(Call<SusiResponse> call, Throwable t) {
                    t.printStackTrace();
                    addNewMessage("An error occurred. please try again!");
                }
            });
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Internet Connection Not Available!!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void addNewMessage(String answer) {
        Number temp = realm.where(ChatMessage.class).max("id");
        long id;
        if (temp == null) {
            id = 0;
        } else {
            id = (long) temp + 1;
        }
        ChatMessage chatMessage = new ChatMessage(id, answer, false, false);
        updateDatabase(id, answer, false, false);
        recyclerAdapter.addMessage(chatMessage, true);
    }

    private void updateDatabase(final long id, final String message, final boolean mine, final boolean image) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                ChatMessage chatMessage = bgRealm.createObject(ChatMessage.class);
                chatMessage.setId(id);
                chatMessage.setContent(message);
                chatMessage.setIsMine(mine);
                chatMessage.setIsImage(image);
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
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyMenu(true);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                modifyMenu(false);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //// Handle Search Query
                results = realm.where(ChatMessage.class).contains("content", query, Case.INSENSITIVE).findAll();
                offset = 1;
                Log.d(TAG, String.valueOf(results.size()));
                if (results.size() > 0) {
                    pointer = (int) results.get(results.size() - offset).getId();
                    searchMovement(pointer);
                } else {
                    Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
            case R.id.up_angle:
                offset++;
                if (results.size() - offset > -1) {
                    pointer = (int) results.get(results.size() - offset).getId();
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

    @OnClick(R.id.btn_send)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String message = etMessage.getText().toString();
                message = message.trim();
                if (!TextUtils.isEmpty(message)) {
                    sendMessage(message);
                    etMessage.setText("");
                }
                break;
        }
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
