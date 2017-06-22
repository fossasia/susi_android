package org.fossasia.susi.ai.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.tajchert.sample.DotsTextView;

/**
 * <h1>Typing dots view holder</h1>
 *
 * Created by mejariamol on 10/21/2016.
 */
public class TypingDotsHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.dots)
    public DotsTextView dotsTextView;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;

    /**
     * Instantiates a new Typing dots holder.
     *
     * @param itemView the item view
     */
    public TypingDotsHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
