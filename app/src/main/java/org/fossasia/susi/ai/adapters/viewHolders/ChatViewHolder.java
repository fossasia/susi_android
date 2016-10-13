package org.fossasia.susi.ai.adapters.viewHolders;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.model.ChatMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.himanshusoni.chatmessageview.ChatMessageView;

import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.SUSI_IMAGE;
import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.SUSI_MESSAGE;
import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.USER_IMAGE;
import static org.fossasia.susi.ai.adapters.recyclerAdapters.ChatFeedRecyclerAdapter.USER_MESSAGE;

/**
 Piyush Gupta on
 10/14/16 at
 12:59 PM
 */

public class ChatViewHolder extends RecyclerView.ViewHolder  implements View.OnLongClickListener{
    public  ChatViewHolder chatViewHolder;
    private String TAG = ChatViewHolder.class.getSimpleName();
    @BindView(R.id.text)
    public TextView chatTextView;
    @BindView(R.id.timestamp)
    public TextView timeStamp;
    @BindView(R.id.chatMessageView)
    public ChatMessageView chatMessage;


    public ChatViewHolder(View view, int myMessage) {
        super(view);
        ButterKnife.bind(this, view);
        //  chatViewHolder=(ChatViewHolder) view.getTag();
        view.setOnLongClickListener(this);
        switch (myMessage) {
            case USER_MESSAGE:
                break;
            case SUSI_MESSAGE:
                break;
            case USER_IMAGE:
            case SUSI_IMAGE:
            default:
        }

    }

    private void setClipboard(Context context, String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

    }

    @Override
    public boolean onLongClick(final View v) {

        final Context context=v.getContext();
        chatViewHolder=(ChatViewHolder)v.getTag();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Message");
        builder.setItems(new CharSequence[]
                        {"1. Copy Text", "2. Delete"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                String str = chatViewHolder.chatTextView.getText().toString();
                                setClipboard(context,str);

                                Snackbar.make(v, "Copied", Snackbar.LENGTH_LONG).show();

                                break;
                            case 1:
                                chatViewHolder.chatMessage.removeAllViews();
                                break;

                        }
                    }
                });
        builder.create().show();

        return false;
    }
}