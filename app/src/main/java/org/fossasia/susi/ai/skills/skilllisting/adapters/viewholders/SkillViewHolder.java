package org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by chiragw15 on 18/8/17.
 */

public class SkillViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.skill_preview_image)
    public ImageView previewImageView;
    @BindView(R.id.skill_preview_title)
    public TextView skillPreviewTitle;
    @BindView(R.id.skill_preview_description)
    public TextView skillPreviewDescription;
    @BindView(R.id.skill_preview_example)
    public TextView skillPreviewExample;

    private ClickListener listener;

    public SkillViewHolder(View itemView, ClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listener = clickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onItemClicked(getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onItemClicked(int position);
    }
}
