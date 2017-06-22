package org.fossasia.susi.ai.helper;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


/**
 * Created by mayanktripathi on 22/06/17.
 */

public class ConstraintsHelper extends RecyclerView.ItemDecoration{

    private static final String TAG = "ConstraintsHelper";
    private int space;

    public ConstraintsHelper(int dimension, Context context) {

        space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimension,
                context.getResources().getDisplayMetrics());
        Log.d(TAG, " space: " + space);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(parent.getChildAdapterPosition(view) == state.getItemCount()-1 && !(outRect.right >= space)){
            outRect.right = space;
            outRect.left = 0;
        }
    }
}
