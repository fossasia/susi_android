package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper;
import org.fossasia.susi.ai.data.model.ChatMessage;
import org.fossasia.susi.ai.dataclasses.SkillRatingQuery;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse;

import java.util.Map;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/*
 *   Created by batbrain7 on 27/06/18
 *
 *   A ViewHolder class for all that displays all the image responses.
 */

public class ImageViewHolder extends MessageViewHolder {
    /**
     * Instantiates a new Message view holder.
     *
     * @param itemView      the item view
     * @param clickListener the click listener
     */

    @BindView(R.id.image_response)
    public ImageView imageView;
    @BindView(R.id.timestamp)
    public TextView timeStamp;
    @BindView(R.id.thumbs_up)
    public ImageView thumbsUp;
    @Nullable
    @BindView(R.id.thumbs_down)
    public ImageView thumbsDown;
    private ChatMessage model;
    private String imageURL;

    public ImageViewHolder(View itemView, ClickListener clickListener) {
        super(itemView, clickListener);
        ButterKnife.bind(this, itemView);
    }

    public void setView(final ChatMessage model) {
        this.model = model;

        if (model != null) {
            imageURL = model.getContent();
            try {
                Picasso.get()
                        .load(imageURL)
                        .placeholder(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_susi))
                        .into(imageView);
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(itemView.getContext(), Uri.parse(imageURL));
            }
        });

        if (model.getSkillLocation().isEmpty()) {
            thumbsUp.setVisibility(View.GONE);
            thumbsDown.setVisibility(View.GONE);
        } else {
            thumbsUp.setVisibility(View.VISIBLE);
            thumbsDown.setVisibility(View.VISIBLE);
        }

        if (model.isPositiveRated() || model.isNegativeRated()) {
            thumbsUp.setVisibility(View.GONE);
            thumbsDown.setVisibility(View.GONE);
        } else {
            thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
            thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
        }

        timeStamp.setText(model.getTimeStamp());

        thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d(model.isPositiveRated() + " " + model.isNegativeRated());
                if (!model.isPositiveRated() && !model.isNegativeRated()) {
                    thumbsUp.setImageResource(R.drawable.thumbs_up_solid);
                    rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), itemView.getContext());
                    setRating(true, true);
                }
            }
        });

        thumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d(model.isPositiveRated() + " " + model.isNegativeRated());
                if (!model.isPositiveRated() && !model.isNegativeRated()) {
                    thumbsDown.setImageResource(R.drawable.thumbs_down_solid);
                    rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), itemView.getContext());
                    setRating(true, false);
                }
            }
        });
    }

    // a function to rate the susi skill
    private void rateSusiSkill(final String polarity, String locationUrl, final Context context) {
        final Map<String, String> susiLocation = ParseSusiResponseHelper.Companion.getSkillLocation(locationUrl);

        if (susiLocation.isEmpty()) {
            return;
        }

        SkillRatingQuery queryObject = new SkillRatingQuery(susiLocation.get("model"), susiLocation.get("group"),
                susiLocation.get("language"), susiLocation.get("skill"), polarity);

        Call<SkillRatingResponse> ratingResponseCall = ClientBuilder.rateSkillCall(queryObject);

        ratingResponseCall.enqueue(new Callback<SkillRatingResponse>() {
            @Override
            public void onResponse(Call<SkillRatingResponse> responseCall, Response<SkillRatingResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    updateRating(polarity);
                    Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show();
                } else {
                    Timber.d("Rating successful");
                }
            }

            @Override
            public void onFailure(Call<SkillRatingResponse> responseCall, Throwable t) {
                Timber.e(t);
                updateRating(polarity);
                Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // function to set the rating in the database
    private void setRating(boolean rating, boolean thumbsUp) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if (thumbsUp) {
            model.setPositiveRated(rating);
        } else {
            model.setNegativeRated(rating);
        }
        realm.commitTransaction();
    }

    private void updateRating(String polarity) {
        switch (polarity) {
            case Constant.POSITIVE:
                thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
                setRating(false, true);
                break;
            case Constant.NEGATIVE:
                thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
                setRating(false, false);
                break;
            default:
        }
    }

}
