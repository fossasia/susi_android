package org.fossasia.susi.ai.adapters.viewHolders;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.susi.ai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 Piyush Gupta on
 10/14/16 at
 12:59 PM
 */

public class MapViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
    public  MapViewHolder mapViewHolder;
    private String TAG = MapViewHolder.class.getSimpleName();

    @BindView(R.id.text)
    public TextView text;
    @BindView(R.id.timestamp)
    public TextView timestampTextView;
    @BindView(R.id.map_image)
    public ImageView mapImage;
    @BindView(R.id.chatMessageView)
    public ChatMessageView chatMessages;


    public MapViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnLongClickListener(this);
    }
    private void setClipboard(Context context, String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

    }


    @Override
    public boolean onLongClick(final View v) {
        final Context context=v.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Message");
        builder.setItems(new CharSequence[]
                        {"1. Copy Text", "2. Delete"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                String str = mapViewHolder.text.getText().toString();
                                setClipboard(context,str);
                                Snackbar.make(v, "Copied", Snackbar.LENGTH_LONG).show();
                                break;
                            case 1:
                                mapViewHolder.chatMessages.removeAllViews();
                                break;

                        }
                    }
                });
        builder.create().show();
        mapViewHolder=null;
        Log.e(TAG, "onLongClick: "+"mapViewHolder" );
        return false;
    }
}
