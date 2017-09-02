package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.SystemClock.sleep;

/**
 * Created by meeera on 31/8/17.
 */

public class TableViewHolder extends MessageViewHolder {
    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;
    @BindView(R.id.timestamp)
    public TextView timeStamp;
    @BindView(R.id.thumbs_up)
    protected ImageView thumbsUp;
    @Nullable
    @BindView(R.id.thumbs_down)
    protected ImageView thumbsDown;
    private ChatMessage model;
    private Context currContext;

    public TableViewHolder(View itemView, ClickListener clickListener) {
        super(itemView, clickListener);
        ButterKnife.bind(this, itemView);
    }

    public void setView(final ChatMessage model, final Context currContext){
        this.model = model;
        this.currContext = currContext;
        int columnSize = model.getTableColumns().size();
        int dataSize = model.getTableDatas().size();
        List<String> data = new ArrayList<>();
        List<String> column = new ArrayList<>();

        for(int columnflag = 0; columnflag<columnSize;columnflag++) {
            column.add( model.getTableColumns().get(columnflag).getColumnName());
        }

        int dataflag = 0;
        while(dataflag<dataSize) {
            data.add(model.getTableDatas().get(dataflag).getTableData());
            dataflag++;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(currContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(currContext,
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        TableAdapter tableadapter = new TableAdapter(column, data);
        recyclerView.setAdapter(tableadapter);

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
        timeStamp.setText(model.getTimeStamp());

    }

    @OnClick(R.id.thumbs_up)
    public void thumbsUpClick() {
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

    @OnClick(R.id.thumbs_down)
    public void thumbsDownClick() {
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
                        default:
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
                    default:
                }
                Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show();
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
}
