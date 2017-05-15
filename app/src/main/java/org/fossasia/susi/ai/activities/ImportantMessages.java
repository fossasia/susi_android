package org.fossasia.susi.ai.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.model.ChatMessage;

import io.realm.Realm;
import io.realm.RealmResults;

public class ImportantMessages extends AppCompatActivity {

    private Realm realm;
    private RecyclerView rvChatImportant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_messages);

        realm = Realm.getDefaultInstance();
        rvChatImportant = (RecyclerView) findViewById(R.id.rv_chat_important);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setChatBackground();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.susi);
        setupAdapter();
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

    private void setupAdapter() {
        rvChatImportant = (RecyclerView) findViewById(R.id.rv_chat_important);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvChatImportant.setLayoutManager(linearLayoutManager);
        rvChatImportant.setHasFixedSize(false);
        RealmResults<ChatMessage> importantMessages = realm.where(ChatMessage.class).equalTo("isImportant",true).findAll().sort("id");
        TextView tv_msg = (TextView) findViewById(R.id.tv_empty_list);

        if(importantMessages.size()!=0)
            tv_msg.setVisibility(View.INVISIBLE);
        else
            tv_msg.setVisibility(View.VISIBLE);

        ChatFeedRecyclerAdapter recyclerAdapter = new ChatFeedRecyclerAdapter(Glide.with(this), this, importantMessages, true);
        rvChatImportant.setAdapter(recyclerAdapter);
        rvChatImportant.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rvChatImportant.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int scrollTo = rvChatImportant.getAdapter().getItemCount() - 1;
                            scrollTo = scrollTo >= 0 ? scrollTo : 0;
                            rvChatImportant.scrollToPosition(scrollTo);
                        }
                    }, 10);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
