package org.fossasia.susi.ai.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.SusiResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.btn_send)
    FloatingActionButton btnSend;
    @BindView(R.id.send_message_layout)
    LinearLayout sendMessageLayout;
    RealmResults<ChatMessage> chatMessageDatabaseList;
    private ChatFeedRecyclerAdapter recyclerAdapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        setupAdapter();
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
        ChatMessage chatMessage = new ChatMessage(query, true, false);
        updateDatabase(query, true, false);
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
        ChatMessage chatMessage = new ChatMessage(answer, false, false);
        updateDatabase(answer, false, false);
        recyclerAdapter.addMessage(chatMessage, true);
    }

    private void updateDatabase(final String message, final boolean mine, final boolean image) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                ChatMessage chatMessage = bgRealm.createObject(ChatMessage.class);
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


//	private void sendMessage() {
//		ChatMessage chatMessage = new ChatMessage(null, true, true);
//		recyclerAdapter.addMessage(chatMessage, true);
//		computeOtherMessage();
//	}

//	private void computeOtherMessage() {
//		ChatMessage chatMessage = new ChatMessage(null, false, true);
//		recyclerAdapter.addMessage(chatMessage, true);
//	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //etMessage.requestFocus();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    //	TODO Removed OnClick for Image for now
//	@OnClick({R.id.iv_image, R.id.btn_send})
    @OnClick(R.id.btn_send)
    public void onClick(View view) {
        switch (view.getId()) {
//			case R.id.iv_image:
//				sendMessage();
//				break;
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
