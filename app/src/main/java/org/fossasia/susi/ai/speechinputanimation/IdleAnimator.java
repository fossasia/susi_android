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

import java.util.List;

public class IdleAnimator implements BarParamsAnimator {

    private static final long IDLE_DURATION = 1000;

    private long startTimestamp;
    private boolean isPlaying;

    private final int floatingAmplitude;
    private final List<SpeechBar> bars;

    public IdleAnimator(List<SpeechBar> bars, int floatingAmplitude) {
        this.floatingAmplitude = floatingAmplitude;
        this.bars = bars;
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
        if (isPlaying) {
            update(bars);
        }
    }

    public void update(List<SpeechBar> bars) {

        long currTimestamp = System.currentTimeMillis();
        if (currTimestamp - startTimestamp > IDLE_DURATION) {
            startTimestamp += IDLE_DURATION;
        }
        long delta = currTimestamp - startTimestamp;

        int i = 0;
        for (SpeechBar bar : bars) {
            updateCirclePosition(bar, delta, i);
            i++;
        }
    }

    private void updateCirclePosition(SpeechBar bar, long delta, int num) {
        float angle = ((float) delta / IDLE_DURATION) * 360f + 120f * num;
        int y = (int) (Math.sin(Math.toRadians(angle)) * floatingAmplitude) + bar.getStartY();
        bar.setY(y);
        bar.update();
    }
}
