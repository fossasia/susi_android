package org.fossasia.susi.ai.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.himanshusoni.chatmessageview.ChatMessageView;

import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.SUSI_IMAGE;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.SUSI_MESSAGE;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.USER_IMAGE;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.USER_MESSAGE;

/**
 * Created by
 * --Vatsal Bajpai on
 * --25/09/16 at
 * --9:51 PM
 */

public class ChatViewHolder extends MessageViewHolder{

    @BindView(R.id.text)
    public TextView chatTextView;
    @BindView(R.id.timestamp)
    public TextView timeStamp;
    @BindView(R.id.chatMessageView)
    public ChatMessageView chatMessageView;


    public ChatViewHolder(View view, ClickListener clickListener ,int myMessage) {
        super(view,clickListener);
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