package org.fossasia.susi.ai.adapters.recyclerAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.susi.ai.ChatMessage;
import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.viewHolders.ChatViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * --Vatsal Bajpai on
 * --25/09/16 at
 * --9:49 PM
 */

public class ChatFeedRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    public static final int MY_MESSAGE = 0;
    public static final int OTHER_MESSAGE = 1;
    public static final int MY_IMAGE = 2;
    public static final int OTHER_IMAGE = 3;

    private Context currContext;
    private List<ChatMessage> itemList;
    private Activity activity;
    private String TAG = ChatFeedRecyclerAdapter.class.getSimpleName();
    private RecyclerView recyclerView;

    public ChatFeedRecyclerAdapter(Activity activity, Context curr_context, List<ChatMessage> itemList) {
        this.itemList = itemList;
        this.currContext = curr_context;
        this.activity = activity;
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

    public void addMessage(ChatMessage chatMessage, boolean shouldScrollToBottom) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.add(chatMessage);
        notifyItemInserted(itemList.size() - 1);
        if (recyclerView != null && shouldScrollToBottom) {
            recyclerView.smoothScrollToPosition(itemList.size() - 1);
        }
    }

    public void addMessage(ChatMessage chatMessage) {
        addMessage(chatMessage, false);
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view;
        switch (viewType) {
            case MY_MESSAGE:
                view = inflater.inflate(R.layout.item_mine_message, viewGroup, false);
                return new ChatViewHolder(view, MY_MESSAGE);
            case OTHER_MESSAGE:
                view = inflater.inflate(R.layout.item_other_message, viewGroup, false);
                return new ChatViewHolder(view, OTHER_MESSAGE);
            case MY_IMAGE:
                view = inflater.inflate(R.layout.item_mine_image, viewGroup, false);
                return new ChatViewHolder(view, MY_IMAGE);
            case OTHER_IMAGE:
                view = inflater.inflate(R.layout.item_other_image, viewGroup, false);
                return new ChatViewHolder(view, OTHER_IMAGE);
            default:
                view = inflater.inflate(R.layout.item_mine_message, viewGroup, false);
                return new ChatViewHolder(view, MY_MESSAGE);
        }

    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = itemList.get(position);

        if (item.isMine() && !item.isImage()) return MY_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return OTHER_MESSAGE;
        else if (item.isMine() && item.isImage()) return MY_IMAGE;
        else return OTHER_IMAGE;
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
                    case MY_MESSAGE:
                        chatViewHolder.chatTextView.setText(model.getContent());
                        chatViewHolder.chatTextView.setTag(chatViewHolder);
                        break;
                    case OTHER_MESSAGE:
                        chatViewHolder.chatTextView.setText(model.getContent());
                        chatViewHolder.chatTextView.setTag(chatViewHolder);
                        break;
                    case MY_IMAGE:
                    case OTHER_IMAGE:
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

    class clickHandler implements View.OnClickListener {

        int position;
        ChatMessage ChatMessage;

        public clickHandler(int position, ChatMessage ChatMessage) {
            this.ChatMessage = ChatMessage;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                default:
                    Toast.makeText(currContext, "onClick", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}