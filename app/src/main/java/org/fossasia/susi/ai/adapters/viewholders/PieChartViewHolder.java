package org.fossasia.susi.ai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    public PieChartViewHolder(View view , ClickListener listener){
        super(view , listener);
        ButterKnife.bind(this, itemView);
    }
}
