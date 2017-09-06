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

import android.graphics.Point;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public class RotatingAnimator implements BarParamsAnimator {

    private static final long DURATION = 1000;
    private static final long ACCELERATE_ROTATION_DURATION = 500;
    private static final long DECELERATE_ROTATION_DURATION = 500;
    private static final float ROTATION_DEGREES = 720f;
    private static final float ACCELERATION_ROTATION_DEGREES = 40f;

    private long startTimestamp;
    private boolean isPlaying;

    private final int centerX, centerY;
    private final List<Point> startPositions;
    private final List<SpeechBar> bars;

    public RotatingAnimator(List<SpeechBar> bars, int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.bars = bars;
        this.startPositions = new ArrayList<>();
        for (SpeechBar bar : bars) {
            startPositions.add(new Point(bar.getX(), bar.getY()));
        }
    }

    @Override
    public void start() {
        isPlaying = true;
        startTimestamp = System.currentTimeMillis();
    }

    @Override
    public void stop() {
        isPlaying = false;
    }

    @Override
    public void animate() {
        if (!isPlaying) return;

        long currTimestamp = System.currentTimeMillis();
        if (currTimestamp - startTimestamp > DURATION) {
            startTimestamp += DURATION;
        }

        long delta = currTimestamp - startTimestamp;

        float interpolatedTime = (float) delta / DURATION;

        float angle = interpolatedTime * ROTATION_DEGREES;

        int i = 0;
        for (SpeechBar bar : bars) {
            float finalAngle = angle;
            if (i > 0 && delta > ACCELERATE_ROTATION_DURATION) {
                finalAngle += decelerate(delta, bars.size() - i);
            } else if (i > 0) {
                finalAngle += accelerate(delta, bars.size() - i);
            }
            rotate(bar, finalAngle, startPositions.get(i));
            i++;
        }
    }

    private float decelerate(long delta, int scale) {
        long accelerationDelta = delta - ACCELERATE_ROTATION_DURATION;
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        float interpolatedTime = interpolator.getInterpolation((float) accelerationDelta / DECELERATE_ROTATION_DURATION);
        float decelerationAngle = -interpolatedTime * (ACCELERATION_ROTATION_DEGREES * scale);
        return ACCELERATION_ROTATION_DEGREES * scale + decelerationAngle;
    }

    private float accelerate(long delta, int scale) {
        long accelerationDelta = delta;
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        float interpolatedTime = interpolator.getInterpolation((float) accelerationDelta / ACCELERATE_ROTATION_DURATION);
        float accelerationAngle = interpolatedTime * (ACCELERATION_ROTATION_DEGREES * scale);
        return accelerationAngle;
    }

    /**
     * X = x0 + (x - x0) * cos(a) - (y - y0) * sin(a);
     * Y = y0 + (y - y0) * cos(a) + (x - x0) * sin(a);
     */
    private void rotate(SpeechBar bar, double degrees, Point startPosition) {

        double angle = Math.toRadians(degrees);

        int x = centerX + (int) ((startPosition.x - centerX) * Math.cos(angle) -
                (startPosition.y - centerY) * Math.sin(angle));

        int y = centerY + (int) ((startPosition.x - centerX) * Math.sin(angle) +
                (startPosition.y - centerY) * Math.cos(angle));

        bar.setX(x);
        bar.setY(y);
        bar.update();
    }
}
