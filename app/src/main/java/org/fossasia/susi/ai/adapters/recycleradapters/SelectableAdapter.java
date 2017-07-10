package org.fossasia.susi.ai.adapters.recycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import org.fossasia.susi.ai.data.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * <h1>Adapter used for selecting views on long click.</h1>
 *
 * Created by better_clever on 18/10/16.
 */
public abstract class SelectableAdapter extends RealmRecyclerViewAdapter<ChatMessage, RecyclerView.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = SelectableAdapter.class.getSimpleName();

    private SparseBooleanArray selectedItems;

    /**
     * Instantiates a new Selectable adapter.
     *
     * @param context    the context
     * @param data       the data
     * @param autoUpdate the auto update
     */
    public SelectableAdapter(Context context, OrderedRealmCollection<ChatMessage> data, boolean autoUpdate) {
        super(context,data,autoUpdate);
        selectedItems = new SparseBooleanArray();
    }

    /**
     * Is selected boolean.
     *
     * @param position the position
     * @return the boolean
     */
    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    /**
     * Toggle selection.
     *
     * @param position the position
     */
    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    /**
     * Clear selection.
     */
    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for (int i : selection) {
            notifyItemChanged(i);
        }
    }

    /**
     * Gets selected item count.
     *
     * @return the selected item count
     */
    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    /**
     * Gets selected items.
     *
     * @return the selected items
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
}
