package org.fossasia.susi.ai.skills.feedback.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by arundhati24 on 27/06/2018
 */

public class AllReviewsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.itemFeedback)
    public LinearLayout itemFeedback;

    @BindView(R.id.tvInitials)
    public TextView initials;

    @BindView(R.id.tvEmail)
    public TextView feedbackEmail;

    @BindView(R.id.tvDate)
    public TextView feedbackDate;

    @BindView(R.id.tvFeedback)
    public TextView feedback;

    public AllReviewsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
