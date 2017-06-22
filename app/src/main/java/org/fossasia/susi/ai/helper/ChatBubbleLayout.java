package org.fossasia.susi.ai.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * <h1>Helper class for defining layout of chat view holder</h1>
 *
 * Created by betterclever on 18/12/16.
 */
public class ChatBubbleLayout extends FrameLayout {

    /**
     * Instantiates a new Chat bubble layout.
     *
     * @param context the context
     */
    public ChatBubbleLayout(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Chat bubble layout.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ChatBubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new Chat bubble layout.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ChatBubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Instantiates a new Chat bubble layout.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     * @param defStyleRes  the def style res
     */
    @TargetApi(21)
    public ChatBubbleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        TextView childTextView = (TextView) getChildAt(0);
        View childDateView = getChildAt(1);

        int view_width = View.MeasureSpec.getSize(widthMeasureSpec);

        int lineCount = childTextView.getLineCount();

        int dateViewHeight = childDateView.getMeasuredHeight();

        int dateViewWidth = childDateView.getMeasuredWidth();

        int textViewPadding = childTextView.getPaddingLeft() + childTextView.getPaddingRight();

        int lastLineStart = childTextView.getLayout().getLineStart(lineCount - 1);
        int lastLineEnd = childTextView.getLayout().getLineEnd(lineCount - 1);

        int lastLineWidth = (int) Layout.getDesiredWidth(childTextView.getText().subSequence(lastLineStart,
                lastLineEnd), childTextView.getPaint());

        int finalFramelayoutWidth = 0;
        int finalFrameLayoutHeight = 0;
        int viewPaddingLeftNRight = getPaddingLeft() + getPaddingRight();
        int finalFrameLayoutRequiredWidth = lastLineWidth + textViewPadding + dateViewWidth  + viewPaddingLeftNRight;

        int lineHeight = (childTextView.getMeasuredHeight() / lineCount) / 2;
        int bottomMargin = lineHeight - (dateViewHeight/2);

        if(( (childTextView.getMeasuredWidth() + viewPaddingLeftNRight)>= view_width)
                || (finalFrameLayoutRequiredWidth >= view_width) ) {
            finalFramelayoutWidth = view_width;
            finalFrameLayoutHeight = getMeasuredHeight();
            if((finalFrameLayoutRequiredWidth >= view_width) ) {
                finalFrameLayoutHeight += dateViewHeight;
                finalFramelayoutWidth = childTextView.getMeasuredWidth() + viewPaddingLeftNRight;

                ((LayoutParams)childDateView.getLayoutParams()).bottomMargin = 0;
            } else {
                ((LayoutParams)childDateView.getLayoutParams()).bottomMargin = bottomMargin;
            }

        } else {
            finalFramelayoutWidth = Math.max(finalFrameLayoutRequiredWidth,
                    childTextView.getMeasuredWidth() + viewPaddingLeftNRight);
            finalFrameLayoutHeight = getMeasuredHeight();
            ((LayoutParams)childDateView.getLayoutParams()).bottomMargin = bottomMargin;
        }

        if(finalFramelayoutWidth > view_width)
            finalFramelayoutWidth = view_width;

        setMeasuredDimension(finalFramelayoutWidth, finalFrameLayoutHeight);
    }
}
