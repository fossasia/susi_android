package org.fossasia.susi.ai.speechinputanimation;

/*
 * Copyright (C) 2016 Evgenii Zagumennyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
