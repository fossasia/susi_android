package me.himanshusoni.chatmessageview.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private final int MY_MESSAGE = 0, OTHER_MESSAGE = 1;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> data) {
        super(context, R.layout.item_mine_message, data);
    }

    @Override
    public int getViewTypeCount() {
        // my message, other message
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);

        if (item.isMine()) return MY_MESSAGE;
        else return OTHER_MESSAGE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_message, parent, false);
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text);

        textView.setText(getItem(position).getContent());

        return convertView;
    }
}
