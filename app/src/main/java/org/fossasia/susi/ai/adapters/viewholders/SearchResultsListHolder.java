package org.fossasia.susi.ai.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saurabh on 19/11/16.
 */

public class SearchResultsListHolder extends MessageViewHolder {

    @BindView(R.id.text)
    public TextView message;
    @BindView(R.id.timestamp)
    public TextView timeStamp;
    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;
    @BindView(R.id.message_star)
    public ImageView messageStar;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;

    public SearchResultsListHolder(View itemView, ClickListener listener) {
        super(itemView,listener);
        ButterKnife.bind(this, itemView);
    }
}
