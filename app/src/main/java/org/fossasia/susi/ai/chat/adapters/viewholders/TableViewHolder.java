package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.content.Context;
import android.os.SystemClock;
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

/*
*   A ViewHolder that contains the recyclerview of the Table Response items.
*
* */

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
    private Context currContext;

    public TableViewHolder(View itemView, ClickListener clickListener) {
        super(itemView, clickListener);
        ButterKnife.bind(this, itemView);
    }

    public void setView(final ChatMessage model, final Context currContext) {
        // check if model is not null else there is no need to set view elements
        if (model != null) {

            // check if the size of the data list and the column list is not 0
            if (model.getTableColumns().size() != 0 || model.getTableData().size() != 0) {
                this.model = model;
                this.currContext = currContext;
                int columnSize = model.getTableColumns().size();
                int dataSize = model.getTableData().size();
                List<String> data = new ArrayList<>();
                List<String> column = new ArrayList<>();

                for (int col = 0; col < columnSize; col++) {
                    column.add(model.getTableColumns().get(col).getColumnName());
                }

                Timber.d("SIZE : " + Integer.toString(column.size()) + " : " + Integer.toString(data.size()));

                for (int dFlag = 0; dFlag < dataSize; dFlag++) {
                    data.add(model.getTableData().get(dFlag).getTableData());
                }


                // Set the layout manager for the recyclerview and and call the TableAdapter to attach the recyclerview elements
                LinearLayoutManager layoutManager = new LinearLayoutManager(currContext, LinearLayoutManager.HORIZONTAL,
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
            thumbsDown.setVisibility(View.INVISIBLE);
        }

        if (model.isPositiveRated()) {
            thumbsUp.setImageResource(R.drawable.thumbs_up_solid);
        } else {
            thumbsDown.setImageResource(R.drawable.thumbs_down_solid);
        }
        timeStamp.setText(model.getTimeStamp());

    }

    @OnClick
    public void thumbsUpClick() {
        thumbsUp.setImageResource(R.drawable.thumbs_up_solid);
        if (!model.isPositiveRated() && !model.isNegativeRated()) {
            rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), currContext);
            setRating(true, true);
        } else if (model.isPositiveRated() && !model.isNegativeRated()) {
            setRating(false, true);
            thumbsUp.setImageResource(R.drawable.thumbs_up_outline);
            rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), currContext);
            SystemClock.sleep(500);
            rateSusiSkill(Constant.NEGATIVE, model.getSkillLocation(), currContext);
            setRating(true, false);
        } else if (!model.isPositiveRated() && model.isNegativeRated()) {
            rateSusiSkill(Constant.POSITIVE, model.getSkillLocation(), currContext);
            setRating(false, false);
            thumbsDown.setImageResource(R.drawable.thumbs_down_outline);
        }
    }

    // a function to rate the susi skill
    private void rateSusiSkill(final String polarity, String locationUrl, final Context context) {

        final Map<String, String> susiLocation = ParseSusiResponseHelper.Companion.getSkillLocation(locationUrl);

        if (susiLocation.size() == 0) {
            return;
        }

        Call<SkillRatingResponse> call = new ClientBuilder().getSusiApi().rateSkill(susiLocation.get("model"),
                susiLocation.get("group"), susiLocation.get("language"), susiLocation.get("skill"), polarity);

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
                }
            }

            @Override
            public void onFailure(Call<SkillRatingResponse> call, Throwable t) {
                t.printStackTrace();
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
    private void setRating(boolean what, boolean which) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if (which) {
            model.setPositiveRated(what);
        } else {
            model.setNegativeRated(what);
        }
        realm.commitTransaction();
    }

}