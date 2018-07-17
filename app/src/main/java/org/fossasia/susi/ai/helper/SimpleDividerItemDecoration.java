package org.fossasia.susi.ai.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.susi.ai.R;

/**
 * Created by arundhati24 16/07/2018.
 */
public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;
    private int drawDividerFromIndex;

    public SimpleDividerItemDecoration(Context context, int drawDividerFromIndex) {
        divider = context.getResources().getDrawable(R.drawable.rv_divider);
        this.drawDividerFromIndex = drawDividerFromIndex;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = drawDividerFromIndex; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }
}