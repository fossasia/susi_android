package org.fossasia.susi.ai.speechinputanimation;

import java.util.ArrayList;
import java.util.List;

import org.fossasia.susi.ai.speechinputanimation.SpeechBar;

public class RmsAnimator implements BarParamsAnimator {
    final private List<BarRmsAnimator> barAnimators;


    public RmsAnimator(List<SpeechBar> speechBars) {
        this.barAnimators = new ArrayList<>();
        for (SpeechBar bar : speechBars) {
            barAnimators.add(new BarRmsAnimator(bar));
        }
    }

    @Override
    public void start() {
        for (BarRmsAnimator barAnimator : barAnimators) {
            barAnimator.start();
        }
    }

    @Override
    public void stop() {
        for (BarRmsAnimator barAnimator : barAnimators) {
            barAnimator.stop();
        }
    }

    @Override
    public void animate() {
        for (BarRmsAnimator barAnimator : barAnimators) {
            barAnimator.animate();
        }
    }

    public void onRmsChanged(float rmsDB) {
        for (BarRmsAnimator barAnimator : barAnimators) {
            barAnimator.onRmsChanged(rmsDB);
        }
    }
}
