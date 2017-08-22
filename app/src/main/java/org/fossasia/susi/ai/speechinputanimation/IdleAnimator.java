package org.fossasia.susi.ai.speechinputanimation;

import java.util.List;

import org.fossasia.susi.ai.speechinputanimation.SpeechBar;

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
