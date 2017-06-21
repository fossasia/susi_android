package org.fossasia.susi.ai.adapters.viewholders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.SUSI_IMAGE;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.SUSI_MESSAGE;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.USER_IMAGE;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.USER_MESSAGE;

/**
 * <h1>Chat view holder</h1>
 *
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
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;
    @Nullable @BindView(R.id.received_tick)
    public ImageView receivedTick;

    /**
     * Instantiates a new Chat view holder.
     *
     * @param view          the view
     * @param clickListener the click listener
     * @param myMessage     the my message
     */
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