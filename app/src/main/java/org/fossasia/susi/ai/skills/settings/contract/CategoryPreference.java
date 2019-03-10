package org.fossasia.susi.ai.skills.settings.contract;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.FrameLayout;
import org.fossasia.susi.ai.R;
class CustomPreference extends PreferenceCategory {
    public CustomPreference(Context context){ super(context); }
    public CustomPreference(Context context, AttributeSet attrs) { super(context, attrs); }
    public CustomPreference(Context context, AttributeSet attrs,int defyStlye) { super(context, attrs,defyStlye); }
    public void onBindViewHolder(PreferenceViewHolder holder) { super.onBindViewHolder(holder);
        TextView title = (TextView) holder.itemView;
        title.setTextColor(getContext().getResources().getColor(R.color.md_blue_A400)); }}

