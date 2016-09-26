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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import org.loklak.android.tools.JsonIO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private FloatingActionButton mButtonSend;
	private EditText mEditTextMessage;
	private ImageView mImageView;

	private RecyclerView rvChatFeed;
	private CoordinatorLayout coordinatorLayout;
	private ChatFeedRecyclerAdapter recyclerAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
		rvChatFeed = (RecyclerView) findViewById(R.id.rv_chat_feed);
		mButtonSend = (FloatingActionButton) findViewById(R.id.btn_send);
		mEditTextMessage = (EditText) findViewById(R.id.et_message);
		mImageView = (ImageView) findViewById(R.id.iv_image);

		setupAdapter();

		mButtonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = mEditTextMessage.getText().toString();
				if (TextUtils.isEmpty(message)) {
					return;
				}
				sendMessage(message);
				mEditTextMessage.setText("");
			}
		});

		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});


	}

	private void setupAdapter() {

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

		rvChatFeed.setLayoutManager(linearLayoutManager);
		rvChatFeed.setHasFixedSize(true);
		List<ChatMessage> chatMessageList = new ArrayList<>();
		recyclerAdapter = new ChatFeedRecyclerAdapter(this, this, chatMessageList);

		rvChatFeed.setAdapter(recyclerAdapter);

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

	private class asksusi extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... query) {
			String response = null;
			if(isNetworkConnected()) {
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
			if(response == null){
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
}