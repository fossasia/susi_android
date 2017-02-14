package org.fossasia.susi.ai.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.tajchert.sample.DotsTextView;

/**
 * Created by mejariamol on 10/21/2016.
 */

public class TypingDotsHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.dots)
    public DotsTextView dotsTextView;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;

    public TypingDotsHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
