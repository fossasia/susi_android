package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.IYoutubeVid;
import org.fossasia.susi.ai.chat.YoutubeVid;
import org.fossasia.susi.ai.data.model.ChatMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * @author mohitkumar
 * <p>
 * This viewholder is used to display the youtube video thumbnails
 */

public class YoutubeVideoViewHolder extends MessageViewHolder {

    @BindView(R.id.youtube_view)
    public ImageView playerView;
    @BindView(R.id.play_video)
    public ImageView playBtn;
    protected ChatMessage model;
    private String videoId;
    private IYoutubeVid youtubeVid;

    public YoutubeVideoViewHolder(View view, ClickListener clickListener) {
        super(view, clickListener);
        ButterKnife.bind(this, view);
    }

    public void setPlayerView(ChatMessage model) {
        this.model = model;

        if (model != null) {
            try {
                videoId = model.getIdentifier();
                String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg";

                Picasso.with(itemView.getContext()).load(img_url).
                        placeholder(ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_susi))
                        .into(playerView);
            } catch (Exception e) {
                Timber.e(e);
            }

        }

        youtubeVid = new YoutubeVid(itemView.getContext());
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youtubeVid.playYoutubeVid(videoId);
            }
        });
    }
}
