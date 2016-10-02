package org.fossasia.susi.ai.adapters.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.USER_IMAGE;
import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.USER_MESSAGE;
import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.SUSI_IMAGE;
import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.SUSI_MESSAGE;

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
            case USER_MESSAGE:
                break;
            case SUSI_MESSAGE:
                break;
            case USER_IMAGE:
            case SUSI_IMAGE:
            default:
        }

    }
}