package org.fossasia.susi.ai.skills.groupwiseskills.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by arundhati24 on 16/07/2018.
 */
public class SkillViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.skill_image)
    public ImageView skillImage;
    @BindView(R.id.skill_name)
    public TextView skillName;
    @BindView(R.id.skill_author_name)
    public TextView skillAuthorName;
    @BindView(R.id.skill_example)
    public TextView skillExample;
    @BindView(R.id.skill_rating)
    public RatingBar skillRating;
    @BindView(R.id.skill_total_ratings)
    public TextView skillTotalRatings;

    public SkillViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
