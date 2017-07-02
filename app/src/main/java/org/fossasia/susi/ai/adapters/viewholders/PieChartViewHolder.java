package org.fossasia.susi.ai.adapters.viewholders;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.model.ChatMessage;
import org.fossasia.susi.ai.rest.responses.susi.Datum;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

/**
 * <h1>Pie chart view holder</h1>
 */
public class PieChartViewHolder extends MessageViewHolder {

    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;
    @BindView(R.id.text)
    public TextView chatTextView;
    @BindView(R.id.piechart)
    public PieChart pieChart;
    @BindView(R.id.timestamp)
    public TextView timeStamp;


    /**
     * Instantiates a new Pie chart view holder.
     *
     * @param view     the view
     * @param listener the listener
     */
    public PieChartViewHolder(View view, ClickListener listener) {
        super(view, listener);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Inflate PieChart
     *
     * @param model the ChatMessage object
     */
    public void setView(ChatMessage model) {
        if (model != null) {
            try {
                chatTextView.setText(model.getContent());
                timeStamp.setText(model.getTimeStamp());
                pieChart.setUsePercentValues(true);
                pieChart.setDrawHoleEnabled(true);
                pieChart.setHoleRadius(7);
                pieChart.setTransparentCircleRadius(10);
                pieChart.setRotationEnabled(true);
                pieChart.setRotationAngle(0);
                pieChart.setDragDecelerationFrictionCoef(0.001f);
                pieChart.getLegend().setEnabled(false);
                pieChart.setDescription("");
                RealmList<Datum> datumList = model.getDatumRealmList();
                final ArrayList<Entry> yVals = new ArrayList<>();
                final ArrayList<String> xVals = new ArrayList<>();
                for (int i = 0; i < datumList.size(); i++) {
                    yVals.add(new Entry(datumList.get(i).getPercent(), i));
                    xVals.add(datumList.get(i).getPresident());
                }
                pieChart.setClickable(false);
                pieChart.setHighlightPerTapEnabled(false);
                PieDataSet dataSet = new PieDataSet(yVals, "");
                dataSet.setSliceSpace(3);
                dataSet.setSelectionShift(5);
                ArrayList<Integer> colors = new ArrayList<>();
                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);
                for (int c : ColorTemplate.JOYFUL_COLORS)
                    colors.add(c);
                for (int c : ColorTemplate.COLORFUL_COLORS)
                    colors.add(c);
                for (int c : ColorTemplate.LIBERTY_COLORS)
                    colors.add(c);
                for (int c : ColorTemplate.PASTEL_COLORS)
                    colors.add(c);
                dataSet.setColors(colors);
                PieData data = new PieData(xVals, dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.GRAY);
                pieChart.setData(data);
                pieChart.highlightValues(null);
                pieChart.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
