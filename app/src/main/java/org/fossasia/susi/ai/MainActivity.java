package org.fossasia.susi.ai;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiClickedListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import org.loklak.android.tools.JsonIO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    private EmojiPopup emojiPopup;
    EmojiEditText editText;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.rv_chat_feed)
    RecyclerView rvChatFeed;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.rl)
    ViewGroup rootView;
    //@BindView(R.id.et_message)
 //   EditText etMessage;
    @BindView(R.id.btn_send)
    FloatingActionButton btnSend;
    @BindView(R.id.send_message_layout)
    LinearLayout sendMessageLayout;

    private ChatFeedRecyclerAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        editText = (EmojiEditText) findViewById(R.id.emojiEditText);
        setUpEmojiPopup();

        setupAdapter();
    }

    private void setupAdapter() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rvChatFeed.setLayoutManager(linearLayoutManager);
        rvChatFeed.setHasFixedSize(true);
        List<ChatMessage> chatMessageList = new ArrayList<>();
        recyclerAdapter = new ChatFeedRecyclerAdapter(this, this, chatMessageList);

        rvChatFeed.setAdapter(recyclerAdapter);
		
		rvChatFeed.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){
			@Override
			public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (bottom < oldBottom) {
					rvChatFeed.postDelayed(new Runnable() {
						@Override
						public void run() {
							int scrollTo = rvChatFeed.getAdapter().getItemCount() - 1;
							scrollTo = scrollTo>=0 ? scrollTo : 0;
							rvChatFeed.scrollToPosition(scrollTo);
						}
					}, 10);
				}
			}
		});

    }

    private void sendMessage(String query) {
        ChatMessage chatMessage = new ChatMessage(query, true, false);
        recyclerAdapter.addMessage(chatMessage, true);
        computeOtherMessage(query);
    }

    private void computeOtherMessage(final String query) {
        new asksusi().execute(query);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        recyclerAdapter.addMessage(chatMessage, true);
        computeOtherMessage();
    }

    private void computeOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        recyclerAdapter.addMessage(chatMessage, true);
    }

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
    public void onBackPressed() {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //etMessage.requestFocus();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    @OnClick({R.id.iv_image, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_image:
                emojiPopup.toggle();
               // sendMessage();
                break;
            case R.id.btn_send:
                String message = editText.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    sendMessage(message);
                    editText.setText("");
                }
                break;
        }
    }

    private class asksusi extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... query) {
            String response = null;
            if (isNetworkConnected()) {
                try {
                    JSONObject json = JsonIO.loadJson("http://api.asksusi.com/susi/chat.json?q=" + URLEncoder.encode(query[0], "UTF-8"));
                    response = json.getJSONArray("answers").getJSONObject(0).getJSONArray("actions").getJSONObject(0).getString("expression");
                } catch (JSONException | UnsupportedEncodingException e) {
                    response = "error: " + e.getMessage();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Internet Connection Not Available!!", Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
            ChatMessage chatMessage = new ChatMessage(response, false, false);
            recyclerAdapter.addMessage(chatMessage, true);
        }

        private boolean isNetworkConnected() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        }
    }
   // @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification = "Sample app we do not care")
    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
            @Override
            public void onEmojiBackspaceClicked(final View v) {
            //    Log.d("MainActivity", "Clicked on Backspace");
            }
        }).setOnEmojiClickedListener(new OnEmojiClickedListener() {
            @Override
            public void onEmojiClicked(final Emoji emoji) {
               // Log.d("MainActivity", "Clicked on emoji");
            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                ivImage.setImageResource(R.drawable.ic_avatar);
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {
                Log.d("MainActivity", "Opened soft keyboard");
            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                ivImage.setImageResource(R.drawable.ic_avatar);
            }
        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).build(editText);
    }

}