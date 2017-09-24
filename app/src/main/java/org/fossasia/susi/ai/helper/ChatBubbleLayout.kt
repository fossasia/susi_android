package org.fossasia.susi.ai.helper

import android.annotation.TargetApi
import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

/**
 * <h1>Helper class for defining layout of chat view holder</h1>

 * Created by betterclever on 18/12/16.
 */
class ChatBubbleLayout : FrameLayout {

    /**
     * Instantiates a new Chat bubble layout.

     * @param context the context
     */
    constructor(context: Context) : super(context) {}

    /**
     * Instantiates a new Chat bubble layout.

     * @param context the context
     * *
     * @param attrs   the attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    /**
     * Instantiates a new Chat bubble layout.

     * @param context      the context
     * *
     * @param attrs        the attrs
     * *
     * @param defStyleAttr the def style attr
     */
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    /**
     * Instantiates a new Chat bubble layout.

     * @param context      the context
     * *
     * @param attrs        the attrs
     * *
     * @param defStyleAttr the def style attr
     * *
     * @param defStyleRes  the def style res
     */
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val childTextView = getChildAt(0) as TextView
        val childDateView = getChildAt(1)

        val view_width = View.MeasureSpec.getSize(widthMeasureSpec)

        val lineCount = childTextView.lineCount

        val dateViewHeight = childDateView.measuredHeight

        val dateViewWidth = childDateView.measuredWidth

        val textViewPadding = childTextView.paddingLeft + childTextView.paddingRight

        val lastLineStart = childTextView.layout.getLineStart(lineCount - 1)
        val lastLineEnd = childTextView.layout.getLineEnd(lineCount - 1)

        val lastLineWidth = Layout.getDesiredWidth(childTextView.text.subSequence(lastLineStart,
                lastLineEnd), childTextView.paint).toInt()

        var finalFramelayoutWidth = 0
        var finalFrameLayoutHeight = 0
        val viewPaddingLeftNRight = paddingLeft + paddingRight
        val finalFrameLayoutRequiredWidth = lastLineWidth + textViewPadding + dateViewWidth + viewPaddingLeftNRight

        val lineHeight = childTextView.measuredHeight / lineCount / 2
        val bottomMargin = lineHeight - dateViewHeight / 2

        if (childTextView.measuredWidth + viewPaddingLeftNRight >= view_width || finalFrameLayoutRequiredWidth >= view_width) {
            finalFramelayoutWidth = view_width
            finalFrameLayoutHeight = measuredHeight
            if (finalFrameLayoutRequiredWidth >= view_width) {
                finalFrameLayoutHeight += dateViewHeight
                finalFramelayoutWidth = childTextView.measuredWidth + viewPaddingLeftNRight

                (childDateView.layoutParams as FrameLayout.LayoutParams).bottomMargin = 0
            } else {
                (childDateView.layoutParams as FrameLayout.LayoutParams).bottomMargin = bottomMargin
            }

        } else {
            finalFramelayoutWidth = Math.max(finalFrameLayoutRequiredWidth,
                    childTextView.measuredWidth + viewPaddingLeftNRight)
            finalFrameLayoutHeight = measuredHeight
            (childDateView.layoutParams as FrameLayout.LayoutParams).bottomMargin = bottomMargin
        }

        if (finalFramelayoutWidth > view_width)
            finalFramelayoutWidth = view_width

        setMeasuredDimension(finalFramelayoutWidth, finalFrameLayoutHeight)
    }
}
