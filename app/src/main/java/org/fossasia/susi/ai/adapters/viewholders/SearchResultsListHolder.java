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
 * <h1>Search result list view holder</h1>
 *
 * Created by saurabh on 19/11/16.
 */
public class SearchResultsListHolder extends MessageViewHolder {

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;

    /**
     * Instantiates a new Search results list holder.
     *
     * @param itemView the item view
     * @param listener the listener
     */
    public SearchResultsListHolder(View itemView, ClickListener listener) {
        super(itemView,listener);
        ButterKnife.bind(this, itemView);
    }
}
