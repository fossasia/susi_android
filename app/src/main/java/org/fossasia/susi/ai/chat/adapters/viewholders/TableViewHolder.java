package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper;
import org.fossasia.susi.ai.chat.adapters.recycleradapters.TableAdapter;
import org.fossasia.susi.ai.data.model.ChatMessage;
import org.fossasia.susi.ai.data.model.TableColumn;
import org.fossasia.susi.ai.data.model.TableData;
import org.fossasia.susi.ai.dataclasses.SkillRatingQuery;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse;

import java.util.ArrayList;
import java.util.List;
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

import static android.os.SystemClock.sleep;

/**
 * A ViewHolder that contains the recyclerview of the Table Response items.
 */

public class TableViewHolder extends MessageViewHolder {

    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;
    @BindView(R.id.timestamp)
    public TextView timeStamp;
    @BindView(R.id.thumbs_up)
    public ImageView thumbsUp;
    @Nullable
    @BindView(R.id.thumbs_down)
    public ImageView thumbsDown;
    private ChatMessage model;

    public TableViewHolder(View itemView, ClickListener clickListener) {
        super(itemView, clickListener);
        ButterKnife.bind(this, itemView);
    }

    public void setView(final ChatMessage model) {
        this.model = model;
        // check if model is not null else there is no need to set view elements
        if (model != null) {
            // check if the size of the data list and the column list is not 0
            if (model.getTableColumns().size() > 0 || model.getTableData().size() > 0) {
                List<String> data = new ArrayList<>();
                List<String> column = new ArrayList<>();

                for (TableColumn col : model.getTableColumns()) {
                    column.add(col.getColumnName());
                }

                Timber.d("SIZE : " + Integer.toString(column.size()) + " : " + Integer.toString(data.size()));

                for (TableData tableData : model.getTableData()) {
                    data.add(tableData.getTableData());
                }

                // Set the layout manager for the recyclerview and and call the TableAdapter to attach the recyclerview elements
                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL,
                        false);
                recyclerView.setLayoutManager(layoutManager);
                TableAdapter tableAdapter = new TableAdapter(column, data);
                recyclerView.setAdapter(tableAdapter);
            } else {
                recyclerView.setLayoutManager(null);
                recyclerView.setAdapter(null);
            }
        } else {
            recyclerView.setLayoutManager(null);
            recyclerView.setAdapter(null);
        }

        if (model.getSkillLocation().isEmpty()) {
            thumbsUp.setVisibility(View.GONE);
            thumbsDown.setVisibility(View.GONE);
        } else {
            thumbsUp.setVisibility(View.VISIBLE);
            thumbsDown.setVisibility(View.VISIBLE);
        }

        if (model.isPositiveRated()) {
            thumbsUp.setImageResource(R.drawable.thumbs_up_solid);
        } else {
            thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
        }

        if (model.isNegativeRated()) {
            thumbsDown.setImageResource(R.drawable.thumbs_down_solid);
        } else {
            thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
        }

        timeStamp.setText(model.getTimeStamp());
        timeStamp.setTag(this);

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

        if (susiLocation.size() == 0) {
            return;
        }

        SkillRatingQuery queryObject = new SkillRatingQuery(susiLocation.get("model"), susiLocation.get("group"),
                susiLocation.get("language"), susiLocation.get("skill"), polarity);

        Call<SkillRatingResponse> call = ClientBuilder.rateSkillCall(queryObject);

        call.enqueue(new Callback<SkillRatingResponse>() {
            @Override
            public void onResponse(Call<SkillRatingResponse> call, Response<SkillRatingResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
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
                    Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show();
                } else {
                    Timber.d("Response successful");
                }
            }

            @Override
            public void onFailure(Call<SkillRatingResponse> call, Throwable t) {
                Timber.d(t);
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

}