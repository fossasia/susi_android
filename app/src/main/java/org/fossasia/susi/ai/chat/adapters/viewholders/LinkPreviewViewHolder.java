package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.squareup.picasso.Picasso;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper;
import org.fossasia.susi.ai.chat.adapters.recycleradapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.data.model.ChatMessage;
import org.fossasia.susi.ai.data.model.WebLink;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.SystemClock.sleep;
import static org.fossasia.susi.ai.chat.adapters.recycleradapters.ChatFeedRecyclerAdapter.USER_WITHLINK;

/**
 * <h1>Link preview view holder</h1>
 *
 * Created by better_clever on 12/10/16.
 */
public class LinkPreviewViewHolder extends MessageViewHolder{

    @BindView(R.id.text)
    public TextView text;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;
    @BindView(R.id.link_preview_image)
    public ImageView previewImageView;
    @BindView(R.id.link_preview_title)
    public TextView titleTextView;
    @BindView(R.id.link_preview_description)
    public TextView descriptionTextView;
    @BindView(R.id.timestamp)
    public TextView timestampTextView;
    @BindView(R.id.preview_layout)
    public LinearLayout previewLayout;
    @Nullable @BindView(R.id.received_tick)
    public ImageView receivedTick;
    @Nullable
    @BindView(R.id.thumbs_up)
    protected ImageView thumbsUp;
    @Nullable
    @BindView(R.id.thumbs_down)
    protected ImageView thumbsDown;

