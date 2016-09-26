package org.fossasia.susi.ai.adapters.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.MY_IMAGE;
import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.MY_MESSAGE;
import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.OTHER_IMAGE;
import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.OTHER_MESSAGE;

/**
 * Created by
 * --Vatsal Bajpai on
 * --25/09/16 at
 * --9:51 PM
 */

public class ChatViewHolder extends RecyclerView.ViewHolder {

    public TextView chatTextView;

    public ChatViewHolder(View view, int myMessage) {
        super(view);

        switch (myMessage) {
            case MY_MESSAGE:
                chatTextView = (TextView) view.findViewById(R.id.text);
                break;
            case OTHER_MESSAGE:
                chatTextView = (TextView) view.findViewById(R.id.text);
                break;
            case MY_IMAGE:
            case OTHER_IMAGE:
            default:
        }

    }
}