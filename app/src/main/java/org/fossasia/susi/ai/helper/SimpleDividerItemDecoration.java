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
    private Drawable mDivider;
    private int mDrawDividerFromIndex;

    public SimpleDividerItemDecoration(Context context, int drawDividerFromIndex) {
        mDivider = context.getResources().getDrawable(R.drawable.rv_divider);
        mDrawDividerFromIndex = drawDividerFromIndex;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = mDrawDividerFromIndex; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}