package org.fossasia.susi.ai.adapters.recycleradapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import java.util.List;

/**
 * Adapter for displaying list of actions in a dialog box when message is long pressed.
 *
 * Created by chiragw15 on 31/7/17.
 */

public class SelectionDialogListAdapter extends ArrayAdapter<Pair<String,Drawable>> {

    private final List<Pair<String,Drawable>> list;
    private final Activity context;

    static class ViewHolder {
        TextView option;
        ImageView icon;
    }

    public SelectionDialogListAdapter(Context context, List<Pair<String,Drawable>> list) {
        super(context, R.layout.item_selection_dialog_list, list);
        this.context = (Activity) context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.item_selection_dialog_list, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.option = (TextView) view.findViewById(R.id.option);
            viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.option.setText(list.get(position).first);
        holder.icon.setImageDrawable(list.get(position).second);
        return view;
    }
}
