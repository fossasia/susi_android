package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <h1>Zero height view holder</h1>
 * <p>
 * Created by mejariamol on 10/21/2016.
 */
public class ZeroHeightHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.chatMessageView)
    public RelativeLayout chatMessage;

    /**
     * Instantiates a new Zero height holder.
     *
     * @param itemView the item view
     */
    public ZeroHeightHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