    private Realm realm;
    private String url;
    private String TAG = ChatFeedRecyclerAdapter.class.getSimpleName();
    private ChatMessage model;
    /**
     * Instantiates a new Link preview view holder.
     *
     * @param itemView the item view
     * @param listener the listener
     */
    public LinkPreviewViewHolder(View itemView , ClickListener listener) {
        super(itemView, listener);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this,itemView);
    }

    /**
     * Inflate Link Preview
     *
     * @param model the ChatMessage object
     * @param currContext the Context
     */
    public void setView(final ChatMessage model, int viewType, final Context currContext) {
        this.model = model;
        Spanned answerText;
        text.setLinksClickable(true);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            answerText = Html.fromHtml(model.getContent(), Html.FROM_HTML_MODE_COMPACT);
        } else {
            answerText = Html.fromHtml(model.getContent());
        }

        if (viewType == USER_WITHLINK) {
            if (model.getIsDelivered())
                receivedTick.setImageResource(R.drawable.ic_check);
            else
                receivedTick.setImageResource(R.drawable.ic_clock);
        }

        if (viewType != USER_WITHLINK) {
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

            thumbsUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    thumbsUp.setImageResource(R.drawable.thumbs_up_solid);
                    if(!model.isPositiveRated() && !model.isNegativeRated()) {
                        rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), currContext);
                        setRating(true, true);
                    } else if(!model.isPositiveRated() && model.isNegativeRated()) {
                        setRating(false, false);
                        thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
                        rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), currContext);
                        sleep(500);
                        rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), currContext);
                        setRating(true, true);
                    } else if (model.isPositiveRated() && !model.isNegativeRated()) {
                        rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), currContext);
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
                        rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), currContext);
                        setRating(true, false);
                    } else if(model.isPositiveRated() && !model.isNegativeRated()) {
                        setRating(false, true);
                        thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
                        rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), currContext);
                        sleep(500);
                        rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), currContext);
                        setRating(true, false);
                    } else if (!model.isPositiveRated() && model.isNegativeRated()) {
                        rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), currContext);
                        setRating(false, false);
                        thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
                    }
                }
            });

        }

        text.setText(answerText);
        timestampTextView.setText(model.getTimeStamp());
        if (model.getWebLinkData() == null) {
            LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                @Override
                public void onPre() {
                    previewImageView.setVisibility(View.GONE);
                    descriptionTextView.setVisibility(View.GONE);
                    titleTextView.setVisibility(View.GONE);
                    previewLayout.setVisibility(View.GONE);
                }

                @Override
                public void onPos(final SourceContent sourceContent, boolean b) {
                    if(!PrefManager.hasTokenExpired() || PrefManager.getBoolean(Constant.ANONYMOUS_LOGGED_IN, false)) {
                        realm.beginTransaction();
                        Realm realm = Realm.getDefaultInstance();
                        WebLink link = realm.createObject(WebLink.class);

                        if (sourceContent != null) {

                            if (!sourceContent.getDescription().isEmpty()) {
                                Log.d(TAG, "onPos: " + sourceContent.getDescription());
                                previewLayout.setVisibility(View.VISIBLE);
                                descriptionTextView.setVisibility(View.VISIBLE);
                                descriptionTextView.setText(sourceContent.getDescription());
                            }

                            if (!sourceContent.getTitle().isEmpty()) {
                                Log.d(TAG, "onPos: " + sourceContent.getTitle());
                                previewLayout.setVisibility(View.VISIBLE);
                                titleTextView.setVisibility(View.VISIBLE);
                                titleTextView.setText(sourceContent.getTitle());
                            }

                            link.setBody(sourceContent.getDescription());
                            link.setHeadline(sourceContent.getTitle());
                            link.setUrl(sourceContent.getUrl());
                            url = sourceContent.getFinalUrl();

                            final List<String> imageList = sourceContent.getImages();

                            if (imageList == null || imageList.size() == 0) {
                                previewImageView.setVisibility(View.GONE);
                                link.setImageURL("");
                            } else {
                                previewImageView.setVisibility(View.VISIBLE);
                                Picasso.with(currContext.getApplicationContext()).load(imageList.get(0))
                                        .fit().centerCrop()
                                        .into(previewImageView);
                                link.setImageURL(imageList.get(0));
                            }
                        }

                        model.setWebLinkData(link);
                        realm.copyToRealmOrUpdate(model);
                        realm.commitTransaction();
                    }
                }
            };

            if (model != null) {
                List<String> urlList = ChatFeedRecyclerAdapter.extractLinks(model.getContent());
                StringBuilder url = new StringBuilder(urlList.get(0));
                StringBuilder http = new StringBuilder("http://");
                StringBuilder https = new StringBuilder("https://");
                if (!(url.toString().startsWith(http.toString()) || url.toString().startsWith(https.toString()))) {
                    url = http.append(url.toString());
                }
                TextCrawler textCrawler = new TextCrawler();
                textCrawler.makePreview(linkPreviewCallback, url.toString());
            }
        } else {

            if(!model.getWebLinkData().getHeadline().isEmpty()) {
                Log.d(TAG, "onPos: " + model.getWebLinkData().getHeadline());
                titleTextView.setText(model.getWebLinkData().getHeadline());
            } else {
                titleTextView.setVisibility(View.GONE);
                Log.d(TAG, "handleItemEvents: " + "isEmpty");
            }

            if(!model.getWebLinkData().getBody().isEmpty()) {
                Log.d(TAG, "onPos: " + model.getWebLinkData().getHeadline());
                descriptionTextView.setText(model.getWebLinkData().getBody());
            } else {
                descriptionTextView.setVisibility(View.GONE);
                Log.d(TAG, "handleItemEvents: " + "isEmpty");
            }

            if(model.getWebLinkData().getHeadline().isEmpty() && model.getWebLinkData().getBody().isEmpty()) {
                previewLayout.setVisibility(View.GONE);
            }

            Log.i(TAG, model.getWebLinkData().getImageURL());
            if (!model.getWebLinkData().getImageURL().equals("")) {
                Picasso.with(currContext.getApplicationContext()).load(model.getWebLinkData().getImageURL())
                        .fit().centerCrop()
                        .into(previewImageView);
            } else {
                previewImageView.setVisibility(View.GONE);
            }

            url = model.getWebLinkData().getUrl();
        }

        previewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(currContext.getPackageManager()) != null) {
                    currContext.startActivity(intent);
                }
            }
        });
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
