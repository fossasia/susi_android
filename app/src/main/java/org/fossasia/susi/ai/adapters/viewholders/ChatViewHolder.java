package org.fossasia.susi.ai.adapters.viewholders;

import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.data.model.ChatMessage;

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
public class ChatViewHolder extends MessageViewHolder {

    @BindView(R.id.text)
    public TextView chatTextView;
    @BindView(R.id.timestamp)
    public TextView timeStamp;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;
    @Nullable
    @BindView(R.id.received_tick)
    public ImageView receivedTick;


    /**
     * Instantiates a new Chat view holder.
     *
     * @param view          the view
     * @param clickListener the click listener
     * @param myMessage     the my message
     */
    public ChatViewHolder(View view, ClickListener clickListener, int myMessage) {
        super(view, clickListener);
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

    /**
     * Inflate ChatView
     *
     * @param model the ChatMessage object
     * @param viewType the viewType
     */
    public void setView(ChatMessage model, int viewType) {
        if (model != null) {
            try {
                switch (viewType) {
                    case USER_MESSAGE:
                        chatTextView.setText(model.getContent());
                        timeStamp.setText(model.getTimeStamp());
                        if (model.getIsDelivered())
                            receivedTick.setImageResource(R.drawable.ic_check);
                        else
                            receivedTick.setImageResource(R.drawable.ic_clock);

                        chatTextView.setTag(this);
                        timeStamp.setTag(this);
                        receivedTick.setTag(this);
                        break;
                    case SUSI_MESSAGE:
                        Spanned answerText;
                        if(model.getActionType().equals(Constant.ANCHOR)) {
                            chatTextView.setLinksClickable(true);
                            chatTextView.setMovementMethod(LinkMovementMethod.getInstance());
                        } else {
                            chatTextView.setLinksClickable(false);
                        }
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            answerText = Html.fromHtml(model.getContent(), Html.FROM_HTML_MODE_COMPACT);
                        } else {
                            answerText = Html.fromHtml(model.getContent());
                        }

                        chatTextView.setText(answerText);
                        timeStamp.setText(model.getTimeStamp());
                        chatTextView.setTag(this);
                        timeStamp.setTag(this);
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
}