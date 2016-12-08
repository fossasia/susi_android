package org.fossasia.susi.ai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 * Created by better_clever on 12/10/16.
 */

public class WebSearchHolder extends MessageViewHolder{

    @BindView(R.id.web_text)
    public TextView webtext;
    @BindView(R.id.chatMessageView)
    public ChatMessageView chatMessageView;
    @BindView(R.id.web_link_preview_image)
    public ImageView webpreviewImageView;
    @BindView(R.id.web_link_preview_title)
    public TextView webtitleTextView;
    @BindView(R.id.web_link_preview_description)
    public TextView webdescriptionTextView;
    @BindView(R.id.timestamp)
    public TextView timestampTextView;
    @BindView(R.id.web_preview_layout)
    public LinearLayout webpreviewLayout;

    public WebSearchHolder(View itemView , ClickListener listener) {
        super(itemView, listener);
        ButterKnife.bind(this,itemView);
    }

}
