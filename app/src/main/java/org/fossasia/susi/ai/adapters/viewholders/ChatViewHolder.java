package org.fossasia.susi.ai.adapters.viewholders;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.model.ChatMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;
    @Nullable @BindView(R.id.received_tick)
    public ImageView receivedTick;
    @BindView(R.id.message_star)
    public ImageView messageStar;


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

    public void setView(int type, int position, String query, int highlightMessagePosition, ChatMessage model, ChatViewHolder chatViewHolder) {
        if (model != null) {
            try {
                switch (type) {
                    case USER_MESSAGE:
                        messageStar.setVisibility( (model.isImportant()) ? View.VISIBLE : View.GONE);
                        chatTextView.setText(model.getContent());
                        timeStamp.setText(model.getTimeStamp());
                        if(model.getIsDelivered())
                            receivedTick.setImageResource(R.drawable.check);
                        else
                            receivedTick.setImageResource(R.drawable.clock);

                        chatTextView.setTag(chatViewHolder);
                        if (highlightMessagePosition == position) {
                            String text = chatTextView.getText().toString();
                            SpannableString modify = new SpannableString(text);
                            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(modify);
                            while (matcher.find()) {
                                int startIndex = matcher.start();
                                int endIndex = matcher.end();
                                modify.setSpan(new BackgroundColorSpan(Color.parseColor("#ffff00")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            chatTextView.setText(modify);

                        }
                        timeStamp.setTag(chatViewHolder);
                        receivedTick.setTag(chatViewHolder);
                        break;
                    case SUSI_MESSAGE:
                        Spanned answerText;
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            answerText = Html.fromHtml(model.getContent(),Html.FROM_HTML_MODE_COMPACT);
                        } else{
                            answerText = Html.fromHtml(model.getContent());
                        }

                        messageStar.setVisibility( (model.isImportant()) ? View.VISIBLE : View.GONE);
                        chatTextView.setText(answerText);
                        timeStamp.setText(model.getTimeStamp());
                        chatTextView.setTag(chatViewHolder);
                        if (highlightMessagePosition == position) {
                            String text = chatTextView.getText().toString();
                            SpannableString modify = new SpannableString(text);
                            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(modify);
                            while (matcher.find()) {
                                int startIndex = matcher.start();
                                int endIndex = matcher.end();
                                modify.setSpan(new BackgroundColorSpan(Color.parseColor("#ffff00")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            chatTextView.setText(modify);

                        }
                        timeStamp.setTag(chatViewHolder);
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