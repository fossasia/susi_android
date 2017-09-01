package org.fossasia.susi.ai.chat.adapters.recycleradapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.adapters.viewholders.HorizontalCellViewHolder;

import java.util.List;

/**
 * Created by meeera on 31/8/17.
 */

public class HorizontalTableAdapter extends RecyclerView.Adapter<HorizontalCellViewHolder>  {
    private List<String> data;
    private int vertpos;

    public HorizontalTableAdapter(List<String> data, int vertpos) {
        this.data = data;
        this.vertpos = vertpos;
    }

    @Override
    public HorizontalCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.hoz_view, parent, false);
        WindowManager wm = (WindowManager)parent.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        LinearLayout.LayoutParams PO = new LinearLayout.LayoutParams(metrics.widthPixels/data.size(), LinearLayout.LayoutParams.WRAP_CONTENT);
        v1.setLayoutParams(new RecyclerView.LayoutParams(PO));
        return new HorizontalCellViewHolder(v1);
    }

    @Override
    public void onBindViewHolder(HorizontalCellViewHolder holder, int position) {

        if(vertpos == 0) {
            holder.txt.setTextSize(18.0f);
            holder.txt.setTypeface(null, Typeface.BOLD);
        }
        holder.txt.setPadding(5,10,5,10);
        holder.txt.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        if(data == null)
            return 0;
        else
            return data.size();
    }
}
