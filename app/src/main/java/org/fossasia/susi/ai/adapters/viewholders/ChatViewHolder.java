package org.fossasia.susi.ai.adapters.viewholders;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.data.model.ChatMessage;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.SystemClock.sleep;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.SUSI_IMAGE;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.SUSI_MESSAGE;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.USER_IMAGE;
import static org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter.USER_MESSAGE;

/**
 * <h1>Chat view holder</h1>
 *
 * Created by
 * --Vatsal Bajpai on
 * --25/09/16 at
 * --9:51 PM
 */
public class ChatViewHolder extends MessageViewHolder {

    @BindView(R.id.text)
    public TextView chatTextView;
    @BindView(R.id.timestamp)
    public TextView timeStamp;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;
    @Nullable
    @BindView(R.id.received_tick)
    public ImageView receivedTick;
    @Nullable
    @BindView(R.id.thumbs_up)
    protected ImageView thumbsUp;
    @Nullable
    @BindView(R.id.thumbs_down)
    protected ImageView thumbsDown;

    private ChatMessage model;
    /**
     * Instantiates a new Chat view holder.
     *
     * @param view          the view
     * @param clickListener the click listener
     * @param myMessage     the my message
     */
    public ChatViewHolder(View view, ClickListener clickListener, int myMessage) {
        super(view, clickListener);
        ButterKnife.bind(this, view);
        switch (myMessage) {
            case USER_MESSAGE:
                break;
            case SUSI_MESSAGE:
                break;
            case USER_IMAGE:
            case SUSI_IMAGE:
            default:
        }
    }

    /**
     * Inflate ChatView
     *
     * @param model the ChatMessage object
     * @param viewType the viewType
     */
    public void setView(final ChatMessage model, int viewType, final Context context) {
        if (model != null) {
            this.model = model;
            try {
                switch (viewType) {
                    case USER_MESSAGE:
                        chatTextView.setText(model.getContent());
                        timeStamp.setText(model.getTimeStamp());
                        if (model.getIsDelivered())
                            receivedTick.setImageResource(R.drawable.ic_check);
                        else
                            receivedTick.setImageResource(R.drawable.ic_clock);

                        chatTextView.setTag(this);
                        timeStamp.setTag(this);
                        receivedTick.setTag(this);
                        break;
                    case SUSI_MESSAGE:
                        Spanned answerText;
                        if(model.getActionType().equals(Constant.ANCHOR)) {
                            chatTextView.setLinksClickable(true);
                            chatTextView.setMovementMethod(LinkMovementMethod.getInstance());
                        } else {
                            chatTextView.setLinksClickable(false);
                        }
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            answerText = Html.fromHtml(model.getContent(), Html.FROM_HTML_MODE_COMPACT);
                        } else {
                            answerText = Html.fromHtml(model.getContent());
                        }

                        if(model.getSkillLocation().isEmpty()){
                            thumbsUp.setVisibility(View.GONE);
                            thumbsDown.setVisibility(View.GONE);
                        } else {
                            thumbsUp.setVisibility(View.VISIBLE);
                            thumbsDown.setVisibility(View.VISIBLE);
                        }

                        if(model.isPositiveRated()){
                            thumbsUp.setImageResource(R.drawable.thumbs_up_solid);
                        } else {
                            thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
                        }

                        if(model.isNegativeRated()){
                            thumbsDown.setImageResource(R.drawable.thumbs_down_solid);
                        } else {
                            thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
                        }

                        chatTextView.setText(answerText);
                        timeStamp.setText(model.getTimeStamp());
                        chatTextView.setTag(this);
                        timeStamp.setTag(this);

                        thumbsUp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                thumbsUp.setImageResource(R.drawable.thumbs_up_solid);
                                if(!model.isPositiveRated() && !model.isNegativeRated()) {
                                    rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), context);
                                    setRating(true, true);
                                } else if(!model.isPositiveRated() && model.isNegativeRated()) {
                                    setRating(false, false);
                                    thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
                                    rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), context);
                                    sleep(500);
                                    rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), context);
                                    setRating(true, true);
                                } else if (model.isPositiveRated() && !model.isNegativeRated()) {
                                    rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), context);
                                    setRating(false, true);
                                    thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
                                }
                            }
                        });

                        thumbsDown.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                thumbsDown.setImageResource(R.drawable.thumbs_down_solid);
                                if(!model.isPositiveRated() && !model.isNegativeRated()) {
                                    rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), context);
                                    setRating(true, false);
                                } else if(model.isPositiveRated() && !model.isNegativeRated()) {
                                    setRating(false, true);
                                    thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
                                    rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), context);
                                    sleep(500);
                                    rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), context);
                                    setRating(true, false);
                                } else if (!model.isPositiveRated() && model.isNegativeRated()) {
                                    rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), context);
                                    setRating(false, false);
                                    thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
                                }
                            }
                        });

                        break;
                    case USER_IMAGE:
                    case SUSI_IMAGE:
                    default:
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setRating(boolean what, boolean which) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if(which) {
            model.setPositiveRated(what);
        } else {
            model.setNegativeRated(what);
        }
        realm.commitTransaction();
    }

    private void rateSusiSkill(final String polarity, String locationUrl, final Context context) {

        final Map<String,String> susiLocation = ParseSusiResponseHelper.Companion.getSkillLocation(locationUrl);

        if(susiLocation.size() == 0)
            return;

        Call<SkillRatingResponse> call = new ClientBuilder().getSusiApi().rateSkill(susiLocation.get("model"),
                susiLocation.get("group"), susiLocation.get("language"), susiLocation.get("skill"), polarity);

        call.enqueue(new Callback<SkillRatingResponse>() {
            @Override
            public void onResponse(Call<SkillRatingResponse> call, Response<SkillRatingResponse> response) {
                if(!response.isSuccessful() || response.body() == null) {
                    switch(polarity) {
                        case Constant.POSITIVE:
                            thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
                            setRating(false, true);
                            break;
                        case Constant.NEGATIVE:
                            thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
                            setRating(false, false);
                            break;
                    }
                    Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SkillRatingResponse> call, Throwable t) {
                t.printStackTrace();
                switch(polarity) {
                    case Constant.POSITIVE:
                        thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
                        setRating(false, true);
                        break;
                    case Constant.NEGATIVE:
                        thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
                        setRating(false, false);
                        break;
                }
                Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show();
            }

        });
    }
}