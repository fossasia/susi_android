package org.fossasia.susi.ai.skills.skilldetails.adapters.viewholders;

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

public class FeedbackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

    @BindView(R.id.seeAllReviews)
    public LinearLayout seeAllReviews;

    private FeedbackViewHolder.ClickListener listener;

    public FeedbackViewHolder(View itemView, FeedbackViewHolder.ClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listener = clickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position == 3) {
                listener.onItemClicked(position);
            }
        }
    }

    public interface ClickListener {
        void onItemClicked(int position);
    }
}
