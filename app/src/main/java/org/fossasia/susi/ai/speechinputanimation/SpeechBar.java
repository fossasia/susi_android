package org.fossasia.susi.ai.speechinputanimation;

import android.graphics.RectF;

public class SpeechBar {

    private int x;
    private int y;
    private int radius;
    private int height;

    private final int maxHeight;
    private final int startX;
    private final int startY;
    final private RectF rect;

    public SpeechBar(int x, int y, int height, int maxHeight, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.startX = x;
        this.startY = y;
        this.height = height;
        this.maxHeight = maxHeight;
        this.rect = new RectF(x - radius,
                y - height / 2,
                x + radius,
                y + height / 2);
    }

    public void update() {
        rect.set(x - radius,
                y - height / 2,
                x + radius,
                y + height / 2);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public RectF getRect() {
        return rect;
    }

    public int getRadius() {
        return radius;
    }
}
