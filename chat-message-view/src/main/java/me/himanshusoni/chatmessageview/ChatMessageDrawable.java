package me.himanshusoni.chatmessageview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessageDrawable extends Drawable {
    private final Paint mPaint;
    private final RectF mBoundsF;
    private final Rect mBoundsI;
    private float mRadius;
    private float mPadding;
    private boolean mInsetForPadding;
    private boolean mInsetForRadius = true;

    public ChatMessageDrawable(int backgroundColor, float radius) {
        mRadius = radius;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(backgroundColor);
        mBoundsF = new RectF();
        mBoundsI = new Rect();
    }

    void setPadding(float padding, boolean insetForPadding, boolean insetForRadius) {
        if (padding == mPadding && mInsetForPadding == insetForPadding &&
                mInsetForRadius == insetForRadius) {
            return;
        }
        mPadding = padding;
        mInsetForPadding = insetForPadding;
        mInsetForRadius = insetForRadius;
        updateBounds(null);
        invalidateSelf();
    }

    float getPadding() {
        return mPadding;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(mBoundsF, mRadius, mRadius, mPaint);
    }

    private void updateBounds(Rect bounds) {
        if (bounds == null) {
            bounds = getBounds();
        }
        mBoundsF.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
        mBoundsI.set(bounds);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateBounds(bounds);
    }

    @Override
    public void getOutline(Outline outline) {
        outline.setRoundRect(mBoundsI, mRadius);
    }

    @Override
    public void setAlpha(int alpha) {
        // not supported because older versions do not support
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // not supported because older versions do not support
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public float getRadius() {
        return mRadius;
    }

    void setRadius(float radius) {
        if (radius == mRadius) {
            return;
        }
        mRadius = radius;
        updateBounds(null);
        invalidateSelf();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidateSelf();
    }

    public Paint getPaint() {
        return mPaint;
    }
}