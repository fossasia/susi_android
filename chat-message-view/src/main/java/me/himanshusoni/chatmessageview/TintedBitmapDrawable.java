package me.himanshusoni.chatmessageview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by himanshusoni on 06/09/15.
 */
public final class TintedBitmapDrawable extends BitmapDrawable {
    private int tint;
    private int alpha;

    public TintedBitmapDrawable(final Resources res, final Bitmap bitmap, final int tint) {
        super(res, bitmap);
        this.tint = tint;
        this.alpha = Color.alpha(tint);
    }

    public TintedBitmapDrawable(final Resources res, final int resId, final int tint) {
        super(res, BitmapFactory.decodeResource(res, resId));
        this.tint = tint;
        this.alpha = Color.alpha(tint);
    }

    public void setTint(final int tint) {
        this.tint = tint;
        this.alpha = Color.alpha(tint);

        invalidateSelf();
    }

    @Override
    public void draw(final Canvas canvas) {
        final Paint paint = getPaint();
        if (paint.getColorFilter() == null) {
            paint.setColorFilter(new LightingColorFilter(tint, 0));
            paint.setAlpha(alpha);
        }
        super.draw(canvas);
    }
}