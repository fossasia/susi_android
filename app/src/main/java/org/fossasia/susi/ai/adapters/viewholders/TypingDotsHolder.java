package org.fossasia.susi.ai.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.himanshusoni.chatmessageview.ChatMessageView;
import pl.tajchert.sample.DotsTextView;

/**
 * Created by mejariamol on 10/21/2016.
 */

public class TypingDotsHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.dots)
    public DotsTextView dotsTextView;
    @BindView(R.id.chatMessageView)
    public ChatMessageView chatMessage;

    public TypingDotsHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
