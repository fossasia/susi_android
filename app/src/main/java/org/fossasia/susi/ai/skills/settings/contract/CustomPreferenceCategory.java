package org.fossasia.susi.ai.skills.settings.contract;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

public class CustomPreferenceCategory extends PreferenceCategory {

    @TargetApi(21)
    public CustomPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    public CustomPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    public CustomPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public CustomPreferenceCategory(Context context) {
        super(context);
        this.init(context, (AttributeSet) null);
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView title = (TextView) holder.itemView;
        title.setTextColor(Color.BLUE);
    }

    private void init(Context context, AttributeSet attrs) {
        this.setLayoutResource(R.layout.preference_category);
    }
}
