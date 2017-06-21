package org.fossasia.susi.ai.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * <h1>Message view holder</h1>
 *
 * Created by better_clever on 18/10/16.
 */
public abstract class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    private ClickListener listener;

    /**
     * Instantiates a new Message view holder.
     *
     * @param itemView      the item view
     * @param clickListener the click listener
     */
    public MessageViewHolder(View itemView , ClickListener clickListener) {
        super(itemView);
        this.listener = clickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onItemClicked(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return listener != null && listener.onItemLongClicked(getAdapterPosition());
    }

    /**
     * The interface Click listener.
     */
    public interface ClickListener {
        /**
         * On item clicked.
         *
         * @param position the position
         */
        void onItemClicked(int position);

        /**
         * On item long clicked boolean.
         *
         * @param position the position
         * @return the boolean
         */
        boolean onItemLongClicked(int position);
    }
}
