package org.fossasia.susi.ai.adapters.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.text)
    public TextView chatTextView;

    public ChatViewHolder(View view, int myMessage) {
        super(view);
        ButterKnife.bind(this, view);
        switch (myMessage) {
            case MY_MESSAGE:
                break;
            case OTHER_MESSAGE:
                break;
            case MY_IMAGE:
            case OTHER_IMAGE:
            default:
        }

    }
}