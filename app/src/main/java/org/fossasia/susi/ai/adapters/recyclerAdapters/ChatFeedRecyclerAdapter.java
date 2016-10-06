package org.fossasia.susi.ai.adapters.recyclerAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewHolders.ChatViewHolder;
import org.fossasia.susi.ai.model.ChatMessage;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by
 * --Vatsal Bajpai on
 * --25/09/16 at
 * --9:49 PM
 */

public class ChatFeedRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    public static final int USER_MESSAGE = 0;
    public static final int SUSI_MESSAGE = 1;
    public static final int USER_IMAGE = 2;
    public static final int SUSI_IMAGE = 3;

    private Context currContext;
    private RealmResults<ChatMessage> itemList;
    private Activity activity;
    private String TAG = ChatFeedRecyclerAdapter.class.getSimpleName();
    private RecyclerView recyclerView;

    public ChatFeedRecyclerAdapter(Activity activity, Context curr_context, RealmResults<ChatMessage> itemList) {
        this.itemList = itemList;
        this.currContext = curr_context;
        this.activity = activity;
        itemList.addChangeListener(new RealmChangeListener<RealmResults<ChatMessage>>() {
            @Override
            public void onChange(RealmResults<ChatMessage> element) {
                notifyItemInserted(ChatFeedRecyclerAdapter.this.itemList.size() - 1);
                if (recyclerView != null) {
                    recyclerView.smoothScrollToPosition(ChatFeedRecyclerAdapter.this.itemList.size() - 1);
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }


    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

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
            default:
                view = inflater.inflate(R.layout.item_user_message, viewGroup, false);
                return new ChatViewHolder(view, USER_MESSAGE);
        }

    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = itemList.get(position);

        if (item.isMine() && !item.isImage()) return USER_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return SUSI_MESSAGE;
        else if (item.isMine() && item.isImage()) return USER_IMAGE;
        else return SUSI_IMAGE;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        handleItemEvents(holder, position);
    }

    private void handleItemEvents(final ChatViewHolder chatViewHolder, final int position) {

        final ChatMessage model = itemList.get(position);
        if (model != null) {
            try {
                switch (getItemViewType(position)) {
                    case USER_MESSAGE:
                        chatViewHolder.chatTextView.setText(model.getContent());
                        chatViewHolder.timeStamp.setText(model.getTimeStamp());
                        chatViewHolder.chatTextView.setTag(chatViewHolder);
                        chatViewHolder.timeStamp.setTag(chatViewHolder);
                        break;
                    case SUSI_MESSAGE:
                        chatViewHolder.chatTextView.setText(model.getContent());
                        chatViewHolder.timeStamp.setText(model.getTimeStamp());
                        chatViewHolder.chatTextView.setTag(chatViewHolder);
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

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}